package com.example.demo;

import java.util.List;

public class AlcoholCalculationResponse {

    private double pureAlcoholGrams;
    private List<DrinkEquivalent> equivalents;

    public AlcoholCalculationResponse(double pureAlcoholGrams, List<DrinkEquivalent> equivalents) {
        this.pureAlcoholGrams = pureAlcoholGrams;
        this.equivalents = equivalents;
    }

    public double getPureAlcoholGrams() {
        return pureAlcoholGrams;
    }

    public void setPureAlcoholGrams(double pureAlcoholGrams) {
        this.pureAlcoholGrams = pureAlcoholGrams;
    }

    public List<DrinkEquivalent> getEquivalents() {
        return equivalents;
    }

    public void setEquivalents(List<DrinkEquivalent> equivalents) {
        this.equivalents = equivalents;
    }
}
