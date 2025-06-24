package com.example.demo.service;

import com.example.demo.CombinationVariant;
import com.example.demo.dto.AlcoholCalculationRequest;
import com.example.demo.dto.AlcoholCalculationResponse;
import com.example.demo.Constants;
import com.example.demo.DrinkEquivalent;
import com.example.demo.dto.Drink;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AlcoholCalculationService {

    public AlcoholCalculationResponse calculate(AlcoholCalculationRequest request) {
        // 1. Расчёт ИМТ
        double heightMeters = request.getHeight() / 100;
        double bmi = request.getWeight() / (heightMeters * heightMeters);

        // 2. Поправочный коэффициент на основе ИМТ
        double bmiFactor = calculateBmiFactor(bmi);

        // 3. Коэффициент Видмарка
        double r = "MALE".equals(request.getGender()) ?
                Constants.MALE_COEFFICIENT :
                Constants.FEMALE_COEFFICIENT;

        // 4. Коэффициент сытости
        double satietyCoeff = switch (request.getSatiety()) {
            case HUNGRY -> Constants.HUNGRY_COEFF;
            case NORMAL -> Constants.NORMAL_COEFF;
            case FULL   -> Constants.FULL_COEFF;
        };

        // 5. Расчёт массы чистого спирта с учётом всех коэффициентов
        double adjustedPromille = request.getDesiredPromille() - request.getPersonalConst();
        double pureAlcoholGrams = Math.max(0, adjustedPromille)
                * request.getWeight()
                * r
                * satietyCoeff
                * bmiFactor;

        // 6. Округление и расчёт эквивалентов
        double roundedGrams = Math.round(pureAlcoholGrams * 10) / 10.0;
        List<CombinationVariant> variants = generateUniqueCombinationVariants(
                roundedGrams,
                request.getDrinks(),
                Constants.MAX_VARIANTS
        );

        return new AlcoholCalculationResponse(roundedGrams, variants);
    }

    private List<CombinationVariant> generateUniqueCombinationVariants(
            double alcoholGrams,
            List<Drink> drinks,
            int maxVariants
    ) {
        // Генерируем все возможные уникальные комбинации
        Set<Set<String>> seenCombinations = new HashSet<>();
        List<CombinationVariant> variants = new ArrayList<>();

        // Создаем все возможные комбинации напитков
        for (int i = 1; i <= Math.min(Constants.MAX_DRINKS_PER_VARIANT, drinks.size()); i++) {
            generateCombinations(
                    variants,
                    seenCombinations,
                    new ArrayList<>(),
                    drinks,
                    0,
                    i,
                    alcoholGrams
            );

            // Останавливаемся если набрали нужное количество
            if (variants.size() >= maxVariants) break;
        }

        // Если комбинаций меньше нужного, добавляем одиночные
        if (variants.size() < maxVariants) {
            for (Drink drink : drinks) {
                if (variants.size() >= maxVariants) break;

                Set<String> key = Set.of(drink.getDrinkName());
                if (!seenCombinations.contains(key)) {
                    variants.add(createVariant(alcoholGrams, List.of(drink)));
                    seenCombinations.add(key);
                }
            }
        }

        return variants.stream().limit(maxVariants).collect(Collectors.toList());
    }

    private void generateCombinations(
            List<CombinationVariant> variants,
            Set<Set<String>> seenCombinations,
            List<Drink> current,
            List<Drink> drinks,
            int start,
            int k,
            double alcoholGrams
    ) {
        if (k == 0) {
            // Создаем ключ для проверки уникальности
            Set<String> key = current.stream()
                    .map(Drink::getDrinkName)
                    .collect(Collectors.toSet());

            if (!seenCombinations.contains(key)) {
                variants.add(createVariant(alcoholGrams, new ArrayList<>(current)));
                seenCombinations.add(key);
            }
            return;
        }

        for (int i = start; i < drinks.size(); i++) {
            current.add(drinks.get(i));
            generateCombinations(variants, seenCombinations, current, drinks, i + 1, k - 1, alcoholGrams);
            current.remove(current.size() - 1);
        }
    }

    private CombinationVariant createVariant(double alcoholGrams, List<Drink> drinks) {
        // Равномерное распределение алкоголя между напитками
        double portion = alcoholGrams / drinks.size();
        List<DrinkEquivalent> equivalents = drinks.stream()
                .map(drink -> {
                    double ml = (portion * 100) / (drink.getDrinkValue() * Constants.ETHANOL_DENSITY);
                    return new DrinkEquivalent(drink.getDrinkName(), Math.round(ml * 10.0) / 10.0);
                })
                .collect(Collectors.toList());

        return new CombinationVariant(equivalents);
    }

    private double calculateBmiFactor(double bmi) {
        if (bmi < Constants.UNDERWEIGHT_BMI) {
            return 0.9;   // Недостаточный вес
        } else if (bmi > Constants.OVERWEIGHT_BMI) {
            return 1.1;  // Избыточный вес
        }
        return 1.0;      // Нормальный вес
    }


    private CombinationVariant generateVariant(double alcoholGrams, List<Drink> drinks) {
        // Случайное количество напитков в комбинации (1-4)
        int drinkCount = ThreadLocalRandom.current().nextInt(1,
                Math.min(Constants.MAX_DRINKS_PER_VARIANT, drinks.size()) + 1);

        // Перемешиваем напитки
        List<Drink> shuffledDrinks = new ArrayList<>(drinks);
        Collections.shuffle(shuffledDrinks);

        // Берём первые N напитков
        List<Drink> selectedDrinks = shuffledDrinks.subList(0, drinkCount);

        // Генерируем случайные пропорции
        double[] proportions = generateRandomProportions(drinkCount);

        // Рассчитываем объёмы для каждого напитка
        List<DrinkEquivalent> equivalents = new ArrayList<>();
        for (int i = 0; i < drinkCount; i++) {
            double share = proportions[i];
            double drinkAlcohol = alcoholGrams * share;
            double ml = calculateDrinkVolume(
                    selectedDrinks.get(i).getDrinkValue(),
                    drinkAlcohol
            );
            equivalents.add(new DrinkEquivalent(
                    selectedDrinks.get(i).getDrinkName(),
                    ml
            ));
        }

        return new CombinationVariant(equivalents);
    }

    private double[] generateRandomProportions(int count) {
        double[] proportions = new double[count];
        double total = 0;

        // Генерируем случайные значения
        for (int i = 0; i < count; i++) {
            proportions[i] = ThreadLocalRandom.current().nextDouble(0.1, 1.0);
            total += proportions[i];
        }

        // Нормализуем пропорции
        for (int i = 0; i < count; i++) {
            proportions[i] /= total;
        }

        return proportions;
    }

    private double calculateDrinkVolume(double strength, double alcoholGrams) {
        double ml = (alcoholGrams * 100) / (strength * Constants.ETHANOL_DENSITY);
        return Math.round(ml * 10.0) / 10.0; // Округление до 0.1 мл
    }
}