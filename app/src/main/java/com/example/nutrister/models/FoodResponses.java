package com.example.nutrister.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodResponses {
    @SerializedName("parsed")
    @Expose
    private List<Parsed> parsed = null;

    public List<Parsed> getParsed() {
        return parsed;
    }

    public void setParsed(List<Parsed> parsed) {
        this.parsed = parsed;
    }

    @Override
    public String toString() {
        return "FoodResponse{" +
                "parsed=" + parsed +
                '}';
    }
}
