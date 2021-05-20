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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Suggestion {
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    // Array length for the advice title and advice
    private final int totalAdvice = 6;
    private int k = 0;
    private String[] titles = new String[totalAdvice];
    private String[] advices = new String[totalAdvice];
    private Object[] numberGenerated = new Object[totalAdvice];

    private String userID;
    private String healthIndex, behaviourIndex;

    private Map<Integer, String> healthIndexDescMap = new LinkedHashMap<>();
    private Map<Integer, String> behaviourIndexDescMap = new LinkedHashMap<>();

    public Suggestion(String health, String behaviour) {
        this.healthIndex = health;
        this.behaviourIndex = behaviour;
        this.userID = Objects.requireNonNull(this.fAuth.getCurrentUser()).getUid();

        this.healthIndexDescMap.put(5, "cholesterol");
        this.healthIndexDescMap.put(3, "sugar");
        this.healthIndexDescMap.put(1, "pressure");

        this.behaviourIndexDescMap.put(5, "smoke");
        this.behaviourIndexDescMap.put(3, "drink");
        this.behaviourIndexDescMap.put(1, "exercise");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void generateSuggestion() {
        int healthIndexNumber = Integer.parseInt(this.healthIndex);
        int behaviourIndexNumber = Integer.parseInt(this.behaviourIndex);

        List<String> healthKeys = new LinkedList<>();
        List<String> behaviourKeys = new LinkedList<>();

        if (healthIndexNumber == 0) {
            healthKeys.add("general");
        } else {
            for (int i : this.healthIndexDescMap.keySet()) {
                if (healthIndexNumber - i >= 0) {
                    healthIndexNumber -= i;
                    healthKeys.add(this.healthIndexDescMap.get(i));
                }
            }
        }

        if (behaviourIndexNumber == 0) {
            behaviourKeys.add("general");
        } else {
            for (int i : this.behaviourIndexDescMap.keySet()) {
                if (behaviourIndexNumber - i >= 0) {
                    behaviourIndexNumber -= i;
                    behaviourKeys.add(this.behaviourIndexDescMap.get(i));
                }
            }
        }
        //Generate RNG array
        Random rng = new Random();
        Set<Integer> generated = new LinkedHashSet<>();
        while (generated.size() < totalAdvice )
        {
            int min = 1;
            int max = this.totalAdvice;
            Integer next = rng.nextInt((max - min) + 1) + min;
            generated.add(next);
        }

        this.numberGenerated = generated.toArray();

        List<CompletableFuture> futures = new LinkedList<>();

        //Decide loop
        int loopCount;
        if (healthKeys.size() + behaviourKeys.size() == 2) {
            loopCount = 3;
        } else if (healthKeys.size() + behaviourKeys.size() == 3) {
            loopCount = 2;
        } else if (healthKeys.size() + behaviourKeys.size() == 4) {
            loopCount =1;
            behaviourKeys.add("general");
            healthKeys.add("general");
        } else if (healthKeys.size() + behaviourKeys.size() == 5) {
            loopCount =1;
            behaviourKeys.add("general");
        } else {
            loopCount =1;
        }
        for (int i =0; i < loopCount; i++) {
            for (String key : healthKeys) {
                CompletableFuture future = new CompletableFuture<>();
                futures.add(future);
                switch (key) {
                    case "general":
                        this.getGeneralAdvice(future);
                        break;
                    case "cholesterol":
                        this.getCholesterolAdvice(future);
                        break;

                    case "sugar":
                        this.getSugarAdvice(future);
                        break;

                    case "pressure":
                        this.getPressureAdvice(future);
                        break;
                }
            }

            for (String key : behaviourKeys) {
                CompletableFuture future = new CompletableFuture<>();
                futures.add(future);
                switch (key) {
                    case "general":
                        this.getGeneralAdvice(future);
                        break;

                    case "smoke":
                        this.getSmokeAdvice(future);
                        break;

                    case "drink":
                        this.getDrinkAdvice(future);
                        break;

                    case "exercise":
                        this.getExerciseAdvice(future);
                        break;
                }
            }
        }

        CompletableFuture futuresWrapper = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<Map<String, String>> adviceMapFuture = futuresWrapper.thenApply(x -> this.createAdviceMap());
        adviceMapFuture.thenAccept((x) -> this.storeAdviceData(x));
    }

    private void getAdviceData(String collectionName, String documentName, int totalAdvice, CompletableFuture future) {
        DocumentReference docRef = this.fStore.collection(collectionName).document(documentName);

        int randomNumber = (int) this.numberGenerated[k];
        this.k++;

        String randomTitleKey = "title" + randomNumber;
        String randomAdviceKey = "advice" + randomNumber;

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("", "DocumentSnapshot data:" + document.getData());
                        String title = document.getString(randomTitleKey);
                        String advice = document.getString(randomAdviceKey);
                        for (int i = 0; i < totalAdvice; i++) {
                            if (titles[i] == null && advices[i] == null) {
                                titles[i] = title;
                                advices[i] = advice;
                                break;
                            }
                        }
                        future.complete(null);
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });
    }

    private Map<String, String> createAdviceMap() {
        Map<String, String> adviceMap = new HashMap<>();

        for (int i = 0; i < this.totalAdvice; i++) {
            if (this.titles[i] != null && this.advices[i] != null) {
                adviceMap.put("title" + i, this.titles[i]);
                adviceMap.put("advice" + i, this.advices[i]);
            } else
                break;
        }
        return adviceMap;
    }

    private void storeAdviceData(Map<String, String> adviceMap) {
        // upload to FireStore
        DocumentReference docRef = this.fStore.collection("user_advice").document(this.userID);

        docRef.set(adviceMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("", "User advice is created for" + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("", "onFailure: " + e.toString());
            }
        });
    }

    private void getGeneralAdvice(CompletableFuture future) {
        this.getAdviceData("suggest", "general", 6, future);
    }

    private void getPressureAdvice(CompletableFuture future) {
        this.getAdviceData("suggest", "pressure", 6, future);
    }

    private void getSugarAdvice(CompletableFuture future) {
        this.getAdviceData("suggest", "sugar", 6, future);
    }

    private void getCholesterolAdvice(CompletableFuture future) {
        this.getAdviceData("suggest", "cholesterol", 6, future);
    }

    private void getExerciseAdvice(CompletableFuture future) {
        this.getAdviceData("suggest", "exercise", 6, future);
    }

    private void getDrinkAdvice(CompletableFuture future) {
        this.getAdviceData("suggest", "drink", 6, future);
    }

    private void getSmokeAdvice(CompletableFuture future) {
        this.getAdviceData("suggest", "smoke", 6, future);
    }

}
