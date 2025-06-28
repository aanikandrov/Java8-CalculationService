package com.example.demo.service;

import com.example.demo.entity.Alcohol;
import com.example.demo.entity.User;
import com.example.demo.utils.AlcoholEquivalent;
import com.example.demo.utils.CombinationVariant;
import com.example.demo.constants.Constants;
import com.example.demo.dto.AlcoholCalculationRequest;
import com.example.demo.dto.AlcoholCalculationResponse;
import com.example.demo.repository.AlcoholRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlcoholCalculationService {
    private final AlcoholRepository alcoholRepository;
    private final UserRepository userRepository;

    public AlcoholCalculationResponse calculate(AlcoholCalculationRequest request) {
        log.info("Starting calculation for user ID: {}", request.getUserId());

        // Получаем доступные напитки
        List<Alcohol> basicDrinks = alcoholRepository.findByIsBasicTrue();
        List<Alcohol> userSavedDrinks = alcoholRepository.findBySavedByUsersUserId(request.getUserId());

        // Объединяем списки, исключая дубликаты
        Set<Alcohol> allDrinks = new HashSet<>();
        allDrinks.addAll(basicDrinks);
        allDrinks.addAll(userSavedDrinks);

        List<Alcohol> availableDrinks = new ArrayList<>(allDrinks);

        // Создаем хэшмапу для быстрого доступа к крепости напитка по имени
        Map<String, Double> drinkStrengthMap = availableDrinks.stream()
                .collect(Collectors.toMap(Alcohol::getName, Alcohol::getDegree));

        log.info("Found {} available drinks for user: {}", availableDrinks.size(),
                availableDrinks.stream().map(Alcohol::getName).collect(Collectors.joining(", ")));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", request.getUserId());
                    return new EntityNotFoundException("User not found with id: " + request.getUserId());
                });

        // 1. Расчёт ИМТ
        double heightMeters = request.getHeight() / 100;
        double bmi = request.getWeight() / (heightMeters * heightMeters);
        log.debug("BMI calculated: {}", bmi);

        // 2. Поправочный коэффициент на основе ИМТ
        double bmiFactor = calculateBmiFactor(bmi);
        log.debug("BMI factor: {}", bmiFactor);

        // 3. Коэффициент Видмарка
        double r = "MALE".equals(request.getGender()) ?
                Constants.MALE_COEFFICIENT :
                Constants.FEMALE_COEFFICIENT;
        log.debug("Widmark coefficient (r): {}", r);

        // 4. Коэффициент сытости
        double satietyCoeff = switch (request.getSatiety()) {
            case HUNGRY -> Constants.HUNGRY_COEFF;
            case NORMAL -> Constants.NORMAL_COEFF;
            case FULL   -> Constants.FULL_COEFF;
        };
        log.debug("Satiety coefficient: {}", satietyCoeff);

        // 5. Расчёт массы чистого спирта
        double userConst = Optional.ofNullable(user.getPersonalConst()).orElse(0.0);
        double adjustedPromille = request.getDesiredPromille() + userConst;
        double pureAlcoholGrams = Math.max(0, adjustedPromille)
                * request.getWeight()
                * r
                * satietyCoeff
                * bmiFactor;
        log.info("Calculated pure alcohol grams: {}", pureAlcoholGrams);

        // 6. Округление и расчёт эквивалентов
        double roundedGrams = Math.round(pureAlcoholGrams * 10) / 10.0;
        log.info("Rounded alcohol grams: {}", roundedGrams);

        List<CombinationVariant> variants = generateUniqueCombinationVariants(
                roundedGrams,
                availableDrinks,
                Constants.MAX_VARIANTS,
                drinkStrengthMap  // Передаем карту крепостей
        );

        log.info("Generated {} combination variants:", variants.size());
        variants.forEach(v -> log.info("Variant: {}", v));

        return new AlcoholCalculationResponse(roundedGrams, variants);
    }

    private double roundToNiceValue(double ml) {
        if (ml < 10) {
            // Для маленьких объемов оставляем 1 знак после запятой
            return Math.round(ml * 10) / 10.0;
        } else if (ml < 50) {
            // Для средних объемов округляем до кратного 5 мл
            return Math.round(ml / 5) * 5;
        } else {
            // Для больших объемов округляем до кратного 10 мл
            return Math.round(ml / 10) * 10;
        }
    }

    private void compensateDifference(List<AlcoholEquivalent> equivalents, double diff,
                                      Map<String, Double> drinkStrengthMap) {
        if (equivalents.isEmpty()) return;

        // Находим напиток с максимальной крепостью для минимальной коррекции
        AlcoholEquivalent strongestDrink = null;
        double maxStrength = 0;
        for (AlcoholEquivalent eq : equivalents) {
            double strength = drinkStrengthMap.getOrDefault(eq.getName(), 40.0);
            if (strength > maxStrength) {
                maxStrength = strength;
                strongestDrink = eq;
            }
        }
        if (strongestDrink == null) return;

        double strength = maxStrength;
        double density = Constants.ETHANOL_DENSITY;

        // Рассчитываем необходимую коррекцию в мл
        double mlAdjustment = diff / ((strength / 100) * density);
        double newMl = strongestDrink.getMl() + mlAdjustment;

        // Округляем скорректированное значение
        strongestDrink.setMl(roundToNiceValue(newMl));
    }

    private CombinationVariant createVariant(double alcoholGrams, List<Alcohol> drinks,
                                             Map<String, Double> drinkStrengthMap) {
        log.debug("Creating variant with {}g alcohol for {} drinks", alcoholGrams, drinks.size());

        double portion = alcoholGrams / drinks.size();
        List<AlcoholEquivalent> equivalents = drinks.stream()
                .map(drink -> {
                    double exactMl = (portion * 100) / (drink.getDegree() * Constants.ETHANOL_DENSITY);
                    return new AlcoholEquivalent(drink.getAlcoholId(), drink.getName(), exactMl);
                })
                .collect(Collectors.toList());

        // Рассчитываем общее количество спирта для проверки
        double totalAlcohol = equivalents.stream()
                .mapToDouble(eq -> {
                    double strength = drinkStrengthMap.getOrDefault(eq.getName(), 40.0);
                    return eq.getMl() * (strength / 100) * Constants.ETHANOL_DENSITY;
                })
                .sum();

        // Округляем объемы до красивых значений
        equivalents.forEach(eq -> {
            double rounded = roundToNiceValue(eq.getMl());
            eq.setMl(rounded);
        });

        // Корректируем для сохранения общего количества спирта
        double newTotalAlcohol = equivalents.stream()
                .mapToDouble(eq -> {
                    double strength = drinkStrengthMap.getOrDefault(eq.getName(), 40.0);
                    return eq.getMl() * (strength / 100) * Constants.ETHANOL_DENSITY;
                })
                .sum();

        double diff = totalAlcohol - newTotalAlcohol;
        if (Math.abs(diff) > 0.1) {
            compensateDifference(equivalents, diff, drinkStrengthMap);
        }

        return new CombinationVariant(equivalents);
    }

    private void generateCombinations(
            List<CombinationVariant> variants,
            Set<Set<String>> seenCombinations,
            List<Alcohol> current,
            List<Alcohol> drinks,
            int start,
            int k,
            double alcoholGrams,
            Map<String, Double> drinkStrengthMap
    ) {
        if (k == 0) {
            Set<String> key = current.stream()
                    .map(Alcohol::getName)
                    .collect(Collectors.toSet());

            if (!seenCombinations.contains(key)) {
                log.debug("Adding new combination: {}", key);
                variants.add(createVariant(alcoholGrams, new ArrayList<>(current), drinkStrengthMap));
                seenCombinations.add(key);
            } else {
                log.debug("Combination already exists: {}", key);
            }
            return;
        }

        for (int i = start; i < drinks.size(); i++) {
            current.add(drinks.get(i));
            generateCombinations(variants, seenCombinations, current, drinks, i + 1, k - 1, alcoholGrams, drinkStrengthMap);
            current.remove(current.size() - 1);
        }
    }

    private List<CombinationVariant> generateUniqueCombinationVariants(
            double alcoholGrams,
            List<Alcohol> drinks,
            int maxVariants,
            Map<String, Double> drinkStrengthMap
    ) {
        log.info("Generating variants for {}g alcohol from {} drinks (max variants: {})",
                alcoholGrams, drinks.size(), maxVariants);

        if (drinks.isEmpty()) {
            log.warn("No drinks available for combination generation");
            return Collections.emptyList();
        }

        Set<Set<String>> seenCombinations = new HashSet<>();
        List<CombinationVariant> variants = new ArrayList<>();

        int maxDrinksPerVariant = Math.min(Constants.MAX_DRINKS_PER_VARIANT, drinks.size());
        log.debug("Max drinks per variant: {}", maxDrinksPerVariant);

        for (int i = 1; i <= maxDrinksPerVariant; i++) {
            log.debug("Generating combinations with {} drinks", i);
            generateCombinations(
                    variants,
                    seenCombinations,
                    new ArrayList<>(),
                    drinks,
                    0,
                    i,
                    alcoholGrams,
                    drinkStrengthMap
            );

            if (variants.size() >= maxVariants) {
                log.debug("Reached max variants limit ({})", maxVariants);
                break;
            }
        }

        log.info("Total variants generated: {}", variants.size());
        return variants.stream().limit(maxVariants).collect(Collectors.toList());
    }

    private double calculateBmiFactor(double bmi) {
        if (bmi < Constants.UNDERWEIGHT_BMI) {
            return 0.9;   // Недостаточный вес
        } else if (bmi > Constants.OVERWEIGHT_BMI) {
            return 1.1;  // Избыточный вес
        }
        return 1.0;      // Нормальный вес
    }
}
