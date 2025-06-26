package com.example.demo.dto;


import com.example.demo.Satiety;
import com.example.demo.entity.Alcohol;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlcoholCalculationRequest {
    private Long userId;

    private Double weight;          // Вес в кг

    private Integer age;                // Возраст

    private String gender;

    private Double height;          // Рост в см

    private Satiety satiety;

    private Double desiredPromille; // Желаемое промилле
}