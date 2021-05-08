package com.example.nutrister.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.R;
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
    private SearchView mSearchView;
    private ListView mListView;
    List<String> searchResult = new ArrayList<String>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchView = root.findViewById(R.id.searchView);
        mListView = root.findViewById(R.id.listView);


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFood(newText);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,searchResult);
                mListView.setAdapter(arrayAdapter);
                arrayAdapter.getFilter().filter(newText);
                return false;
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
                        Log.v("Tag", "the enercKcal" + foods.getFood().getNutrients().getEnercKcal());
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

    private void searchFood(String newText) {
        FoodApi foodapi = Servicey.getFoodApi();

        Call<List> responseCall = foodapi
                .autoComplete(
                        newText,
                        10,
                        Credentials.APP_ID,
                        Credentials.API_KEY);
        responseCall.enqueue(new Callback<List>() {
            @Override
            public void onResponse(Call<List> call, Response<List> response) {
                if (response.code() == 200) {
                    Object[] list = response.body().toArray();

                    for (int x=0; x<10; x++){
                        searchResult.add((String) list[x]);
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
            public void onFailure(Call<List> call, Throwable t) {
                Log.v("Tag", "Error"+ t.toString());

            }
        });

    }


}
