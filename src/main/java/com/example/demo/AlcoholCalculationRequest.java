package com.example.demo;

public class AlcoholCalculationRequest {
    private double weight;          // Вес в кг
    private int age;                // Возраст
    private boolean gender;          // Пол (MALE/FEMALE)
    private double height;          // Рост в см
    private double personalConst;   // Персональная константа
    private double desiredPromille; // Желаемое промилле

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
}
