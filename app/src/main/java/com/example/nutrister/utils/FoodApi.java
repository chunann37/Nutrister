package com.example.nutrister.utils;

import com.example.nutrister.models.FoodResponses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodApi {

    //Search for food
    //'https://api.edamam.com/api/food-database/v2/parser?ingr=red%20apple&app_id={your app_id}&app_key={your app_key}'
    @GET("parser?nutrition-type=logging&")
    Call<FoodResponses> searchFood(
            @Query("ingr") String ingr,
            @Query("app_id") String id,
            @Query("app_key") String key
    );




}
