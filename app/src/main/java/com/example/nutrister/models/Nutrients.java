package com.example.nutrister.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nutrients {
    @SerializedName("ENERC_KCAL")
    @Expose
    private Double enercKcal;
    @SerializedName("PROCNT")
    @Expose
    private Double procnt;
    @SerializedName("FAT")
    @Expose
    private Double fat;
    @SerializedName("CHOCDF")
    @Expose
    private Double chocdf;
    @SerializedName("FIBTG")
    @Expose
    private Double fibtg;

    public Double getEnercKcal() {
        return enercKcal;
    }

    public void setEnercKcal(Double enercKcal) {
        this.enercKcal = enercKcal;
    }

    public Double getProcnt() {
        return procnt;
    }

    public void setProcnt(Double procnt) {
        this.procnt = procnt;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getChocdf() {
        return chocdf;
    }

    public void setChocdf(Double chocdf) {
        this.chocdf = chocdf;
    }

    public Double getFibtg() {
        return fibtg;
    }

    public void setFibtg(Double fibtg) {
        this.fibtg = fibtg;
    }

    @Override
    public String toString() {
        return "Nutrients{" +
                "enercKcal=" + enercKcal +
                ", procnt=" + procnt +
                ", fat=" + fat +
                ", chocdf=" + chocdf +
                ", fibtg=" + fibtg +
                '}';
    }
}
