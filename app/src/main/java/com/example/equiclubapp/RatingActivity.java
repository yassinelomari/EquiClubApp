package com.example.equiclubapp;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView emoji1, emoji2, emoji3, emoji4;
    TextInputEditText rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rating);
        // initialise rating field
        rating = findViewById(R.id.rating);
        //get ImageView
        emoji1 = findViewById(R.id.emoji1);
        emoji2 = findViewById(R.id.emoji2);
        emoji3 = findViewById(R.id.emoji3);
        emoji4 = findViewById(R.id.emoji4);
        //ImageView with listener
        emoji1.setOnClickListener(this);
        emoji2.setOnClickListener(this);
        emoji3.setOnClickListener(this);
        emoji4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emoji1:
                rating.setText("is me emojie 1");
                break;
            case R.id.emoji2:
                rating.setText("is me emojie 2");
                break;
            case R.id.emoji3:
                rating.setText("is me emojie 3");
                break;
            case R.id.emoji4:
                rating.setText("is me emojie 4");
                break;
        }
    }
}