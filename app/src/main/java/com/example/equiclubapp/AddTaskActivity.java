package com.example.equiclubapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class AddTaskActivity extends AppCompatActivity {

    Button addTask;
    MaterialButton selectDate, selectTime;
    TextInputEditText addTitleTask, addDetailTask, addDateTask, addDureeTask, timeStart;
    int userId, hour, min;
    String role;
    LocalDateTime dateSeance;
    MaterialDatePicker materialDatePicker;
    TimePickerDialog timePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        VolleySingleton.handleSSLHandshake();
        addTask = findViewById(R.id.addTask);
        Bundle extras = getIntent().getExtras();
        userId = extras.getInt("userId");
        role = extras.getString("role", "");
        //Toast.makeText(AddTaskActivity.this , "- send Id : "+extras.getInt("userId"), Toast.LENGTH_LONG)
                //.show();
        addTitleTask = findViewById(R.id.addTitleTask);
        addDetailTask = findViewById(R.id.addDetailTask);
        addDateTask = findViewById(R.id.addDateTask);
        timeStart = findViewById(R.id.addTimeTask);
        addDureeTask = findViewById(R.id.addDureeTask);
        selectDate = findViewById(R.id.SelectDateTask);
        selectTime = findViewById(R.id.SelectTimeTask);

        addTask.setOnClickListener(this::onClickAdd);
        selectDate.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
        selectTime.setOnClickListener(v -> timePicker.show());

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selectioner la date");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        CalendarConstraints.Builder cBuilder = new CalendarConstraints.Builder();
        cBuilder.setValidator(DateValidatorPointForward.now());
        builder.setCalendarConstraints(cBuilder.build());
        materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                TimeZone tz = calendar.getTimeZone();
                ZoneId zid = tz.toZoneId();
                dateSeance = LocalDateTime.ofInstant(calendar.toInstant(), zid);
                addDateTask.setText(materialDatePicker.getHeaderText());
            }
        });

        timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            hour = hourOfDay;
            min = minute;
            timeStart.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
        },
                0, 0, true);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickAdd(View v){
        LocalDateTime dateStart = LocalDateTime.of(dateSeance.getYear(),
                dateSeance.getMonthValue(), dateSeance.getDayOfMonth(), hour, min);
        /*Toast.makeText(getApplicationContext(), userId +"|" + dateStart.format(DateTimeFormatter.ISO_DATE_TIME) + "|" + addDureeTask.getText().toString()
                + "|" + addTitleTask.getText().toString() + "|" + addDetailTask.getText().toString(), Toast.LENGTH_LONG).show();*/
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("startDate", dateStart.format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("durationMinut", Objects.requireNonNull(addDureeTask.getText()).toString());
            jsonObject.put("title", Objects.requireNonNull(addTitleTask.getText()).toString());
            jsonObject.put("detail", Objects.requireNonNull(addDetailTask.getText()).toString());
            jsonObject.put("isDone", dateStart.format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("user_Fk",userId);
            jsonObject.put("taskId", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ApiUrls.BASE + ApiUrls.TASKS_WS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "task uploaded succefuly", Toast.LENGTH_LONG).show();
                Intent tt = new Intent(AddTaskActivity.this, CalendarUserActivity.class);
                tt.putExtra("requestRole", role);
                tt.putExtra("requestId", userId);
                startActivity(tt);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString() , Toast.LENGTH_LONG).show();
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        });


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }
}