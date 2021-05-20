package com.example.nutrister.utils;

import com.example.nutrister.models.FoodResponses;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodApi {

    //Search for food
    //'https://api.edamam.com/api/food-database/v2/parser?nutrition-type=logging&ingr=apple&app_id={app_id)&app_key={app_key)'
    @GET("api/food-database/v2/parser?nutrition-type=logging&")
    Call<FoodResponses> searchFood(
            @Query("ingr") String ingr,
            @Query("app_id") String id,
            @Query("app_key") String key
    );

    //Autocomplete
    //http://api.edamam.com/auto-complete?q=appl&limit=10&app_id={app_id}&app_key={app_key}
    @GET("auto-complete?")
    Call<List> autoComplete(
            @Query("q") String q,
            @Query("limit") int limit,
            @Query("app_id") String id,
            @Query("app_key") String key
    );




}
