package com.example.demo;

public class Constants {

    // Количество возвращаемых вариантов
    static final public int MAX_VARIANTS = 7;

    // Макс напитков в комбинации
    static final public int MAX_DRINKS_PER_VARIANT = 3;

    // Скорость вывода алкоголя (промилле в час)
    static final public double ALCOHOL_ELIMINATION_RATE = 0.15;

    // Коэффициенты для формулы Видмарка
    static final public double MALE_COEFFICIENT = 0.7;
    static final public double FEMALE_COEFFICIENT = 0.6;

    // Плотность этанола (г/мл)
    static final public double ETHANOL_DENSITY = 0.78924;

    // Коэффициенты сытости
    static final public double HUNGRY_COEFF = 1.0;
    static final public double NORMAL_COEFF = 0.8;
    static final public double FULL_COEFF = 0.6;

    // ИМТ-границы
    static final public double UNDERWEIGHT_BMI = 18.5;
    static final public double OVERWEIGHT_BMI = 25.0;
}
