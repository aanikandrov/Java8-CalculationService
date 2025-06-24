package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class AlcoholCalculationService {

    public AlcoholCalculationResponse calculate(AlcoholCalculationRequest request) {
        // Коэффициент распределения Видмарка
        double r = "MALE".equals(request.getGender()) ?
                Constants.MALE_COEFFICIENT :
                Constants.FEMALE_COEFFICIENT;

        // Определение коэффициента сытости
        double satietyCoeff = switch (request.getSatiety()) {
            case HUNGRY -> Constants.HUNGRY_COEFF;
            case NORMAL -> Constants.NORMAL_COEFF;
            case FULL -> Constants.FULL_COEFF;
        };

        // Расчёт массы чистого спирта (граммы)
        double adjustedPromille = request.getDesiredPromille() - request.getPersonalConst();
        double pureAlcoholGrams = Math.max(0, adjustedPromille) *
                request.getWeight() *
                r *
                satietyCoeff;

        // TODO заменить на БД !!!
        // Рассчёт эквивалентов напитков
        List<DrinkEquivalent> equivalents = Arrays.asList(
                calculateDrink("Пиво", 5.0, pureAlcoholGrams),
                calculateDrink("Вино", 12.0, pureAlcoholGrams),
                calculateDrink("Водка", 40.0, pureAlcoholGrams)
        );

        double roundedAlcohol = Math.round(pureAlcoholGrams * 10) / 10.0;
        return new AlcoholCalculationResponse(roundedAlcohol, equivalents);
    }

    private DrinkEquivalent calculateDrink(String name, double strength, double alcoholGrams) {
        double ml = (alcoholGrams * 100) / (strength * Constants.ETHANOL_DENSITY);
        return new DrinkEquivalent(name, Math.round(ml * 10.0) / 10.0);
    }
}