package com.example.nutrister;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nutrister.utils.Accumulation;
import com.example.nutrister.utils.BMICalculator;
import com.example.nutrister.utils.BMRCalculator;
import com.example.nutrister.utils.HealthIndex;
import com.example.nutrister.utils.Suggestion;
import com.example.nutrister.utils.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UpdateProfile extends AppCompatActivity {
    EditText mWeight, mHeight;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Spinner mExercise, mDrink;
    RadioGroup mSmoke, mPressure, mSugar, mCholesterol;
    RadioButton bSmoke, bPressure, bSugar, bCholesterol;
    String userID, age, gender;
    Button updateProfileBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mWeight = findViewById(R.id.weightType);
        mHeight = findViewById(R.id.heightType);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Profile");


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

        updateProfileBtn = findViewById(R.id.finishBtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        //Retrieve user age and gender
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                age = documentSnapshot.getString("age");
                gender = documentSnapshot.getString("gender");
            }
        });

        //setOnclick
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (!validateHeight() | !validateWeight() | !validateExercise() | !validateDrink() | !validateSmoke() | !validateSugar() | !validatePressure() | !validateCholesterol()) {
                    return;
                }
                String weightValue = mWeight.getText().toString().trim();
                String heightValue = mHeight.getText().toString().trim();

                //calculate user BMI
                BMICalculator bmiCalculator = new BMICalculator();
                bmiCalculator.calculateBMI(weightValue, heightValue);
                String bmiValue = bmiCalculator.BMIvalue;
                String bmiStatus = bmiCalculator.result;

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

                UserInformation userinformation = new UserInformation();
                userinformation.collectProfileUpdate(weightValue,heightValue,exercise,drink,smoke,pressure,sugar,cholesterol);

                Map<String, Object> user = new HashMap<>();
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
                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "User profile is update for" + userID);
                        Toast.makeText(UpdateProfile.this, "Completed", Toast.LENGTH_SHORT).show();
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
                SimpleDateFormat df = new SimpleDateFormat("dd/MM", Locale.getDefault());
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


                Intent intent = new Intent( getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //Back to previous fragment
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent parentIntent = NavUtils.getParentActivityIntent(this);
            assert parentIntent != null;
            parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(parentIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateWeight() {
        String weight = mWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weight)) {
            mWeight.setError("Please enter your weight");
            return false;
        } else if (Integer.parseInt(weight) < 3 || Integer.parseInt(weight) > 150) {
            mWeight.setError("Please enter valid weight");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateHeight() {
        String height = mHeight.getText().toString().trim();
        if (TextUtils.isEmpty(height)) {
            mHeight.setError("Please enter your height");
            return false;
        } else if (Integer.parseInt(height) < 50 || Integer.parseInt(height) > 220) {
            mHeight.setError("Please enter valid height");
            return false;
        } else {
            return true;
        }
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