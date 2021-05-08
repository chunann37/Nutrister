package com.example.nutrister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrister.utils.BMICalculator;
import com.example.nutrister.utils.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class SetupProfile extends AppCompatActivity {
    EditText mUsername, mWeight, mHeight;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        mUsername = findViewById(R.id.userType);
        datePicker = findViewById(R.id.agePicker);
        mWeight = findViewById(R.id.weightType);
        mHeight = findViewById(R.id.heightType);
        radioGroup = findViewById(R.id.radioGroup);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        if (fAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        } else {
            Toast.makeText(SetupProfile.this, "Logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextProfile(View view) {
        if (!validateUsername() | !validateAge() | !validateGender() | !validateHeight() | !validateWeight()) {
            return;
        }
        //user information
        String username = mUsername.getText().toString().trim();
        selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
        String gender = selectedGender.getText().toString().trim();

        String age = String.valueOf((Calendar.getInstance().get(Calendar.YEAR)) - (datePicker.getYear()));

        String weightValue = mWeight.getText().toString().trim();
        String heightValue = mHeight.getText().toString().trim();

        //calculate user BMI
        BMICalculator bmiCalculator = new BMICalculator();
        bmiCalculator.calculateBMI(weightValue, heightValue);
        String bmiValue = bmiCalculator.BMIvalue;
        String bmiStatus = bmiCalculator.result;

        //collect profile information
        UserInformation userinformation = new UserInformation();
        userinformation.collectProfile(username, gender, age, weightValue, heightValue, bmiValue, bmiStatus);

        Intent intent = new Intent(SetupProfile.this, Questionnaire.class);
        intent.putExtra("username", username);
        intent.putExtra("gender", gender);
        intent.putExtra("age", age);
        intent.putExtra("weightValue", weightValue);
        intent.putExtra("heightValue", heightValue);
        intent.putExtra("bmiValue", bmiValue);
        intent.putExtra("bmiStatus", bmiStatus);

        startActivity(intent);
    }


    private boolean validateUsername() {
        String username = mUsername.getText().toString().trim();
        String checkspaces = "^[A-Za-z]\\\\w{5,29}$";

        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Username is required.");
            return false;
        } else if (username.length() > 20) {
            mUsername.setError("Username is too long");
            return false;
        } else if (username.matches(checkspaces)) {
            mUsername.setError("No White spaces are allowed!");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 1) {
            Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
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

}