package com.example.equiclubapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_admin);
        //get CardView
        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        //CardView with listener
        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card1:
                openCard1();
                break;
            case R.id.card2:
                openCard2();
                break;
            case R.id.card3:
                openCard3();
                break;
        }
    }
    public void openCard1(){
        Intent intent = new Intent(com.example.equiclubapp.AdminActivity.this, com.example.equiclubapp.UsersActivity.class);
        startActivity(intent);
    }
    public void openCard2(){
        Intent intent = new Intent(com.example.equiclubapp.AdminActivity.this, com.example.equiclubapp.ClientsActivity.class);
        startActivity(intent);
    }
    public void openCard3(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(com.example.equiclubapp.AdminActivity.this, FaceActivity.class);
        startActivity(intent);
        finish();
    }
}