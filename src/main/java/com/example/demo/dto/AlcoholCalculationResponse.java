package com.example.demo.dto;

import com.example.demo.CombinationVariant;

import java.util.List;

public class AlcoholCalculationResponse {

    private double pureAlcoholGrams;
    private List<CombinationVariant> variants;

    public AlcoholCalculationResponse(double pureAlcoholGrams, List<CombinationVariant> variants) {
        this.pureAlcoholGrams = pureAlcoholGrams;
        this.variants = variants;
    }

    public double getPureAlcoholGrams() {
        return pureAlcoholGrams;
    }

    public void setPureAlcoholGrams(double pureAlcoholGrams) {
        this.pureAlcoholGrams = pureAlcoholGrams;
    }

    public List<CombinationVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<CombinationVariant> variants) {
        this.variants = variants;
    }
}
