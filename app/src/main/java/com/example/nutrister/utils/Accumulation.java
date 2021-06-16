package com.example.nutrister.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Accumulation {
    private double totalEnergy, totalCarbs, totalProtein, totalFat, totalFiber;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userID;
    String[] mealName = {"Breakfast", "Lunch", "Dinner", "Snack"};

    public Accumulation(String userID, FirebaseAuth fAuth, FirebaseFirestore db) {
        this.fAuth = fAuth;
        this.db = db;
        this.userID = userID;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doCalculation(){

        List<CompletableFuture<Object>> futures = new LinkedList<>();
        CompletableFuture<Object> futureEnergy = new CompletableFuture<>();
        futures.add(futureEnergy);
        this.getTotalEnergy(futureEnergy);
        CompletableFuture<Object> futureCarbs = new CompletableFuture<>();
        futures.add(futureCarbs);
        this.getTotalCarbs(futureCarbs);
        CompletableFuture<Object> futureProtein = new CompletableFuture<>();
        futures.add(futureProtein);
        this.getTotalProtein(futureProtein);
        CompletableFuture<Object> futureFat = new CompletableFuture<>();
        futures.add(futureFat);
        this.getTotalFat(futureFat);
        CompletableFuture<Object> futureFiber = new CompletableFuture<>();
        futures.add(futureFiber);
        this.getTotalFiber(futureFiber);

        CompletableFuture futuresWrapper = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<Map<String, Object>> nutrientFuture = futuresWrapper.thenApply(x -> this.createInfoMap());
        nutrientFuture.thenAccept((x) -> this.storeInfoData(x));

    }

    private void storeInfoData(Map<String, Object> infoMap) {
        // upload to FireStore
        DocumentReference docRef = this.db.collection("users_food_log").document(this.userID);

        docRef.update(infoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("", "Consumed info created" + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("", "onFailure: " + e.toString());
            }
        });

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM", Locale.getDefault());
        SimpleDateFormat df2 = new SimpleDateFormat("dd_MM", Locale.getDefault());
        String formattedDate = df.format(c);
        String formattedDate2 = df2.format(c);
        DocumentReference caloriesRef = this.db.collection("users").document(userID).collection("calories_history").document(formattedDate2);
        Map<String, Object> caloriesHistory = new HashMap<>();
        caloriesHistory.put("calories", totalEnergy);
        caloriesHistory.put("date", formattedDate);
        caloriesHistory.put("timestamp", FieldValue.serverTimestamp());
        caloriesRef.set(caloriesHistory).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("", "Calories history updated" + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("", "onFailure: " + e.toString());
            }
        });

    }

    private Map<String, Object> createInfoMap() {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("totalEnergy", this.totalEnergy);
        infoMap.put("totalCarbs", this.totalCarbs);
        infoMap.put("totalProtein", this.totalProtein);
        infoMap.put("totalFat", this.totalFat);
        infoMap.put("totalFiber", this.totalFiber);

        return infoMap;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getTotalEnergy(CompletableFuture<Object> futureEnergy) {
        final int[] counter = {0};
        //Collect total energy
        for (String mealTemp : mealName) {
            db.collection("users_food_log").document(userID).collection(mealTemp)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    double newFood = Double.parseDouble(Objects.requireNonNull(document.getString("foodEnergy")));
                                    double serving = Double.parseDouble(Objects.requireNonNull(document.getString("foodServing")));
                                    totalEnergy += newFood * serving;
                                    Log.d("", "Energy added is " + totalEnergy);
                                }
                                counter[0] += 1;
                                if (counter[0] == 4) {
                                    futureEnergy.complete(null);
                                }
                            } else {
                                Log.d("", "Error getting documents: ", task.getException());
                            }

                        }

                    });
        }
    }

    public void getTotalCarbs(CompletableFuture<Object> futureCarbs) {
        final int[] counter = {0};
        for (String mealTemp : mealName) {
            db.collection("users_food_log").document(userID).collection(mealTemp)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    double newFood = Double.parseDouble(Objects.requireNonNull(document.getString("foodCarbs")));
                                    totalCarbs += newFood;
                                    Log.d("", "Carbs added is " + totalCarbs);
                                }
                                counter[0] += 1;
                                if (counter[0] == 4) {
                                    futureCarbs.complete(null);
                                }
                            } else {
                                Log.d("", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void getTotalProtein(CompletableFuture<Object> futureProtein) {
        final int[] counter = {0};
        for (String mealTemp : mealName) {
            db.collection("users_food_log").document(userID).collection(mealTemp)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    double newFood = Double.parseDouble(Objects.requireNonNull(document.getString("foodProtein")));
                                    totalProtein += newFood;
                                    Log.d("", "Protein added is " + totalProtein);
                                }
                                counter[0] += 1;
                                if (counter[0] == 4) {
                                    futureProtein.complete(null);
                                }
                            } else {
                                Log.d("", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void getTotalFat(CompletableFuture<Object> futureFat) {
        final int[] counter = {0};
        for (String mealTemp : mealName) {
            db.collection("users_food_log").document(userID).collection(mealTemp)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    double newFood = Double.parseDouble(Objects.requireNonNull(document.getString("foodFat")));
                                    totalFat += newFood;
                                    Log.d("", "Fat added is " + totalFat);
                                }
                                counter[0] += 1;
                                if (counter[0] == 4) {
                                    futureFat.complete(null);
                                }
                            } else {
                                Log.d("", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void getTotalFiber(CompletableFuture<Object> futureFiber) {
        final int[] counter = {0};
        for (String mealTemp : mealName) {
            db.collection("users_food_log").document(userID).collection(mealTemp)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    double newFood = Double.parseDouble(Objects.requireNonNull(document.getString("foodFiber")));
                                    totalFiber += newFood;
                                    Log.d("", "Fiber added is " + totalFiber);
                                }
                                counter[0] += 1;
                                if (counter[0] == 4) {
                                    futureFiber.complete(null);
                                }
                            } else {
                                Log.d("", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
}
