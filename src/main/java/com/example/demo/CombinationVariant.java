package com.example.demo;

import lombok.Data;

import java.util.List;

@Data
public class CombinationVariant {
    private List<DrinkEquivalent> drinks;

    public CombinationVariant(List<DrinkEquivalent> drinks) {
        this.drinks = drinks;
    }
}