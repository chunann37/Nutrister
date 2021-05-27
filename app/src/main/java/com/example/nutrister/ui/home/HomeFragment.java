package com.example.nutrister.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.R;
import com.example.nutrister.utils.Cumulation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ProgressBar progBar;
    String userID;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private double totalEnergy, totalCarbs, totalProtein, totalFat, totalFiber;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        progBar = root.findViewById(R.id.caloriesProgressBar);

        progBar.setMax(5000);
        progBar.setProgress(2500);

        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();


        Cumulation cumulation = new Cumulation(userID, fAuth, db);
        totalEnergy = cumulation.getTotalEnergy();

        Log.d("", "The total energy of all day is" + totalEnergy);
        Log.d("", "The total carbs of all day is" + totalCarbs);
        Log.d("", "The total protein of all day is" + totalProtein);
        Log.d("", "The total fat of all day is" + totalFat);
        Log.d("", "The total fiber of all day is" + totalFiber);

        return root;
    }

}