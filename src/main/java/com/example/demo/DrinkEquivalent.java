package com.example.demo;

public class DrinkEquivalent {
    private String drinkName;
    private double ml; // это миллилитры, а не машинЛёрнинг :(

    public DrinkEquivalent(String drinkName, double ml) {
        this.drinkName = drinkName;
        this.ml = ml;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public double getMl() {
        return ml;
    }

    public void setMl(double ml) {
        this.ml = ml;
    }
}
