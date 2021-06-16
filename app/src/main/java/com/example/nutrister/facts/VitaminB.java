package com.example.nutrister.facts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.nutrister.R;

import java.util.Objects;

public class VitaminB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamin_c);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Vitamin B12");
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