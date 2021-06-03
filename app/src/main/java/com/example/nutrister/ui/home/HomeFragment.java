package com.example.nutrister.ui.home;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.nutrister.R;
import com.example.nutrister.utils.CustomPercentFormatter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ProgressBar progBar;
    private int totalEnergy, totalCarbs, totalProtein, totalFat, totalFiber, bmrValue;
    private TextView textEnergy, textBmr, textCarbs, textProtein, textFat, textFiber;
    private int[] amount ={0,0,0,0};
    private ArrayList<PieEntry> entries = new ArrayList<>();
    String userID;
    PieChart pieChart;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        progBar = root.findViewById(R.id.caloriesProgressBar);
        textEnergy = root.findViewById(R.id.consumedValue);
        textBmr = root.findViewById(R.id.bmrValue);
        textCarbs = root.findViewById(R.id.carbsValue);
        textProtein = root.findViewById(R.id.proteinValue);
        textFat = root.findViewById(R.id.fatValue);
        textFiber = root.findViewById(R.id.fiberValue);
        pieChart = root.findViewById(R.id.pieChart);
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        //Progress Bar
        CompletableFuture future = new CompletableFuture<>();
        loadData(future);
        setupPieChart();
        future.thenAccept(x -> loadPieChartData());


        return root;
    }

    private void loadData(CompletableFuture future) {
        DocumentReference infoRef = db.collection("users_food_log").document(userID);
        infoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textBmr.setText(documentSnapshot.getString("bmrValue"));
                bmrValue = Integer.parseInt(documentSnapshot.getString("bmrValue"));
                totalEnergy = documentSnapshot.getLong("totalEnergy").intValue();
                Log.d("","the total energy "+ totalEnergy );
                String showEnergy = String.valueOf(totalEnergy);
                textEnergy.setText(showEnergy);
                progBar.setMax(bmrValue);
                progBar.setProgress(totalEnergy);

                amount[0] = documentSnapshot.getLong("totalCarbs").intValue();
                amount[1] = documentSnapshot.getLong("totalProtein").intValue();
                amount[2] = documentSnapshot.getLong("totalFat").intValue();
                amount[3] = documentSnapshot.getLong("totalFiber").intValue();

                textCarbs.setText(String.valueOf(amount[0]));
                textProtein.setText(String.valueOf(amount[1]));
                textFat.setText(String.valueOf(amount[2]));
                textFiber.setText(String.valueOf(amount[3]));

                entries.add(0,(new PieEntry(amount[0],"Carbs")));
                entries.add(1,(new PieEntry(amount[1],"Protein")));
                entries.add(2,(new PieEntry(amount[2],"Fat")));
                entries.add(3,(new PieEntry(amount[3],"Fiber")));


                future.complete(null);
            }
        });
    }

    private void setupPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Nutrients");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraBottomOffset(10f);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(18f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);

    }

    private void loadPieChartData(){
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS){
            colors.add(color);
        }
        for (int color: ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.BLACK);
        data.setValueFormatter(new CustomPercentFormatter());

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.setDrawEntryLabels(false);
        pieChart.getData().setDrawValues(true);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }



}