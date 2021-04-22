package com.example.nutrister.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.R;
import com.example.nutrister.models.FoodModel;
import com.example.nutrister.models.FoodResponses;
import com.example.nutrister.models.Parsed;
import com.example.nutrister.request.Servicey;
import com.example.nutrister.utils.Credentials;
import com.example.nutrister.utils.FoodApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private Button btn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        btn = root.findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                GetRetrofitResponse();
            }
        });




        return root;
    }

    private void GetRetrofitResponse() {
        FoodApi foodapi = Servicey.getFoodApi();

        Call<FoodResponses> responseCall = foodapi
                .searchFood(
                        "apple",
                        Credentials.APP_ID,
                        Credentials.API_KEY);
        responseCall.enqueue(new Callback<FoodResponses>() {
            @Override
            public void onResponse(Call<FoodResponses> call, Response<FoodResponses> response) {
                if (response.code() == 200) {
                    Log.v("Tag", "the response"+ response.body().toString());

                    List<Parsed> food = new ArrayList<>(response.body().getParsed());
                    for (Parsed foods: food){
                        Log.v("Tag", "the nutrient" + foods.getFood());
                    }
                } else {
                    try {
                        Log.v("Tag", "Error"+ response.errorBody().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<FoodResponses> call, Throwable t) {

            }
        });

    }

}