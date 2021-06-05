package com.example.equiclubapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.CustomDecoration;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Event;
import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.data.IEvent;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class DayTasksActivity extends AppCompatActivity {

    int userId;
    String role;
    String day;

    CalendarDayView dayView;
    ArrayList<IEvent> events;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_tasks);
        VolleySingleton.handleSSLHandshake();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        userId = sharedPreferences.getInt("idUser", 0);
        role = sharedPreferences.getString("role", "");

        Bundle extras = getIntent().getExtras();
        day = extras.getString("day");

        Toast.makeText(getApplicationContext(), role + userId + day,
                Toast.LENGTH_LONG).show();

        dayView = (CalendarDayView) findViewById(R.id.calendar);

        dayView.setLimitTime(8, 22);
        events = new ArrayList<>();

        if(role.equals("MONITOR")){
            getSeances();
        } else {
            getTasks();
        }


        //dayView.setEvents(events);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTasks() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                ApiUrls.BASE + ApiUrls.TASKS_WS + userId + "/" + day,
                null, resp -> {
            Log.e(DayTasksActivity.class.getSimpleName(), "tasks : " +  resp.toString());
            for (int i = 0; i < resp.length(); i++) {
                try {
                    LocalDateTime startDate = LocalDateTime.parse(
                            resp.getJSONObject(i).getString("startDate"),
                            DateTimeFormatter.ISO_DATE_TIME);
                    int eventColor1 = getResources().getColor(R.color.tache);
                    Calendar timeStart1 = Calendar.getInstance();
                    timeStart1.set(Calendar.HOUR_OF_DAY, startDate.getHour());
                    timeStart1.set(Calendar.MINUTE, startDate.getMinute());
                    Calendar timeEnd1 = (Calendar) timeStart1.clone();
                    timeEnd1.add(Calendar.MINUTE, resp.getJSONObject(i).getInt("durationMinut"));
                    Event event1 = new Event( resp.getJSONObject(i).getInt("taskId"),
                            timeStart1, timeEnd1, resp.getJSONObject(i).getString("detail"),
                            "", eventColor1);
                    event1.setTitle(resp.getJSONObject(i).getString("title"));
                    events.add(event1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            dayView.setDecorator(new CustomDecoration(DayTasksActivity.this));
            dayView.setEvents(events);
        },
                error -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getSeances() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                ApiUrls.BASE + ApiUrls.SEANCES_WS + userId + "/" + day,
                null, resp -> {
            Log.e(DayTasksActivity.class.getSimpleName(), "seances : " + resp.toString());
            for (int i = 0; i < resp.length(); i++) {
                try {
                    LocalDateTime startDate = LocalDateTime.parse(
                            resp.getJSONObject(i).getString("startDate"),
                            DateTimeFormatter.ISO_DATE_TIME);
                    int eventColor1 = getResources().getColor(R.color.seance);
                    Calendar timeStart1 = Calendar.getInstance();
                    timeStart1.set(Calendar.HOUR_OF_DAY, startDate.getHour());
                    timeStart1.set(Calendar.MINUTE, startDate.getMinute());
                    Calendar timeEnd1 = (Calendar) timeStart1.clone();
                    timeEnd1.add(Calendar.MINUTE, resp.getJSONObject(i).getInt("durationMinut"));
                    int idS = resp.getJSONObject(i).getInt("seanceId");
                    Event event1 = new Event( idS* 100000, timeStart1, timeEnd1, "", "", eventColor1);
                    event1.setTitle("Seance");
                    events.add(event1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            getTasks();
        },
                error -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}