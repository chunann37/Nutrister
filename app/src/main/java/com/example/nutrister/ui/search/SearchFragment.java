package com.example.nutrister.ui.search;

import android.content.Context;
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
                queryFood(newText);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,searchResult);
                arrayAdapter.notifyDataSetChanged();
                mListView.setAdapter(arrayAdapter);

                return false;
            }
        });

        return root;
    }

    private void searchFood() {
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

    private void queryFood(String newText) {
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
                    if (searchResult.isEmpty()) {
                        for (int x = 0; x < list.length; x++) {
                            searchResult.add((String) list[x]);
                        }
                    } else {
                        searchResult.clear();
                        for (int x = 0; x < list.length; x++) {
                            searchResult.add((String) list[x]);
                        }
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

    public void showProgressBar (SearchView mSearchView, Context context){
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        if (mSearchView.findViewById(id).findViewById(R.id.search_progress_bar) != null)
            mSearchView.findViewById(id).findViewById(R.id.search_progress_bar).animate().setDuration(200).alpha(1).start();

        else
        {
            View v = LayoutInflater.from(context).inflate(R.layout.loading_icon, null);
            ((ViewGroup) mSearchView.findViewById(id)).addView(v, 1);
        }
    }
    public void hideProgressBar(SearchView mSearchView){
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        if (mSearchView.findViewById(id).findViewById(R.id.search_progress_bar) != null)
            mSearchView.findViewById(id).findViewById(R.id.search_progress_bar).animate().setDuration(200).alpha(0).start();
    }



}
