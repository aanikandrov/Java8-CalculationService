package com.example.demo;

import javax.validation.constraints.*;

public class AlcoholCalculationRequest {

    @Positive(message = "Вес должен быть положительным числом")
    private double weight;          // Вес в кг

    @Min(value = 18, message = "Минимальный возраст 18 лет")
    private int age;                // Возраст

    // к гендеру ограничений нет))
    private boolean gender;         // Пол (MALE/FEMALE)
    @Positive(message = "Рост должен быть положительным числом")
    private double height;          // Рост в см

    // TODO понять как учитывать
    private double personalConst;   // Персональная константа
    @DecimalMin(value = "0.0", message = "Промилле не может быть отрицательным")
    private double desiredPromille; // Желаемое промилле

    //    @Min(value = 0, message = "Количество часов не может быть отрицательным")
    //    private Integer hours;          // Часы до выведения алкоголя
    //    // null если не указано

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getPersonalConst() {
        return personalConst;
    }

    public void setPersonalConst(double personalConst) {
        this.personalConst = personalConst;
    }

    public double getDesiredPromille() {
        return desiredPromille;
    }

    public void setDesiredPromille(double desiredPromille) {
        this.desiredPromille = desiredPromille;
    }

//    public Integer getHours() {
//        return hours;
//    }
//
//    public void setHours(Integer hours) {
//        this.hours = hours;
//    }
}
