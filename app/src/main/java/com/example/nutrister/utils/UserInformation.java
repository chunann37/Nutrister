package com.example.nutrister.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserInformation {

    public String username;
    public String gender;
    public String age;
    public String weightValue;
    public String heightValue;
    public String bmiValue;
    public String bmiStatus;
    public String bmrValue;
    public String exercise;
    public String smoke;
    public String drink;
    public String pressure;
    public String sugar;
    public String cholesterol;


    public void collectProfile(String username, String gender, String age, String weightValue, String heightValue, String bmiValue, String bmiStatus) {
        this.username = username;
        this.gender = gender;
        this.age = age;
        this.weightValue = weightValue;
        this.heightValue = heightValue;
        this.bmiValue = bmiValue;
        this.bmiStatus = bmiStatus;
    }

    public String getUsername() {
        return username;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getWeightValue() {
        return weightValue;
    }

    public String getHeightValue() {
        return heightValue;
    }

    public String getBmiValue(){
        return bmiValue;
    }

    public String getBmiStatus(){
        return bmiStatus;
    }

    public void collectQuestionnaire(String bmrValue, String exercise, String drink, String smoke, String pressure, String sugar, String cholesterol) {
        this.bmrValue = bmrValue;
        this.exercise = exercise;
        this.drink = drink;
        this.smoke = smoke;
        this.pressure = pressure;
        this.sugar = sugar;
        this.cholesterol = cholesterol;
    }

    public void collectProfileUpdate(String weightValue, String heightValue, String exercise, String drink, String smoke, String pressure, String sugar, String cholesterol) {
        this.weightValue = weightValue;
        this.heightValue = heightValue;
        this.exercise = exercise;
        this.drink = drink;
        this.smoke = smoke;
        this.pressure = pressure;
        this.sugar = sugar;
        this.cholesterol = cholesterol;
    }


}

