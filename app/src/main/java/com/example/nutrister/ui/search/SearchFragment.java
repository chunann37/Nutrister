package com.example.nutrister.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.R;
import com.example.nutrister.SearchActivity;
import com.example.nutrister.models.FoodResponses;
import com.example.nutrister.models.Parsed;
import com.example.nutrister.request.Servicey;
import com.example.nutrister.utils.Credentials;
import com.example.nutrister.utils.FoodApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private SearchView mSearchView;
    private ListView mListView;
    List<String> searchResult = new ArrayList<>();


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
                searchFood(query);
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                CompletableFuture future = new CompletableFuture<>();
                queryFood(newText, future);
                future.thenAccept(x -> updateArray());

                return false;
            }

        });

        return root;
    }

    private void updateArray(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, searchResult);
        arrayAdapter.notifyDataSetChanged();
        mListView.setAdapter(arrayAdapter);

    }

    private void searchFood(String query) {
        FoodApi foodapi = Servicey.getFoodApi();

        Call<FoodResponses> responseCall = foodapi
                .searchFood(
                        query,
                        Credentials.APP_ID,
                        Credentials.API_KEY);
        responseCall.enqueue(new Callback<FoodResponses>() {
            @Override
            public void onResponse(@NotNull Call<FoodResponses> call, @NotNull Response<FoodResponses> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    Log.v("Tag", "the response"+ response.body().toString());

                    List<Parsed> food = new ArrayList<>(response.body().getParsed());

                    Intent intent = new Intent(SearchFragment.this.getActivity(), SearchActivity.class);

                    for (Parsed foods: food){
                        Log.v("Tag", "the enercKcal" + foods.getFood().getNutrients().getEnercKcal());
                        intent.putExtra("categoryName",foods.getFood().getCategory());
                        intent.putExtra("energy",foods.getFood().getNutrients().getEnercKcal().toString());
                        intent.putExtra("carbs",foods.getFood().getNutrients().getChocdf().toString());
                        intent.putExtra("protein",foods.getFood().getNutrients().getProcnt().toString());
                        intent.putExtra("fat",foods.getFood().getNutrients().getFat().toString());
                        intent.putExtra("fiber",foods.getFood().getNutrients().getFibtg().toString());
                        intent.putExtra("image",foods.getFood().getImage());
                    }
                    startActivity(intent);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.v("Tag", "Error"+ response.errorBody().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FoodResponses> call, @NotNull Throwable t) {

            }
        });

    }


    private void queryFood(String newText, CompletableFuture future) {
        FoodApi foodapi = Servicey.getFoodApi();

        Call<List> responseCall = foodapi
                .autoComplete(
                        newText,
                        10,
                        Credentials.APP_ID,
                        Credentials.API_KEY);
        responseCall.enqueue(new Callback<List>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<List> call, @NotNull Response<List> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    Object[] list = response.body().toArray();
                    if (searchResult.isEmpty()) {
                        for (Object o : list) {
                            searchResult.add((String) o);
                        }
                    } else {
                        searchResult.clear();
                        for (Object o : list) {
                            searchResult.add((String) o);
                        }
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.v("Tag", "Error"+ response.errorBody().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                future.complete(null);
            }

            @Override
            public void onFailure(@NotNull Call<List> call, @NotNull Throwable t) {
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
