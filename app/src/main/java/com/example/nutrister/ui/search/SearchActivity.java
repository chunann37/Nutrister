package com.example.nutrister.ui.search;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nutrister.CustomFood;
import com.example.nutrister.R;
import com.example.nutrister.UpdateProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SearchActivity extends AppCompatActivity {

    TextView categoryName,foodLabel, foodEnergy, foodCarbs, foodProtein, foodFat, foodFiber;
    ImageView image;
    Spinner mealSpinner, servingSpinner;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, mealName, servingSize, mFoodLabel, mFoodEnergy, mFoodCarbs, mFoodProtein, mFoodFat, mFoodFiber;
    Button customFoodBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        categoryName = findViewById(R.id.categoryInput);
        foodLabel = findViewById(R.id.labelInput);
        foodEnergy = findViewById(R.id.energyInput);
        foodCarbs = findViewById(R.id.carbsInput);
        foodProtein = findViewById(R.id.proteinInput);
        foodFat = findViewById(R.id.fatInput);
        foodFiber = findViewById(R.id.fiberInput);
        image = findViewById(R.id.imageView);
        customFoodBtn = findViewById(R.id.customFoodBtn);

        //get Value from searchFragment
        Intent intent = getIntent();
        categoryName.setText(intent.getStringExtra("categoryName"));
        foodLabel.setText(intent.getStringExtra("label"));
        foodEnergy.setText(intent.getStringExtra("energy"));
        foodCarbs.setText(intent.getStringExtra("carbs"));
        foodProtein.setText(intent.getStringExtra("protein"));
        foodFat.setText(intent.getStringExtra("fat"));
        foodFiber.setText(intent.getStringExtra("fiber"));

        mFoodLabel = intent.getStringExtra("label");
        mFoodEnergy = intent.getStringExtra("energy");
        mFoodCarbs = intent.getStringExtra("carbs");
        mFoodProtein = intent.getStringExtra("protein");
        mFoodFat = intent.getStringExtra("fat");
        mFoodFiber = intent.getStringExtra("fiber");

        String imageUrl = intent.getStringExtra("image");
        if (imageUrl == null) {
            Glide.with(this).load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/480px-No_image_available.svg.png").into(image);
        } else {
            Glide.with(this).load(imageUrl).into(image);
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle("Search Result");

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addFood(View view) {

        mealName = mealSpinner.getSelectedItem().toString().trim();
        servingSize = servingSpinner.getSelectedItem().toString().trim();

        uploadInfo();
    }

    private void uploadInfo() {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        //upload
        CollectionReference db = fStore.collection("users_food_log").document(userID).collection(mealName);
        Map<String, Object> user = new HashMap<>();
        user.put("foodLabel", mFoodLabel);
        user.put("foodEnergy", mFoodEnergy);
        user.put("foodCarbs", mFoodCarbs);
        user.put("foodProtein", mFoodProtein);
        user.put("foodFat", mFoodFat);
        user.put("foodFiber", mFoodFiber);
        user.put("foodServing", servingSize);
        user.put("date", formattedDate);
        db.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("","Food added successfully");
                Toast.makeText(SearchActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("", "onFailure: " + e.toString());
                Toast.makeText(SearchActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void naviToCustom(View view) {
        Intent in = new Intent(getApplicationContext(), CustomFood.class);
        startActivity(in);
    }
}