package com.example.nutrister.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parsed {
    @SerializedName("food")
    @Expose
    private Food food;
    @SerializedName("quantity")
    @Expose
    private Double quantity;
    @SerializedName("measure")
    @Expose
    private Measure measure;

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    @Override
    public String toString() {
        return "Parsed{" +
                "food=" + food +
                ", quantity=" + quantity +
                ", measure=" + measure +
                '}';
    }
}
