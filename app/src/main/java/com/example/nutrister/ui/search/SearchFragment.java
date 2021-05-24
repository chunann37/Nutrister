package com.example.nutrister.ui.search;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.R;
import com.example.nutrister.models.FoodResponses;
import com.example.nutrister.models.Parsed;
import com.example.nutrister.request.Servicey;
import com.example.nutrister.utils.Credentials;
import com.example.nutrister.utils.FoodApi;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
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
                mSearchView.clearFocus();
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                CompletableFuture future = new CompletableFuture<>();
                queryFood(newText, future);
                future.thenAccept(x -> updateArray());

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        searchFood(selectedItem);
                    }
                });

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

                    if (response.body().getParsed().isEmpty()){
                        startActivity(new Intent (SearchFragment.this.getActivity(),Search_NotFound.class));
                    } else {
                        List<Parsed> food = new ArrayList<>(response.body().getParsed());
                        Intent intent = new Intent(SearchFragment.this.getActivity(), SearchActivity.class);
                        for (Parsed foods : food) {
                            intent.putExtra("categoryName", foods.getFood().getCategory());
                            intent.putExtra("label", foods.getFood().getLabel());
                            intent.putExtra("image", foods.getFood().getImage());

                            DecimalFormat df = new DecimalFormat("0.00");
                            String roundedEnergy = df.format(foods.getFood().getNutrients().getEnercKcal());
                            String roundedCarbs = df.format(foods.getFood().getNutrients().getChocdf());
                            String roundedProtein = df.format(foods.getFood().getNutrients().getProcnt());
                            String roundedFat = df.format(foods.getFood().getNutrients().getFat());
                            String roundedFiber = df.format(foods.getFood().getNutrients().getFibtg());
                            intent.putExtra("energy", roundedEnergy);
                            intent.putExtra("carbs", roundedCarbs);
                            intent.putExtra("protein", roundedProtein);
                            intent.putExtra("fat", roundedFat);
                            intent.putExtra("fiber", roundedFiber);

                        }
                        startActivity(intent);
                    }
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
                Log.d("ERROR", t.getMessage());
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
                Log.d("ERROR", t.getMessage());
            }
        });

    }



}
