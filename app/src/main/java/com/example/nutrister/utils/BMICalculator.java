package com.example.nutrister.utils;

import java.text.DecimalFormat;

public class BMICalculator {
    public String BMIvalue;
    public String result;
    public String weightAdvice;


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
            bmiLabel = "Underweight";
        } else if (Float.compare(bmi, 18.5f) > 0 && Float.compare(bmi, 25f) <= 0) {
            bmiLabel = "Normal";
        } else if (Float.compare(bmi, 25f) > 0 && Float.compare(bmi, 30f) <= 0) {
            bmiLabel = "Overweight";
        } else {
            bmiLabel = "Obese";
        }
        result = bmiLabel;
        generateWeightAdvice(result);
    }

    private void generateWeightAdvice(String bmiStatus){
        switch (bmiStatus){
            case "Underweight":
                weightAdvice="Based on BMI status, you should increase calories intake to gain weight.";
                break;

            case "Normal":
                weightAdvice="Based on BMI status, you should maintain calories intake to maintain healthy weight.";
                break;

            case "Overweight":
                weightAdvice="Based on BMI status, you should decrease calories intake and exercise to lose weight.";
                break;

            case "Obese":
                weightAdvice="Based on BMI status, you need to decrease calories intake and exercise regularly to lose weight.";
                break;

        }

    }
}