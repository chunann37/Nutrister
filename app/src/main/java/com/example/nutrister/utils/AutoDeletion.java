package com.example.nutrister.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AutoDeletion {

    public AutoDeletion() {
    }

    public static void deletion(String userID, FirebaseAuth fAuth, FirebaseFirestore db) {

        //Auto-deletion
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String currentDate = df.format(c);

        //Breakfast
        Query bfItem = db.collection("users_food_log").document(userID).collection("Breakfast").whereNotEqualTo("date", currentDate);
        String finalUserID = userID;
        bfItem.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        db.collection("users_food_log").document(finalUserID).
                                collection("Breakfast").document(document.getId()).delete();
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

        //Lunch
        Query lcItem = db.collection("users_food_log").document(userID).collection("Lunch").whereNotEqualTo("date", currentDate);
        String finalUserID1 = userID;
        lcItem.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        db.collection("users_food_log").document(finalUserID1).
                                collection("Lunch").document(document.getId()).delete();
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

        //Dinner
        Query dnItem = db.collection("users_food_log").document(userID).collection("Dinner").whereNotEqualTo("date", currentDate);
        String finalUserID2 = userID;
        dnItem.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        db.collection("users_food_log").document(finalUserID2).
                                collection("Dinner").document(document.getId()).delete();
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

        //Snack
        Query snItem = db.collection("users_food_log").document(userID).collection("Snack").whereNotEqualTo("date", currentDate);
        String finalUserID3 = userID;
        snItem.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        db.collection("users_food_log").document(finalUserID3).
                                collection("Snack").document(document.getId()).delete();
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

    }



}
