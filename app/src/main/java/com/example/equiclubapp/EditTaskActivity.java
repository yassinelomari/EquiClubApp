package com.example.equiclubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Seance;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class EditTaskActivity extends AppCompatActivity {
    private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS_UR = "/Users/";

    AutoCompleteTextView editUser;
    TextInputEditText dateStart, timeStart, editDetail, editTitle;
    MaterialButton selectDate, selectTime, saveButton;

    MaterialDatePicker materialDatePicker;
    TimePickerDialog timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        VolleySingleton.handleSSLHandshake();

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selectioner la date");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        CalendarConstraints.Builder cBuilder = new CalendarConstraints.Builder();
        cBuilder.setValidator(DateValidatorPointForward.now());
        builder.setCalendarConstraints(cBuilder.build());
        materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener((obj) ->
                dateStart.setText(materialDatePicker.getHeaderText()));

        timePicker = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener)
                (view, hourOfDay, minute) -> timeStart.setText(String.format("%02d", hourOfDay)
                        + ":" + String.format("%02d", minute)), 0, 0,
                true);

        editUser = findViewById(R.id.addUserTask);
        dateStart = findViewById(R.id.addDateTask);
        timeStart = findViewById(R.id.addTimeTask);
        editDetail = findViewById(R.id.addDetailTask);
        editTitle = findViewById(R.id.addTitleTask);
        selectDate = findViewById(R.id.SelectDateTask);
        selectTime = findViewById(R.id.SelectTimeTask);
        saveButton = findViewById(R.id.addTask);

        selectDate.setOnClickListener(this::onClickButtons);
        selectTime.setOnClickListener(this::onClickButtons);
        saveButton.setOnClickListener(this::onClickButtons);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void onClickButtons(View view) {
        switch (view.getId()){
            case R.id.SelectDateTask :
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                break;
            case R.id.SelectTimeTask:
                timePicker.show();
                break;
            case R.id.addTask:

                break;
        }
    }

}