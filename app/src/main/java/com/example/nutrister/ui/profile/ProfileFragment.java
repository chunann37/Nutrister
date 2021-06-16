package com.example.nutrister.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.Login;
import com.example.nutrister.R;
import com.example.nutrister.UpdateProfile;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfileFragment extends Fragment {
    TextView username, weight, height, bmi, bmr, bmiStatus;
    ImageView profileImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, gender;
    Button profileBtn, logoutBtn;
    LineChart weightLineChart, caloriesLineCahrt;

    private ArrayList<Entry> dataVals = new ArrayList<Entry>();
    private ArrayList<Entry> dataValsCalories = new ArrayList<Entry>();
    private List<Float> historyWeight = new ArrayList<Float>();
    private List<String> dateWeight = new ArrayList<String>();
    private List<Float> historyCalories = new ArrayList<Float>();
    private List<String> dateCalories = new ArrayList<String>();

    private List<String> xAxisValues = new ArrayList<>();
    private List<String> xAxisValuesCalories = new ArrayList<>();

    private ProfileViewModel profileViewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        username = view.findViewById(R.id.profileName);
        profileImage = view.findViewById(R.id.imageView);
        weight = view.findViewById(R.id.profileWeight);
        height = view.findViewById(R.id.profileHeight);
        bmi = view.findViewById(R.id.profileBMI);
        bmiStatus = view.findViewById(R.id.profileBMIstatus);
        profileBtn = view.findViewById(R.id.updateBtn);
        logoutBtn = view.findViewById(R.id.logoutButton);
        weightLineChart = view.findViewById(R.id.WeightLineChart);
        caloriesLineCahrt = view.findViewById(R.id.CaloriesLineChart);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        CompletableFuture future = new CompletableFuture<>();
        CompletableFuture future2 = new CompletableFuture<>();
        loadData(future);
        future.thenAccept(x -> loadWeightChartData(future2));
        future2.thenAccept(x -> setupWeightLineChart());

        CompletableFuture futureCalories = new CompletableFuture<>();
        CompletableFuture futureCalories2 = new CompletableFuture<>();
        loadCaloriesData(futureCalories);
        futureCalories.thenAccept(x -> loadCaloriesChartData(futureCalories2));
        futureCalories2.thenAccept(x -> setupCaloriesLineChart());

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), UpdateProfile.class);
                startActivity(in);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getActivity().getApplicationContext(), Login.class));
                getActivity().finish();
                ;
            }
        });

        return view;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadWeightChartData(CompletableFuture future2) {
        for (int i = 0; i < historyWeight.size(); i++) {
            Log.d("", "The history weight size" + historyWeight.size());
            dataVals.add(new Entry(i + 1, historyWeight.get(i)));
            Log.d("", "The data " + dataVals.get(i));
        }
        xAxisValues.add(0, "");
        for (int i = 0; i < dateWeight.size(); i++) {
            xAxisValues.add(i + 1, dateWeight.get(i));
        }
        future2.complete(null);

    }

    private void setupWeightLineChart() {
        LineDataSet lineDataSet = new LineDataSet(dataVals, "Weight Tracking");
        lineDataSet.setColor(Color.parseColor("#B7E9F7"));
        lineDataSet.setValueTextSize(20f);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        //customization
        weightLineChart.setExtraLeftOffset(15);
        weightLineChart.setExtraRightOffset(30);
        weightLineChart.setExtraBottomOffset(15);
        //hide background lines
        weightLineChart.getXAxis().setDrawGridLines(false);
        weightLineChart.getAxisLeft().setDrawGridLines(false);
        weightLineChart.getAxisRight().setDrawGridLines(false);
        //hide right Y and top X
        YAxis rightYAxis = weightLineChart.getAxisRight();
        rightYAxis.setEnabled(false);
        XAxis topXAxis = weightLineChart.getXAxis();
        topXAxis.setEnabled(false);
        //configure xyAxis
        XAxis xAxis = weightLineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#A9A9A9"));
        YAxis leftYAxis = weightLineChart.getAxisLeft();
        leftYAxis.setTextSize(14f);
        leftYAxis.setLabelCount(4);
        leftYAxis.setTextColor(Color.parseColor("#A9A9A9"));
        lineDataSet.setLineWidth(4f);
        //String setter in x-Axis
        weightLineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        LineData data = new LineData((dataSets));
        weightLineChart.setData(data);
        weightLineChart.setVisibleXRangeMaximum(2);
        weightLineChart.invalidate();
        weightLineChart.getLegend().setEnabled(false);
        weightLineChart.getDescription().setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData(CompletableFuture future) {

        //Retrieve Data
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setText(documentSnapshot.getString("name"));
                weight.setText(documentSnapshot.getString("weightValue"));
                height.setText(documentSnapshot.getString("heightValue"));
                bmi.setText(documentSnapshot.getString("bmiValue"));
                bmiStatus.setText(documentSnapshot.getString("bmiStatus"));
                gender = documentSnapshot.getString("gender");

                if(gender.equals("Male")){
                    profileImage.setImageResource(R.drawable.profile_male);
                } else {
                    profileImage.setImageResource(R.drawable.profile_female);
                }

                if (bmiStatus.getText().toString().contains("Underweight")){
                    bmiStatus.setTextColor(Color.parseColor("#FB8500"));
                } else if (bmiStatus.getText().toString().contains("Normal")){
                    bmiStatus.setTextColor(Color.parseColor("#06d6a0"));
                } else if (bmiStatus.getText().toString().contains("Overweight")){
                    bmiStatus.setTextColor(Color.parseColor("#FB8500"));
                } else {
                    bmiStatus.setTextColor(Color.parseColor("#d00000"));
                }

            }
        });
        //Weight data
        CollectionReference weightRef = fStore.collection("users").document(userID).collection("weight_history");
        weightRef.orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("", "The task " + task);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        dateWeight.add(0, document.getString("date"));
                        Log.d("", "Date is " + dateWeight.get(0));
                        historyWeight.add(Float.parseFloat(document.getString("weight")));
                    }
                    Collections.reverse(dateWeight);
                    future.complete(null);
                } else {
                    Log.d("", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void loadCaloriesData(CompletableFuture futureCalories){
        //Calories data
        CollectionReference caloriesRef = fStore.collection("users").document(userID).collection("calories_history");
        caloriesRef.orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("", "The task " + task);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        dateCalories.add(0, document.getString("date"));
                        Log.d("", "Date is " + dateCalories.get(0));
                        historyCalories.add(Float.parseFloat(String.valueOf(document.getLong("calories"))));
                    }
                    Collections.reverse(dateCalories);
                    futureCalories.complete(null);
                } else {
                    Log.d("", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void loadCaloriesChartData(CompletableFuture futureCalories2) {
        for (int i = 0; i < historyCalories.size(); i++) {
            Log.d("", "The history calories size" + historyCalories.size());
            dataValsCalories.add(new Entry(i + 1, historyCalories.get(i)));
            Log.d("", "The data " + dataValsCalories.get(i));
        }
        xAxisValuesCalories.add(0, "");
        for (int i = 0; i < dateCalories.size(); i++) {
            xAxisValuesCalories.add(i + 1, dateCalories.get(i));
        }
        futureCalories2.complete(null);

    }

    private void setupCaloriesLineChart() {
        LineDataSet lineDataSet = new LineDataSet(dataValsCalories, "Calories Tracking");
        lineDataSet.setColor(Color.parseColor("#f7c5b7"));
        lineDataSet.setValueTextSize(20f);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        //customization
        caloriesLineCahrt.setExtraLeftOffset(15);
        caloriesLineCahrt.setExtraRightOffset(30);
        caloriesLineCahrt.setExtraBottomOffset(15);
        //hide background lines
        caloriesLineCahrt.getXAxis().setDrawGridLines(false);
        caloriesLineCahrt.getAxisLeft().setDrawGridLines(false);
        caloriesLineCahrt.getAxisRight().setDrawGridLines(false);
        //hide right Y and top X
        YAxis rightYAxis = caloriesLineCahrt.getAxisRight();
        rightYAxis.setEnabled(false);
        XAxis topXAxis = caloriesLineCahrt.getXAxis();
        topXAxis.setEnabled(false);
        //configure xyAxis
        XAxis xAxis = caloriesLineCahrt.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#A9A9A9"));
        YAxis leftYAxis = caloriesLineCahrt.getAxisLeft();
        leftYAxis.setTextSize(14f);
        leftYAxis.setLabelCount(4);
        leftYAxis.setTextColor(Color.parseColor("#A9A9A9"));
        lineDataSet.setLineWidth(4f);
        //String setter in x-Axis
        caloriesLineCahrt.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValuesCalories));

        LineData data = new LineData((dataSets));
        caloriesLineCahrt.setData(data);
        caloriesLineCahrt.setVisibleXRangeMaximum(2);
        caloriesLineCahrt.invalidate();
        caloriesLineCahrt.getLegend().setEnabled(false);
        caloriesLineCahrt.getDescription().setEnabled(false);
    }
}