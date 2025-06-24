package com.example.demo.dto;

public class Drink {
    private String drinkName;
    private double drinkValue; // крепость напитка в %

    public Drink() {}

    public Drink(String drinkName, double drinkValue) {
        this.drinkName = drinkName;
        this.drinkValue = drinkValue;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public double getDrinkValue() {
        return drinkValue;
    }

    public void setDrinkValue(double drinkValue) {
        this.drinkValue = drinkValue;
    }
}