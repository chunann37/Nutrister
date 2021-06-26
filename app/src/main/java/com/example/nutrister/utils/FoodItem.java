package com.example.nutrister.utils;

import android.widget.Button;

public class FoodItem {
    private String foodCarbs, foodEnergy, foodFat, foodFiber, foodLabel, foodProtein, foodServing;

    public FoodItem() {
    }

    public FoodItem(String foodCarbs, String foodEnergy, String foodFat, String foodFiber, String foodLabel, String foodProtein, String foodServing) {
        this.foodCarbs = foodCarbs;
        this.foodEnergy = foodEnergy;
        this.foodFat = foodFat;
        this.foodFiber = foodFiber;
        this.foodLabel = foodLabel;
        this.foodProtein = foodProtein;
        this.foodServing = foodServing;
    }

    public String getFoodCarbs() {
        return foodCarbs;
    }

    public void setFoodCarbs(String foodCarbs) {
        this.foodCarbs = foodCarbs;
    }

    public String getFoodEnergy() {
        return foodEnergy;
    }

    public void setFoodEnergy(String foodEnergy) {
        this.foodEnergy = foodEnergy;
    }

    public String getFoodFat() {
        return foodFat;
    }

    public void setFoodFat(String foodFat) {
        this.foodFat = foodFat;
    }

    public String getFoodFiber() {
        return foodFiber;
    }

    public void setFoodFiber(String foodFiber) {
        this.foodFiber = foodFiber;
    }

    public String getFoodLabel() {
        return foodLabel;
    }

    public void setFoodLabel(String foodLabel) {
        this.foodLabel = foodLabel;
    }

    public String getFoodProtein() {
        return foodProtein;
    }

    public void setFoodProtein(String foodProtein) {
        this.foodProtein = foodProtein;
    }

    public String getFoodServing() {
        return foodServing;
    }

    public void setFoodServing(String foodServing) {
        this.foodServing = foodServing;
    }

}
