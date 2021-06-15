package com.example.nutrister.utils;

public class BMRCalculator {
    public String BMRvalue;
    public double basicValue, recommendValue;
    public double bmr;


    public void basicBMR(String weight, String height, String age, String gender, String exercise, String bmiStatus) {
        if (height != null && !"".equals(height)
                && weight != null && !"".equals(weight)) {
            double heightValue = Double.parseDouble(height);
            double weightValue = Double.parseDouble(weight);
            double ageValue = Double.parseDouble(age);

            if (gender.equals("Male")) {
                bmr = ((10 * weightValue) + (6.25 * heightValue) - (5 * ageValue) + 5);
            } else {
                bmr = ((10 * weightValue) + (6.25 * heightValue) - (5 * ageValue) - 161);
            }

            if (exercise.equals("little or no exercise")) {
                basicValue = (bmr * 1.2);
            } else if (exercise.equals("light exercise or sports 1-3 days/week")) {
                basicValue = (bmr * 1.375);
            } else if (exercise.equals("moderate exercise 3-5 days/week")) {
                basicValue = (bmr * 1.55);
            } else if (exercise.equals("hard exercise 6-7 days/week")) {
                basicValue = (bmr * 1.725);
            } else {
                basicValue = (bmr * 1.9);
            }
            double roundedResult = Math.round(basicValue);
            int intResult = (int) roundedResult;
            recommendBMR(bmiStatus, intResult);
        }
    }

    public void recommendBMR(String bmiStatus, int bmrValue) {
        int deductValue;
        switch (bmiStatus) {
            case "Underweight":
                deductValue = 300;
                recommendValue = bmrValue + deductValue;
                break;

            case "Normal":
                recommendValue = bmrValue;
                break;

            case "Overweight":
                deductValue = 300;
                recommendValue = bmrValue - deductValue;
                break;

            case "Obese":
                deductValue = 500;
                recommendValue = bmrValue - deductValue;
                break;

        }
        double roundedResult = Math.round(recommendValue);
        int intResult = (int) roundedResult;
        BMRvalue = String.valueOf(intResult);

    }
}

