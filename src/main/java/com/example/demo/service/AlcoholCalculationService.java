package com.example.demo.service;

import com.example.demo.dto.AlcoholCalculationRequest;
import com.example.demo.dto.AlcoholCalculationResponse;
import com.example.demo.Constants;
import com.example.demo.DrinkEquivalent;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

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
        List<DrinkEquivalent> equivalents = calculateEquivalents(roundedGrams);

        return new AlcoholCalculationResponse(roundedGrams, equivalents);
    }

    private double calculateBmiFactor(double bmi) {
        if (bmi < Constants.UNDERWEIGHT_BMI) {
            return 0.9;   // Недостаточный вес
        } else if (bmi > Constants.OVERWEIGHT_BMI) {
            return 1.1;  // Избыточный вес
        }
        return 1.0;      // Нормальный вес
    }

    // TODO вынести в БД
    private List<DrinkEquivalent> calculateEquivalents(double alcoholGrams) {
        return Arrays.asList(
                calculateDrink("Пиво", 5.0, alcoholGrams),
                calculateDrink("Вино", 12.0, alcoholGrams),
                calculateDrink("Водка", 40.0, alcoholGrams)
        );
    }

    private DrinkEquivalent calculateDrink(String name, double strength, double alcoholGrams) {
        double ml = (alcoholGrams * 100) / (strength * Constants.ETHANOL_DENSITY);
        return new DrinkEquivalent(name, Math.round(ml * 10.0) / 10.0);
    }
}