package com.example.demo;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/alcohol")
public class AlcoholCalculatorController {

    @PostMapping("/calculate")
    public AlcoholCalculationResponse calculate(
            @RequestBody AlcoholCalculationRequest request
    ) {
        // Коэффициент распределения Видмарка
        double r = request.getGender() ? 0.7 : 0.6;

        // Расчёт массы чистого спирта (граммы)
        double adjustedPromille = request.getDesiredPromille() - request.getPersonalConst();
        double pureAlcoholGrams = Math.max(0, adjustedPromille) * request.getWeight() * r;

        // Рассчёт эквивалентов напитков
        // TODO вынести в БД?
        List<DrinkEquivalent> equivalents = Arrays.asList(
                calculateDrink("Пиво", 5.0, pureAlcoholGrams),
                calculateDrink("Вино", 12.0, pureAlcoholGrams),
                calculateDrink("Водка", 40.0, pureAlcoholGrams)
        );

        double roundedAlcohol = Math.round(pureAlcoholGrams * 10) / 10.0;

        return new AlcoholCalculationResponse(roundedAlcohol, equivalents);
    }

    private DrinkEquivalent calculateDrink(String name, double strength, double alcoholGrams) {
        double ethanolDensity = 0.78924; // г/мл
        double volumeMl = (alcoholGrams * 100) / (strength * ethanolDensity);
        return new DrinkEquivalent(name, Math.round(volumeMl * 10.0) / 10.0); // округление до 0.1
    }
}