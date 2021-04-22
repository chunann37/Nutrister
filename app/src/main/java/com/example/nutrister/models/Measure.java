package com.example.nutrister.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Measure {

    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("weight")
    @Expose
    private Double weight;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Measure{" +
                "uri='" + uri + '\'' +
                ", label='" + label + '\'' +
                ", weight=" + weight +
                '}';
    }
}
