package com.example.equiclubapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Seance;
import com.example.equiclubapp.Models.SeancesOpenHelper;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarActivity extends AppCompatActivity {

    int clientId;

    CompactCalendarView compactCalendarView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-YYYY", Locale.getDefault());
    private SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    TextView tx_date;
    LinearLayout ly_left, ly_right;
    String formattedDate;

    ImageView addSeance;
    //ImageView today, week, month, year;
    TextInputEditText monitor, dateStart, timeStart, duration;

    ArrayList<Seance> seances;

    SeancesOpenHelper db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);
        addSeance = findViewById(R.id.add_seance);
        VolleySingleton.handleSSLHandshake();
        Bundle extras = getIntent().getExtras();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        clientId = sharedPreferences.getInt("idUser", 0);
        String role = sharedPreferences.getString("role", "");

        addSeance.setVisibility((role.equals("ADMIN") ? View.VISIBLE : View.INVISIBLE));
        clientId = role.equals("ADMIN") ? extras.getInt("clientId") : clientId;
        Toast.makeText(CalendarActivity.this , "- Yopiiii : "+clientId, Toast.LENGTH_LONG)
                .show();
        db = new SeancesOpenHelper(this);

        addSeance.setOnClickListener(this::addSeance);
        monitor = findViewById(R.id.addMonitorSeance);
        dateStart = findViewById(R.id.addDateSeance);
        timeStart = findViewById(R.id.addTimeSeance);
        duration = findViewById(R.id.addDurSeance);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        tx_date = (TextView) findViewById(R.id.text);
        ly_left = (LinearLayout) findViewById(R.id.layout_left);
        ly_right = (LinearLayout) findViewById(R.id.layout_right);

        //today.setOnClickListener(this::calendarShowToday);

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

    public void addSeance(View v){
        Intent intent = new Intent(CalendarActivity.this, EditSeanceActivity.class);
        intent.putExtra("clientId", clientId);
        startActivity(intent);
    }

    public void calendarlistener() {
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDayClick(Date dateClicked) {
                if(!seances.isEmpty()){
                    boolean drap = true;
                    for(Seance seance:seances){
                        LocalDateTime sd = seance.getStartDate();

                        if(sd.getDayOfMonth() == dateClicked.getDate() &&
                                sd.getMonthValue() == (dateClicked.getMonth() + 1) &&
                                sd.getYear() == (dateClicked.getYear() +1900)) {
                            drap = false;
                            if (isConnected())
                                setMonitorName(seance.getMonitorId());
                            else
                                monitor.setText(seance.getComments());
                            dateStart.setText(seance.getStartDate().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy")));
                            timeStart.setText(seance.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm")));
                            duration.setText("" + seance.getDurationMinut());
                            //Log.e(CalendarActivity.class.getSimpleName(),"seance : " + seance);
                        }
                    }
                    if(drap) {
                        Toast.makeText(getApplicationContext(), "pas de seance dans ce jour",
                                Toast.LENGTH_LONG).show();
                        setFieldsNull();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"pas de seance dans ce jour",
                            Toast.LENGTH_LONG).show();
                    setFieldsNull();
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

    private void setFieldsNull() {
        monitor.setText(null);
        dateStart.setText(null);
        timeStart.setText(null);
        duration.setText(null);
    }

    private void setMonitorName(int monitorId) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                ApiUrls.BASE + ApiUrls.USERS_WS + monitorId,
                null, (resp) -> {
            try {
                monitor.setText(resp.getString("userFname") + " " + resp.getString("userLname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, (error) -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    //get data of current month

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void monthData(Date monthSelected){
        Log.e(CalendarActivity.class.getSimpleName(),"monthSelected : " + monthSelected);
        String month = (new SimpleDateFormat("MM")).format(monthSelected);
        String year = (new SimpleDateFormat("yyyy")).format(monthSelected);

        Date currentDate = new Date(System.currentTimeMillis());
        if (isConnected()){
            Log.e(CalendarActivity.class.getSimpleName(),"connected ");
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                    ApiUrls.BASE + ApiUrls.SEANCES_WS + clientId + "/" + month + "/" + year,
                    null, (resp) -> {
                setdate(resp);
                if(monthSelected.compareTo(currentDate) <= 0) {
                    setDataInLocalDb();
                }
            }, (error) -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage())
            );
            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } else {
            Log.e(CalendarActivity.class.getSimpleName(),"disconnected ");
            if(monthSelected.compareTo(currentDate) <= 0) {
                getDataFromDb(month, year);
            } else {
                Toast.makeText(getApplicationContext(), "Il faut se connecter a l'internet",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getDataFromDb(String month, String year) {
        seances = (ArrayList<Seance>) db.getAllSeances(clientId, Integer.parseInt(month), Integer.parseInt(year));
        //Log.e(CalendarActivity.class.getSimpleName(),"from db : " + seances);
        for(Seance newSeance:seances){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, newSeance.getStartDate().getYear());
            cal.set(Calendar.MONTH, newSeance.getStartDate().getMonthValue() - 1);
            cal.set(Calendar.DAY_OF_MONTH, newSeance.getStartDate().getDayOfMonth());
            Event event = new Event(Color.RED, cal.getTimeInMillis(), "test");
            compactCalendarView.addEvent(event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDataInLocalDb() {
        //Log.e(CalendarActivity.class.getSimpleName(), "DataInLocalDb : " + db.getAllSeances());
        List<Seance> seancesDb = db.getAllSeances();
        for (Seance s:seances) {
            if(!seancesDb.contains(s)){
                //Log.e(CalendarActivity.class.getSimpleName(),"not contains : " + s.getSeanceId());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        ApiUrls.BASE + ApiUrls.USERS_WS + s.getMonitorId(),
                        null, (resp) -> {
                    try {

                        db.addSeance(s, resp.getString("userFname") + " " + resp.getString("userLname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, (error) -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage()));
                VolleySingleton.getInstance(this).addToRequestQueue(request);
            } else {
                //Log.e(CalendarActivity.class.getSimpleName(),"contains");
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        ApiUrls.BASE + ApiUrls.USERS_WS + s.getMonitorId(),
                        null, (resp) -> {
                    try {
                        Log.e(CalendarActivity.class.getSimpleName(),"update from json :" +resp.toString());
                        db.updateSeance(s, resp.getString("userFname") + " " + resp.getString("userLname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, (error) -> Log.e(CalendarActivity.class.getSimpleName(),error.getMessage()));
                VolleySingleton.getInstance(this).addToRequestQueue(request);
            }

        }
    }


    //get current date

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setdate(JSONArray resp) {
        seances = new ArrayList();
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

    public boolean isConnected(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                connected = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                connected = false;
            }
        } else {
            connected = false;
        }
        return connected;
    }
}