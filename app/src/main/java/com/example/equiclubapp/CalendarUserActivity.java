package com.example.equiclubapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Seance;
import com.example.equiclubapp.Models.Task;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CalendarUserActivity extends AppCompatActivity {

    int userId;
    String role;

    CompactCalendarView compactCalendarView;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat DateFormat;
    TextView tx_date;
    LinearLayout ly_left, ly_right;
    String formattedDate;

    ImageView today, week, month, year, addtask, addseance;

    ArrayList<Seance> seances;
    ArrayList<Task> tasks;
    int requestId;
    LinearLayout items;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar_user);
        Bundle extras = getIntent().getExtras();
        items = findViewById(R.id.items);
        addtask = findViewById(R.id.add_task);
        addseance = findViewById(R.id.add_seance);
        VolleySingleton.handleSSLHandshake();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        userId = sharedPreferences.getInt("idUser", 0);
        role = sharedPreferences.getString("role", "");
        String requestRole;

        if (extras == null){
            requestRole = "empty";
            requestId = 0;
        } else{
            requestId = extras.getInt("requestId", 0);
            requestRole = extras.getString("requestRole", "");
        }

        if(role.equals("MONITOR"))
            items.setVisibility(View.INVISIBLE);
        if(requestRole.equals("MONITOR")){
            items.setVisibility(View.VISIBLE);
        }else {
            addseance.setVisibility(View.INVISIBLE);
        }

        userId = role.equals("ADMIN") ? extras.getInt("requestId") : userId;
        role = role.equals("ADMIN") ? extras.getString("requestRole") : role;

        Toast.makeText(CalendarUserActivity.this , "- loliii : "+role+", send Role : "+requestRole+", send Id : "+requestId, Toast.LENGTH_LONG)
                .show();
        /*userId = 2;
        role = "MONITOR";*/

        simpleDateFormat = new SimpleDateFormat("MMMM-YYYY", Locale.getDefault());
        DateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        today = findViewById(R.id.today);
        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        tx_date = (TextView) findViewById(R.id.text);
        ly_left = (LinearLayout) findViewById(R.id.layout_left);
        ly_right = (LinearLayout) findViewById(R.id.layout_right);

        today.setOnClickListener(this::calendarShowToday);
        addtask.setOnClickListener(this::addTask);
        addseance.setOnClickListener(this::addSeance);

        calendarlistener();
        Date currentDate = new Date(System.currentTimeMillis());

        monthData(currentDate);

        tx_date.setText(simpleDateFormat.format(currentDate));

        ly_right.setOnClickListener(v -> {
            compactCalendarView.showCalendarWithAnimation();
            compactCalendarView.showNextMonth();
        });

        ly_left.setOnClickListener(v -> {
            compactCalendarView.showCalendarWithAnimation();
            compactCalendarView.showPreviousMonth();
        });
    }

    private void addSeance(View view) {
        Intent intent = new Intent(CalendarUserActivity.this, EditSeanceActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("action", "MONITOR");
        startActivity(intent);
        finish();
    }

    public void addTask(View v){
        Intent intent = new Intent(CalendarUserActivity.this, AddTaskActivity.class);
        intent.putExtra("userId", requestId);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void monthData(Date monthSelected) {
        seances = new ArrayList();
        tasks = new ArrayList<>();
        String sMonth = (new SimpleDateFormat("MM")).format(monthSelected);
        String sYear = (new SimpleDateFormat("yyyy")).format(monthSelected);
        if(role.equals("MONITOR")) {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                    ApiUrls.BASE + ApiUrls.SEANCES_MONITOR_WS + userId + "/" + sMonth + "/" + sYear,
                    null, this::setSeancedata,
                    (error) -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage())
            );
            VolleySingleton.getInstance(this).addToRequestQueue(request);
        }

        JsonArrayRequest request1 = new JsonArrayRequest(Request.Method.GET,
                ApiUrls.BASE + ApiUrls.TASKS_WS + userId + "/" + sMonth + "/" + sYear,
                null, this::setTaskdata,
                (error) -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request1);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setTaskdata(JSONArray resp) {
        try {
            for (int i = 0; i < resp.length(); i++) {

                int id = resp.getJSONObject(i).getInt("taskId");
                int user_id = resp.getJSONObject(i).getInt("user_Fk");
                int durationMinut = resp.getJSONObject(i).getInt("durationMinut");
                String title = resp.getJSONObject(i).getString("title");
                String detail = resp.getJSONObject(i).getString("detail");
                LocalDateTime startDate = LocalDateTime.parse(
                        resp.getJSONObject(i).getString("startDate"),
                        DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime isDone = LocalDateTime.parse(
                        resp.getJSONObject(i).getString("isDone"),
                        DateTimeFormatter.ISO_DATE_TIME);
                Task task = new Task(id, startDate, durationMinut, title, detail, isDone, user_id);
                tasks.add(task);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, task.getStartDate().getYear());
                cal.set(Calendar.MONTH, task.getStartDate().getMonthValue() - 1);
                cal.set(Calendar.DAY_OF_MONTH, task.getStartDate().getDayOfMonth());
                Event event = new Event(Color.BLUE, cal.getTimeInMillis(), "test");
                compactCalendarView.addEvent(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setSeancedata(JSONArray resp) {
        try {
            for (int i = 0; i < resp.length(); i++) {

                int id = resp.getJSONObject(i).getInt("seanceId");
                int seanceGrpId = resp.getJSONObject(i).getInt("seanceGrpId");
                int clientId = resp.getJSONObject(i).getInt("clientId");
                int monitorId = resp.getJSONObject(i).getInt("monitorId");
                int durationMinut = resp.getJSONObject(i).getInt("durationMinut");
                int paymentId = resp.getJSONObject(i).getInt("paymentId");
                LocalDateTime startDate = LocalDateTime.parse(
                        resp.getJSONObject(i).getString("startDate"),
                        DateTimeFormatter.ISO_DATE_TIME);
                String comments = resp.getJSONObject(i).getString("comments");
                boolean isDone = resp.getJSONObject(i).getBoolean("isDone");
                Seance newSeance = new Seance(id, seanceGrpId, clientId, monitorId, startDate,
                        durationMinut, isDone, paymentId, comments);
                seances.add(newSeance);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, newSeance.getStartDate().getYear());
                cal.set(Calendar.MONTH, newSeance.getStartDate().getMonthValue() - 1);
                cal.set(Calendar.DAY_OF_MONTH, newSeance.getStartDate().getDayOfMonth());
                Event event = new Event(Color.RED, cal.getTimeInMillis(), "test");
                compactCalendarView.addEvent(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void calendarlistener() {
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDayClick(Date dateClicked) {
                if(!seances.isEmpty() || !tasks.isEmpty()){
                    boolean drap = true;
                    for(Seance seance:seances){
                        LocalDateTime sd = seance.getStartDate();

                        if(sd.getDayOfMonth() == dateClicked.getDate() &&
                                sd.getMonthValue() == (dateClicked.getMonth() + 1) &&
                                sd.getYear() == (dateClicked.getYear() +1900)) {
                            drap = false;

                        }
                    }
                    for(Task task:tasks){
                        LocalDateTime sd = task.getStartDate();

                        if(sd.getDayOfMonth() == dateClicked.getDate() &&
                                sd.getMonthValue() == (dateClicked.getMonth() + 1) &&
                                sd.getYear() == (dateClicked.getYear() +1900)) {
                            drap = false;

                        }
                    }
                    if(drap) {
                        Toast.makeText(getApplicationContext(), "pas de tache dans ce jour",
                                Toast.LENGTH_LONG).show();
                    } else {
                        LocalDate date = LocalDate.of((dateClicked.getYear() +1900),
                                (dateClicked.getMonth() + 1), dateClicked.getDate());
                        Intent intent = new Intent(CalendarUserActivity.this,
                                DayTasksActivity.class);
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        if(sharedPreferences.getString("role", "").equals("ADMIN")) {
                            intent.putExtra("idUser", userId);
                            intent.putExtra("role", role);
                        }
                        intent.putExtra("day", date.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME));
                        startActivity(intent);
                        /*Toast.makeText(getApplicationContext(), "il y'a des taches dans ce jour",
                                Toast.LENGTH_LONG).show();*/
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"pas de seance dans ce jour",
                            Toast.LENGTH_LONG).show();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                compactCalendarView.removeAllEvents();
                monthData(firstDayOfNewMonth);
                //Setdate();
                tx_date.setText(simpleDateFormat.format(firstDayOfNewMonth));

            }
        });
    }

    private void calendarShowToday(View view) {
        Intent intent = new Intent(CalendarUserActivity.this,
                CalendarUserActivity.class);
        startActivity(intent);
        finish();
    }
}