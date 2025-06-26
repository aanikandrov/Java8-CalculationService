package com.example.demo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrinkEquivalent {
    private String drinkName;
    private double ml; // это миллилитры, а не машинЛёрнинг :(

    public DrinkEquivalent(String drinkName, double ml) {
        this.drinkName = drinkName;
        this.ml = ml;
    }
}