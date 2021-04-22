package com.example.nutrister.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodModel implements Parcelable {
    //Model class for our food
    private String label;
    private String foodId;
    private String category;
    private String[] nutrients;

    //Constructor
    public FoodModel(String label, String foodId, String category, String[] nutrients) {
        this.label = label;
        this.foodId = foodId;
        this.category = category;
        this.nutrients = nutrients;
    }

    protected FoodModel(Parcel in) {
        label = in.readString();
        foodId = in.readString();
        category = in.readString();
        nutrients = in.createStringArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(foodId);
        dest.writeString(category);
        dest.writeStringArray(nutrients);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FoodModel> CREATOR = new Creator<FoodModel>() {
        @Override
        public FoodModel createFromParcel(Parcel in) {
            return new FoodModel(in);
        }

        @Override
        public FoodModel[] newArray(int size) {
            return new FoodModel[size];
        }
    };

    //Getters
    public String getLabel() {
        return label;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getCategory() {
        return category;
    }

    public String[] getNutrients() {
        return nutrients;
    }


}
