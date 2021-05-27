package com.example.nutrister.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nutrister.R;

import java.util.Objects;

public class Search_NotFound extends AppCompatActivity {
    ImageView image;
    TextView title, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_not_found);

        image = findViewById(R.id.imageView);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Search Result");
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