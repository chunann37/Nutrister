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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Questionnaire extends AppCompatActivity {

    Spinner mExercise, mDrink;
    RadioGroup mSmoke, mPressure, mSugar, mCholesterol;
    RadioButton bSmoke, bPressure, bSugar, bCholesterol;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, username, gender, age, weightValue, heightValue, bmiValue, bmiStatus;

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
        bmrCalculator.calculateBMR(weightValue, heightValue, age, gender, exercise);
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