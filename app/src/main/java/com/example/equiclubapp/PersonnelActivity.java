package com.example.equiclubapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PersonnelActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_personnel);
        //get CardView
        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card4 = findViewById(R.id.card4);
        CardView card5 = findViewById(R.id.card5);
        CardView card6 = findViewById(R.id.card6);
        //CardView with listener
        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card4.setOnClickListener(this);
        card5.setOnClickListener(this);
        card6.setOnClickListener(this);
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
            case R.id.card4:
                openCard4();
                break;
            case R.id.card5:
                openCard5();
                break;
            case R.id.card6:
                openCard6();
                break;
        }
    }
    public void openCard1(){
        Intent intent = new Intent(com.example.equiclubapp.PersonnelActivity.this, com.example.equiclubapp.ProfileActivity.class);
        startActivity(intent);
    }
    public void openCard2(){
        Intent intent = new Intent(com.example.equiclubapp.PersonnelActivity.this, com.example.equiclubapp.CalendarActivity.class);
        startActivity(intent);
        //if(isConnected())
          //Toast.makeText(getApplicationContext(),"card2 connected", Toast.LENGTH_SHORT).show();
        //else
            //Toast.makeText(getApplicationContext(),"card2 disconnected", Toast.LENGTH_SHORT).show();
    }
    public void openCard3(){
        Toast.makeText(getApplicationContext(),"card3 hello", Toast.LENGTH_SHORT).show();
    }
    public void openCard4(){
        Intent intent = new Intent(com.example.equiclubapp.PersonnelActivity.this, com.example.equiclubapp.RatingActivity.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(),"card4 hello", Toast.LENGTH_SHORT).show();
    }
    public void openCard5(){
        //Toast.makeText(getApplicationContext(),"card5 hello", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(com.example.equiclubapp.PersonnelActivity.this, com.example.equiclubapp.SupportActivity.class);
        startActivity(intent);
    }
    public void openCard6(){
        Intent intent = new Intent(com.example.equiclubapp.PersonnelActivity.this, FaceActivity.class);
        startActivity(intent);
        finish();
    }
    public boolean isConnected(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                connected = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                connected = true;
            }
        } else {
            connected = false;
        }
        return connected;
    }
}