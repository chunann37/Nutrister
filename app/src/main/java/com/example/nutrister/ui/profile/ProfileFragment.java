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
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfileFragment extends Fragment {
    TextView username, weight, height, bmi, bmr, bmiStatus;
    ImageView profileImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, gender;
    Button profileBtn, logoutBtn;
    LineChart lineChart;

    private ArrayList<Entry> dataVals = new ArrayList<Entry>();
    private List<Float> historyWeight = new ArrayList<Float>();
    private List<String> historyDate = new ArrayList<String>();
    private List<String> xAxisValues = new ArrayList<>();

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
        bmr = view.findViewById(R.id.profileBMR);
        bmiStatus = view.findViewById(R.id.profileBMIstatus);
        profileBtn = view.findViewById(R.id.updateBtn);
        logoutBtn = view.findViewById(R.id.logoutButton);
        lineChart = view.findViewById(R.id.lineChart);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        CompletableFuture future = new CompletableFuture<>();
        CompletableFuture future2 = new CompletableFuture<>();
        loadData(future);
        future.thenAccept(x -> loadChartData(future2));
        future2.thenAccept(x -> setupLineChart());


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
    private void loadChartData(CompletableFuture future2) {
        for (int i = 0; i < historyWeight.size(); i++) {
            Log.d("", "The history weight size" + historyWeight.size());
            dataVals.add(new Entry(i + 1, historyWeight.get(i)));
            Log.d("", "The data " + dataVals.get(i));
        }
        xAxisValues.add(0, "");
        for (int i = 0; i < historyDate.size(); i++) {
            xAxisValues.add(i + 1, historyDate.get(i));
        }
        future2.complete(null);

    }

    private void setupLineChart() {
        LineDataSet lineDataSet = new LineDataSet(dataVals, "Weight Tracking");
        lineDataSet.setColor(Color.parseColor("#B7E9F7"));
        lineDataSet.setValueTextSize(20f);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        //customization
        lineChart.setExtraLeftOffset(15);
        lineChart.setExtraRightOffset(30);
        lineChart.setExtraBottomOffset(15);
        //hide background lines
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        //hide right Y and top X
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);
        XAxis topXAxis = lineChart.getXAxis();
        topXAxis.setEnabled(false);
        //configure xyAxis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setTextSize(14f);
        leftYAxis.setLabelCount(4);

        lineDataSet.setLineWidth(4f);
        //String setter in x-Axis
        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        LineData data = new LineData((dataSets));
        lineChart.setData(data);
        lineChart.setVisibleXRangeMaximum(2);
        lineChart.invalidate();
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
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
                bmr.setText(documentSnapshot.getString("bmrValue"));
                gender = documentSnapshot.getString("gender");

                if(gender.equals("Male")){

                    profileImage.setImageResource(R.drawable.profile_male);
                } else {
                    profileImage.setImageResource(R.drawable.profile_female);
                }

            }
        });

        CollectionReference weightRef = fStore.collection("users").document(userID).collection("weight_history");
        weightRef.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("", "The task " + task);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        historyDate.add(0, document.getString("date"));
                        Log.d("", "Date is " + historyDate.get(0));
                        historyWeight.add(Float.parseFloat(document.getString("weight")));
                    }
                    future.complete(null);
                } else {
                    Log.d("", "Error getting documents: ", task.getException());
                }

            }

        });
    }

}