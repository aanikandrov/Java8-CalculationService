package com.example.demo.dto;

import com.example.demo.CombinationVariant;
import com.example.demo.AlcoholEquivalent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlcoholCalculationResponse {

    private double pureAlcoholGrams;
    private List<CombinationVariant> variants;

    public AlcoholCalculationResponse(double pureAlcoholGrams, List<CombinationVariant> variants) {
        this.pureAlcoholGrams = pureAlcoholGrams;
        this.variants = variants;
    }
}