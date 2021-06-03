package com.example.nutrister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nutrister.ui.search.SearchActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CustomFood extends AppCompatActivity {
    EditText mFoodLabel, mEnergyInput, mCarbsInput, mProteinInput, mFatInput, mFiberInput;
    Spinner mealSpinner, servingSpinner;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID,mealName, servingSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_food);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create custom food data");
        mFoodLabel = findViewById(R.id.foodNameInput);
        mEnergyInput = findViewById(R.id.energyInput);
        mCarbsInput = findViewById(R.id.carbsInput);
        mProteinInput = findViewById(R.id.proteinInput);
        mFatInput = findViewById(R.id.fatInput);
        mFiberInput = findViewById(R.id.fiberInput);
        mealSpinner = findViewById(R.id.mealSpinner);
        servingSpinner = findViewById(R.id.servingSpinner);
        //Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        //Meal Drop-down list
        mealSpinner = findViewById(R.id.mealSpinner);
        String[] mealList = getResources().getStringArray(R.array.meal_list);
        ArrayAdapter mealAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mealList);
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealSpinner.setAdapter(mealAdapter);
        //Serving size Drop-down list
        servingSpinner = findViewById(R.id.servingSpinner);
        String[] servingList = getResources().getStringArray(R.array.serving_size);
        ArrayAdapter servingAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, servingList);
        servingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servingSpinner.setAdapter(servingAdapter);
    }

    public void addCustomFood(View view) {
        if (!validateLabel() | !validateEnergy() | !validateCarbs() | !validateProtein() | !validateFat() | !validateFiber()) {
            return;
        }
        mealName = mealSpinner.getSelectedItem().toString().trim();
        servingSize = servingSpinner.getSelectedItem().toString().trim();
        String foodLabel = mFoodLabel.getText().toString().trim();
        String foodEnergy = mEnergyInput.getText().toString().trim();
        String foodCarbs = mCarbsInput.getText().toString().trim();
        String foodProtein = mProteinInput.getText().toString().trim();
        String foodFat = mFatInput.getText().toString().trim();
        String foodFiber = mFiberInput.getText().toString().trim();

        //Date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        //upload to FireStore
        CollectionReference db = fStore.collection("users_food_log").document(userID).collection(mealName);
        Map<String, Object> user = new HashMap<>();
        user.put("foodLabel", foodLabel);
        user.put("foodEnergy", foodEnergy);
        user.put("foodCarbs", foodCarbs);
        user.put("foodProtein", foodProtein);
        user.put("foodFat", foodFat);
        user.put("foodFiber", foodFiber);
        user.put("foodServing", servingSize);
        user.put("date", formattedDate);
        db.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("","Food added successfully");
                Toast.makeText(CustomFood.this,"Food added successfully",Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("", "onFailure: " + e.toString());
                Toast.makeText(CustomFood.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private boolean validateFiber() {
        String foodfiber = mFiberInput.getText().toString().trim();
        if (TextUtils.isEmpty(foodfiber)) {
            mFiberInput.setError("Food fiber is required.");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateFat() {
        String foodfat = mFatInput.getText().toString().trim();
        if (TextUtils.isEmpty(foodfat)) {
            mFatInput.setError("Food fat is required.");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateProtein() {
        String foodprotein = mProteinInput.getText().toString().trim();
        if (TextUtils.isEmpty(foodprotein)) {
            mProteinInput.setError("Food protein is required.");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateCarbs() {
        String foodcarbs = mCarbsInput.getText().toString().trim();
        if (TextUtils.isEmpty(foodcarbs)) {
            mCarbsInput.setError("Food carbs is required.");
            return false;
        } else {
            return true;
        }

    }

    private boolean validateEnergy() {
        String foodenergy = mEnergyInput.getText().toString().trim();
        if (TextUtils.isEmpty(foodenergy)) {
            mEnergyInput.setError("Food energy is required.");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateLabel() {
        String foodlabel = mFoodLabel.getText().toString().trim();
        if (TextUtils.isEmpty(foodlabel)) {
            mFoodLabel.setError("Food name is required.");
            return false;
        } else {
            return true;
        }
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
}