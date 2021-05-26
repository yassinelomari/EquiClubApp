package com.example.equiclubapp;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity {
    CustomCalendar customCalendar;
    ImageView today, week, month, year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);

        customCalendar = findViewById(R.id.custom_calendar);

        today = findViewById(R.id.today);

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarShowToday();
            }
        });

        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        calendarShowToday();

    }

    public void calendarShowToday(){
        HashMap<Object, Property> descHashMap = new HashMap<>();

        Property defaultProperty = new Property();
        defaultProperty.layoutResource = R.layout.default_view;
        defaultProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("default", defaultProperty);
        //For current date
        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.current_view;
        currentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("current", currentProperty);
        //For present date
        Property presentProperty = new Property();
        presentProperty.layoutResource = R.layout.present_view;
        presentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("present", presentProperty);
        //For absent date
        Property absentProperty = new Property();
        absentProperty.layoutResource = R.layout.absent_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("absent", absentProperty);
        //Set descHashMap on custom calendar
        customCalendar.setMapDescToProp(descHashMap);
        //Initialize dateHashMap
        HashMap<Integer,Object> dateHashMap = new HashMap<>();
        //Initialize calendar
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.FRENCH);
        //Put values
        dateHashMap.put(calendar.get(Calendar.DAY_OF_YEAR), "current");
        dateHashMap.put(25, "present");
        dateHashMap.put(28, "present");
        dateHashMap.put(30, "present");
        //Set date
        customCalendar.setDate(calendar,dateHashMap);
        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                String sDate = selectedDate.get(Calendar.DATE)+"/";
                Toast.makeText(getApplicationContext(),sDate, Toast.LENGTH_SHORT).show();
            }
        });
    }
}