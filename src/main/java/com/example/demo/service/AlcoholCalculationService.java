package com.example.demo.service;

import com.example.demo.CombinationVariant;
import com.example.demo.Constants;
import com.example.demo.DrinkEquivalent;
import com.example.demo.dto.AlcoholCalculationRequest;
import com.example.demo.dto.AlcoholCalculationResponse;
import com.example.demo.entity.Alcohol;
import com.example.demo.entity.User;
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
        double userConst = user.getPersonalConst() != null ? user.getPersonalConst() : 0.0;
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
                Constants.MAX_VARIANTS
        );

        log.info("Generated {} combination variants:", variants.size());
        variants.forEach(v -> log.info("Variant: {}", v));

        return new AlcoholCalculationResponse(roundedGrams, variants);
    }

    private CombinationVariant createVariant(double alcoholGrams, List<Alcohol> drinks) {
        log.debug("Creating variant with {}g alcohol for {} drinks", alcoholGrams, drinks.size());

        double portion = alcoholGrams / drinks.size();
        List<DrinkEquivalent> equivalents = drinks.stream()
                .map(drink -> {
                    double ml = (portion * 100) / (drink.getDegree() * Constants.ETHANOL_DENSITY);
                    double roundedMl = Math.round(ml * 10.0) / 10.0;
                    log.trace("Drink: {}, degree: {}, calculated ml: {} (rounded: {})",
                            drink.getName(), drink.getDegree(), ml, roundedMl);
                    return new DrinkEquivalent(drink.getName(), roundedMl);
                })
                .collect(Collectors.toList());

        return new CombinationVariant(equivalents);
    }

    private void generateCombinations(
            List<CombinationVariant> variants,
            Set<Set<String>> seenCombinations,
            List<Alcohol> current,
            List<Alcohol> drinks,
            int start,
            int k,
            double alcoholGrams
    ) {
        if (k == 0) {
            Set<String> key = current.stream()
                    .map(Alcohol::getName)
                    .collect(Collectors.toSet());

            if (!seenCombinations.contains(key)) {
                log.debug("Adding new combination: {}", key);
                variants.add(createVariant(alcoholGrams, new ArrayList<>(current)));
                seenCombinations.add(key);
            } else {
                log.debug("Combination already exists: {}", key);
            }
            return;
        }

        for (int i = start; i < drinks.size(); i++) {
            current.add(drinks.get(i));
            generateCombinations(variants, seenCombinations, current, drinks, i + 1, k - 1, alcoholGrams);
            current.remove(current.size() - 1);
        }
    }

    private List<CombinationVariant> generateUniqueCombinationVariants(
            double alcoholGrams,
            List<Alcohol> drinks,
            int maxVariants
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
                    alcoholGrams
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