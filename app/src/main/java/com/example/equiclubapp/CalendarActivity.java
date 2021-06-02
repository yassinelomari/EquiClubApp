package com.example.equiclubapp;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;

public class CalendarActivity extends AppCompatActivity {
    ImageView today, week, month, year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);
        today = findViewById(R.id.today);
        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
    }
}