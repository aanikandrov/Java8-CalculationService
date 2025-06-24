package com.example.demo;

import java.util.List;

public class CombinationVariant {
    private List<DrinkEquivalent> drinks;

    public CombinationVariant(List<DrinkEquivalent> drinks) {
        this.drinks = drinks;
    }

    public List<DrinkEquivalent> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<DrinkEquivalent> drinks) {
        this.drinks = drinks;
    }
}