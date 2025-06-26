package com.example.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlcoholEquivalent {
    private Long alcoholId;
    private String name;
    private double ml; // это миллилитры, а не машинЛёрнинг :(

    public AlcoholEquivalent(Long alcoholId, String name, double roundedMl) {
        this.alcoholId = alcoholId;
        this.name = name;
        this.ml = roundedMl;
    }
}