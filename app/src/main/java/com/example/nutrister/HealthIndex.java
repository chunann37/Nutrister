package com.example.nutrister;

public class HealthIndex {
    public String result, index1, index2;
    public int result1, result2;

    public void calculateHealthIndex (String exercise, String drink, String smoke, String pressure, String sugar, String cholesterol){
        int counter = 0;
        if (exercise.equals("little or no exercise") ){
            counter = counter + 1;
        } else {
            counter = counter + 0;
        }
        if (drink.equals("little or no drink")){
            counter = counter + 0;
        } else {
            counter = counter + 3;
        }
        if (smoke.equals("No")){
            counter = counter + 0;
        } else {
            counter = counter + 5;
        }
        if (pressure.equals("No")) {
            counter = counter + 0;
        } else  {
            counter = counter + 10;
        }
        if (sugar.equals("No")){
            counter = counter + 0;
        } else {
            counter = counter + 30;
        }
        if (cholesterol.equals("No")){
            counter = counter + 0;
        } else  {
            counter = counter + 50;
        }

        result = String.valueOf(counter);

        if (counter != 0){
            result1 = counter % 10;
            counter = counter / 10;
            result2 = counter % 10;
        } else {
            result1 = 0;
            result2 = 0;
        }
        index1 = String.valueOf(result1);
        index2 = String.valueOf(result2);
    }






}
