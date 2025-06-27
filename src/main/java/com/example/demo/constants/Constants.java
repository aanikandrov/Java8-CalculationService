package com.example.demo.constants;

public class Constants {

    // Количество возвращаемых вариантов
    static final public int MAX_VARIANTS = 15;

    // Макс напитков в комбинации
    static final public int MAX_DRINKS_PER_VARIANT = 3;

    // Скорость вывода алкоголя (промилле в час)
    static final public double ALCOHOL_ELIMINATION_RATE = 0.15;

    // Коэффициенты для формулы Видмарка
    static public final double MALE_COEFFICIENT = 0.7;
    static public final double FEMALE_COEFFICIENT = 0.6;

    // Плотность этанола (г/мл)
    static public final double ETHANOL_DENSITY = 0.78924;

    // Коэффициенты сытости
    static public final double HUNGRY_COEFF = 1.0;
    static public final double NORMAL_COEFF = 0.8;
    static public final double FULL_COEFF = 0.6;

    // ИМТ-границы
    static public final double UNDERWEIGHT_BMI = 18.5;
    static public final double OVERWEIGHT_BMI = 25.0;
}