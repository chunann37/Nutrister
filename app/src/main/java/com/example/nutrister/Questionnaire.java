package com.example.nutrister;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrister.ui.search.SearchActivity;
import com.example.nutrister.utils.BMRCalculator;
import com.example.nutrister.utils.HealthIndex;
import com.example.nutrister.utils.Suggestion;
import com.example.nutrister.utils.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Questionnaire extends AppCompatActivity {

    Spinner mExercise, mDrink;
    RadioGroup mSmoke, mPressure, mSugar, mCholesterol;
    RadioButton bSmoke, bPressure, bSugar, bCholesterol;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, username, gender, age, weightValue, heightValue, bmiValue, bmiStatus, bmiAdvice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        //get Value from SetupProfile
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        gender = intent.getStringExtra("gender");
        age = intent.getStringExtra("age");
        weightValue = intent.getStringExtra("weightValue");
        heightValue = intent.getStringExtra("heightValue");
        bmiValue = intent.getStringExtra("bmiValue");
        bmiStatus = intent.getStringExtra("bmiStatus");
        bmiAdvice = intent.getStringExtra("bmiAdvice");

        //Exercise dropdown list
        mExercise = findViewById(R.id.exerciseSpinner);
        String[] exerciseFrequency = getResources().getStringArray(R.array.exercise_frequency);
        ArrayAdapter adapterExercise = new ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseFrequency);
        adapterExercise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mExercise.setAdapter(adapterExercise);

        //Drinking dropdown list
        mDrink = findViewById(R.id.drinkSpinner);
        String[] drinkFrequency = getResources().getStringArray(R.array.drink_frequency);
        ArrayAdapter adapterDrink = new ArrayAdapter(this, android.R.layout.simple_spinner_item, drinkFrequency);
        adapterDrink.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDrink.setAdapter(adapterDrink);

        //RadioGroup
        mSmoke = findViewById(R.id.smokeRG);
        mPressure = findViewById(R.id.bloodPressureRG);
        mSugar = findViewById(R.id.bloodSugarRG);
        mCholesterol = findViewById(R.id.bloodCholesterolRG);

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Toast.makeText(Questionnaire.this, "Please complete this health evaluation", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void finishQuestion(View view) throws InterruptedException {
        if (!validateExercise() | !validateDrink() | !validateSmoke() | !validateSugar() | !validatePressure() | !validateCholesterol()) {
            return;
        }
        bSmoke = findViewById(mSmoke.getCheckedRadioButtonId());
        bPressure = findViewById(mPressure.getCheckedRadioButtonId());
        bSugar = findViewById(mSugar.getCheckedRadioButtonId());
        bCholesterol = findViewById(mCholesterol.getCheckedRadioButtonId());

        String exercise = mExercise.getSelectedItem().toString().trim();
        String drink = mDrink.getSelectedItem().toString().trim();
        String smoke = bSmoke.getText().toString().trim();
        String pressure = bPressure.getText().toString().trim();
        String sugar = bSugar.getText().toString().trim();
        String cholesterol = bCholesterol.getText().toString().trim();

        //calculate user BMR
        BMRCalculator bmrCalculator = new BMRCalculator();
        bmrCalculator.basicBMR(weightValue, heightValue, age, gender, exercise, bmiStatus);
        String bmrValue = bmrCalculator.BMRvalue;

        //calculate user health index
        HealthIndex healthIndex = new HealthIndex();
        healthIndex.calculateHealthIndex(exercise,drink,smoke,pressure,sugar,cholesterol);
        String healthIndexValue = healthIndex.result;
        String healthIndex1 = healthIndex.index1;
        String healthIndex2 = healthIndex.index2;

        //Generate suggestion
        Suggestion suggestion = new Suggestion(healthIndex2, healthIndex1);
        suggestion.generateSuggestion();


        //collect questionnaire information
        UserInformation userinformation = new UserInformation();
        userinformation.collectQuestionnaire(bmrValue,exercise,drink,smoke,pressure,sugar,cholesterol);

        //upload to FireStore
        DocumentReference documentReference = fStore.collection("users").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("name", username);
        user.put("gender", gender);
        user.put("age", age);
        user.put("weightValue", weightValue);
        user.put("heightValue", heightValue);
        user.put("bmiValue",bmiValue);
        user.put("bmiStatus",bmiStatus);
        user.put("bmiAdvice",bmiAdvice);
        user.put("exercise", exercise);
        user.put("smoke", smoke);
        user.put("drink", drink);
        user.put("pressure", pressure);
        user.put("sugar", sugar);
        user.put("cholesterol", cholesterol);
        user.put("bmrValue", bmrValue);
        user.put("healthIndexValue", healthIndexValue);
        user.put("healthIndex1", healthIndex1);
        user.put("healthIndex2", healthIndex2);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("", "User health profile is created for" + userID);
                Toast.makeText(Questionnaire.this, "Completed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("", "onFailure: " + e.toString());
            }
        });
        DocumentReference baseRef = fStore.collection("users_food_log").document(userID);
        Map<String, Object> base = new HashMap<>();
        base.put("bmrValue",bmrValue);
        base.put("totalEnergy",0);
        base.put("totalCarbs",0);
        base.put("totalProtein",0);
        base.put("totalFat",0);
        base.put("totalFiber",0);
        baseRef.set(base).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("", "Constructed food log" + userID);
            }
        });
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        String formattedDate = df.format(c);
        CollectionReference db = fStore.collection("users").document(userID).collection("weight_history");
        Map<String, Object> weightHistory = new HashMap<>();
        weightHistory.put("weight", weightValue);
        weightHistory.put("date", formattedDate);
        weightHistory.put("timestamp", FieldValue.serverTimestamp());
        db.add(weightHistory).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("","Weight history updated successfully");
            }
        });
        SimpleDateFormat df2 = new SimpleDateFormat("dd_MM", Locale.getDefault());
        String formattedDate2 = df2.format(c);
        DocumentReference caloriesRef = fStore.collection("users").document(userID).collection("calories_history").document(formattedDate2);
        Map<String, Object> caloriesHistory = new HashMap<>();
        caloriesHistory.put("calories", 0);
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
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }



    private boolean validateExercise() {
        if (mExercise.getSelectedItem() == "") {
            Toast.makeText(this, "Please answer this question", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateSmoke() {
        if (mSmoke.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please answer this question", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateDrink() {
        if (mDrink.getSelectedItem() == "") {
            Toast.makeText(this, "Please answer this question", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePressure() {
        if (mPressure.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please answer this question", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateSugar() {
        if (mSugar.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please answer this question", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateCholesterol() {
        if (mCholesterol.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please answer this question", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


}