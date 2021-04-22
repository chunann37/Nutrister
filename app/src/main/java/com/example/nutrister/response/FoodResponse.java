package com.example.nutrister.response;

import com.example.nutrister.models.FoodModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//This class is to find specific food
public class FoodResponse {
    @SerializedName("parsed")
    @Expose
    private List<FoodModel> food;

    public List<FoodModel> getFood() {
        return food;
    }


    @Override
    public String toString() {
        return "FoodResponse{" +
                "food=" + food +
                '}';
    }
}
