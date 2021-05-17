package com.example.nutrister.utils;

import java.text.DecimalFormat;

public class BMICalculator {
    public String BMIvalue;
    public String result;


    public void calculateBMI(String weight, String height) {
        if (height != null && !"".equals(height)
                && weight != null && !"".equals(weight)) {
            float heightValue = Float.parseFloat(height) / 100;
            float weightValue = Float.parseFloat(weight);

            float bmi = weightValue / (heightValue * heightValue);

            DecimalFormat df = new DecimalFormat("0.00");
            BMIvalue = df.format(bmi);

            displayBMI(bmi);
        }

    }

    private void displayBMI(float bmi) {
        String bmiLabel = "";

        if (Float.compare(bmi, 18.5f) <= 0) {
            bmiLabel = "underweight";
        } else if (Float.compare(bmi, 18.5f) > 0 && Float.compare(bmi, 25f) <= 0) {
            bmiLabel = "normal";
        } else if (Float.compare(bmi, 25f) > 0 && Float.compare(bmi, 30f) <= 0) {
            bmiLabel = "overweight";
        } else {
            bmiLabel = "obese";
        }
        result = bmiLabel;
    }

}