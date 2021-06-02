package com.example.equiclubapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.AutoCompleteClientAdapter;
import com.example.equiclubapp.ListesAdapters.AutoCompleteMonitorAdapter;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;
import com.example.equiclubapp.Models.Seance;
import com.example.equiclubapp.Models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditSeanceActivity extends AppCompatActivity {
   /* private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS_CL = "/Clients/";
    private static final String URL_WS_SC = "/Seances/";
    private static final String URL_WS_GP = "/Seances/groups/";
    private static final String URL_WS_MX_GP = "/Seances/groupIdMax/";
    private static final String URL_WS_MN = "/Users/";*/

    Seance seance;
    int action;

    List<Client> clients;
    List<User> users;
    List<String> seanceGrps;
    int idClient, idGrp, idMonitor;
    LocalDateTime dateSeance;
    MaterialDatePicker materialDatePicker;
    TimePickerDialog timePicker;

    MaterialButton selectDate, selectTime, saveButton;
    TextInputEditText dateStart, timeStart, editDurat;
    AutoCompleteTextView editClient, editGrp, editMonitor;
    CheckBox nvGroup;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seance);

        /*Bundle extras = getIntent().getExtras();
        action = extras.getInt("requestCode");*/


        clients = new ArrayList<>();
        users = new ArrayList<>();
        seanceGrps = new ArrayList<>();

        VolleySingleton.handleSSLHandshake();

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selectioner la date");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        CalendarConstraints.Builder cBuilder = new CalendarConstraints.Builder();
        cBuilder.setValidator(DateValidatorPointForward.now());
        builder.setCalendarConstraints(cBuilder.build());
        materialDatePicker = builder.build();

        timePicker = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener)
                this::onSelectTime , 0, 0, true);

        selectDate = findViewById(R.id.SelectDateSeance);
        selectTime = findViewById(R.id.SelectTimeSeance);
        dateStart = findViewById(R.id.addDateSeance);
        timeStart = findViewById(R.id.addTimeSeance);
        editDurat = findViewById(R.id.addDurSeance);
        editClient = findViewById(R.id.addClientSeance);
        editGrp = findViewById(R.id.addGrpSeance);
        editMonitor = findViewById(R.id.addMonitorSeance);
        nvGroup = findViewById(R.id.nvGroup);
        saveButton = findViewById(R.id.addSeance);

        editMonitor.setFocusable(false);
        editMonitor.setFocusableInTouchMode(false);
        editMonitor.setDropDownHeight(0);
        dateStart.setFocusable(false);
        timeStart.setFocusable(false);
        selectDate.setEnabled(false);
        selectTime.setEnabled(false);
        nvGroup.setOnCheckedChangeListener(this::setOnNvGrpChangeListener);
        editClient.setOnItemClickListener((adapterView, view, i, l) -> {
            Object item = adapterView.getItemAtPosition(i);
            Log.d(EditSeanceActivity.class.getSimpleName(),"ccc :" + item);
            if (item instanceof Client){
                Client client=(Client) item;
                idClient = client.getClientId();
            }
        });
        editGrp.setOnItemClickListener((adapterView, view, i, l) -> {
            String grpItem = editGrp.getText().toString().trim();
            String[] grpInfo = grpItem.split("\\|");
            idGrp = Integer.parseInt(grpInfo[0]);
            idMonitor = Integer.parseInt(grpInfo[3]);
            dateSeance = LocalDateTime.parse(grpInfo[1], DateTimeFormatter.ISO_DATE_TIME);
        });
        editMonitor.setOnItemClickListener((adapterView, view, i, l) -> {
            Object item = adapterView.getItemAtPosition(i);
            if (item instanceof User){
                User user=(User) item;
                idMonitor = user.getUserId();
            }
        });
        selectDate.setOnClickListener(this::onClickButtons);
        selectTime.setOnClickListener(this::onClickButtons);
        saveButton.setOnClickListener(this::onClickButtons);
        materialDatePicker.addOnPositiveButtonClickListener(this::onSelectDate);

        /*if (action == 2) {
            seance = extras.getParcelable("seance");
            showAdapter();
        }*/
    }

    /*private void showAdapter() {
        findViewById(R.id.layoutSeanceClient).setVisibility(View.INVISIBLE);
        selectDate.setVisibility(View.INVISIBLE);
        selectTime.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        nvGroup.setVisibility(View.INVISIBLE);
        findViewById(R.id.layoutSeanceGrp).getLayoutParams().width = ConstraintLayout.LayoutParams
                .MATCH_PARENT;
        findViewById(R.id.layoutSeancedate).getLayoutParams().width = ConstraintLayout.LayoutParams
                .MATCH_PARENT;
        findViewById(R.id.layoutSeanceTime).getLayoutParams().width = ConstraintLayout.LayoutParams
                .MATCH_PARENT;
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ApiUrls.BASE + ApiUrls.CLIENTS_WS,
                null, (resp) -> {
            try {
                if(clients.isEmpty()) {
                    for (int i = 0; i < resp.length(); i++) {
                        int id = resp.getJSONObject(i).getInt("clientId");
                        String fName = resp.getJSONObject(i).getString("fName");
                        String lName = resp.getJSONObject(i).getString("lName");
                        String email = resp.getJSONObject(i).getString("clientEmail");
                        String phone = resp.getJSONObject(i).getString("clientPhone");
                        String idDoc = resp.getJSONObject(i).getString("identityDoc");
                        String idNum = resp.getJSONObject(i).getString("identityNumber");
                        String pathPhoto = resp.getJSONObject(i).getString("photo");
                        clients.add(new Client(id, fName, lName, email, phone, idDoc, idNum, pathPhoto));
                    }
                }
                if(!clients.isEmpty())
                    editClient.setAdapter(new AutoCompleteClientAdapter(EditSeanceActivity.this, clients));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EditSeanceActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        },
                (error) -> Log.e(EditSeanceActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);

        JsonArrayRequest requestSGrps = new JsonArrayRequest(Request.Method.GET, ApiUrls.BASE +
                ApiUrls.GP_SC_WS, null, (resp) -> {
            try {
                //Log.d(EditSeanceActivity.class.getSimpleName(),"len :" + resp);
                if(seanceGrps.isEmpty()) {
                    for (int i = 0; i < resp.length(); i++) {
                        int grpId = resp.getJSONObject(i).getInt("seanceGrpId");
                        String startDate = resp.getJSONObject(i).getString("startDate");
                        String monitor = resp.getJSONObject(i).getString("monitor");
                        seanceGrps.add(grpId + "|" + startDate.substring(0, 16) + "|" + monitor);
                    }
                }
                if(!seanceGrps.isEmpty()){
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            R.layout.doc_list_item, seanceGrps);
                    editGrp.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EditSeanceActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        },
                (error) -> Log.e(EditSeanceActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(requestSGrps);

        JsonArrayRequest requestUsers = new JsonArrayRequest(Request.Method.GET, ApiUrls.BASE +
                ApiUrls.USERS_WS, null, (resp) -> {
            try {

                if(users.isEmpty()) {
                    for (int i = 0; i < resp.length(); i++) {
                        int id = resp.getJSONObject(i).getInt("userId");
                        String fName = resp.getJSONObject(i).getString("userFname");
                        String lName = resp.getJSONObject(i).getString("userLname");
                        String email = resp.getJSONObject(i).getString("userEmail");
                        String phone = resp.getJSONObject(i).getString("userPhone");
                        String userType = resp.getJSONObject(i).getString("userType");
                        String pathPhoto = resp.getJSONObject(i).getString("userphoto");
                        //Log.d(EditSeanceActivity.class.getSimpleName(),"userType :" + userType);
                        if(userType.equals("MONITOR")){
                            users.add(new User(id, email, fName, lName, userType, pathPhoto,phone));
                        }
                    }
                }
                //Log.d(EditSeanceActivity.class.getSimpleName(),"users :" + users);
                if(!users.isEmpty()){
                    editMonitor.setAdapter(new AutoCompleteMonitorAdapter(EditSeanceActivity.this, users));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EditSeanceActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        },
                (error) -> Log.e(EditSeanceActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(requestUsers);
    }

    private void setOnNvGrpChangeListener(CompoundButton compoundButton, boolean b) {
        if (b) {
            editMonitor.setFocusable(true);
            editMonitor.setFocusableInTouchMode(true);
            editMonitor.setDropDownHeight(ConstraintLayout.LayoutParams.WRAP_CONTENT);
            selectDate.setEnabled(true);
            selectTime.setEnabled(true);
            editGrp.setFocusable(false);
        } else {
            editMonitor.setFocusable(false);
            editMonitor.setFocusableInTouchMode(false);
            editMonitor.setDropDownHeight(0);
            selectDate.setEnabled(false);
            selectTime.setEnabled(false);
            editGrp.setFocusableInTouchMode(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClickButtons(View view) {
        switch (view.getId()){
            case R.id.SelectDateSeance :
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                break;
            case R.id.SelectTimeSeance:
                timePicker.show();
                break;
            case R.id.addSeance:
                /*Toast.makeText(EditSeanceActivity.this , idClient + " | " + idGrp +
                        " | " + idMonitor, Toast.LENGTH_LONG).show();*/
                int duration = Integer.parseInt(Objects.requireNonNull(editDurat.getText())
                        .toString().trim());
                if(nvGroup.isChecked()){
                    String dateS = Objects.requireNonNull(dateStart.getText()).toString().trim();
                    String timeS = Objects.requireNonNull(timeStart.getText()).toString().trim();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyyHH:mm");
                    dateSeance = LocalDateTime.parse(dateS + timeS, formatter);
                    JsonObjectRequest requestMaxGrps = new JsonObjectRequest(Request.Method.GET,
                            ApiUrls.BASE + ApiUrls.MAX_GP_WS, null, (resp) -> {
                        try {
                           idGrp = resp.getInt("id") + 1;
                            //Log.d(EditClientActivity.class.getSimpleName(), "maxid: " + resp.getInt("id"));
                            seance = new Seance(0, idGrp, idClient, idMonitor, dateSeance,
                                    duration, true, 0, "");
                            sendRequest(Request.Method.POST, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditSeanceActivity.this , "error", 
                                    Toast.LENGTH_LONG).show();
                        }
                    }, (error) -> Log.e(EditSeanceActivity.class.getSimpleName(),error.getMessage())
                    );
                    VolleySingleton.getInstance(this).addToRequestQueue(requestMaxGrps);
                } else {
                    seance = new Seance(0, idGrp, idClient, idMonitor, dateSeance, duration,
                            true, 0, "");
                    sendRequest(Request.Method.POST, null);
                }
                break;
        }
    }

    private void sendRequest(int methode, Object id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("seanceId", id);
            jsonObject.put("seanceGrpId", seance.getSeanceGrpId());
            jsonObject.put("clientId", seance.getClientId());
            jsonObject.put("monitorId", seance.getMonitorId());
            jsonObject.put("startDate", seance.getStartDate());
            jsonObject.put("durationMinut", seance.getDurationMinut());
            jsonObject.put("isDone", seance.getDone());
            jsonObject.put("paymentId", seance.getPaymentId());
            jsonObject.put("comments", seance.getComments());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(methode, ApiUrls.BASE + ApiUrls.SEANCES_WS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(getApplicationContext(), ClientsActivity.class);
                startActivityForResult(intent, 50);
                Toast.makeText(getApplicationContext(), "seance added successfully", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("accept", "application/json");
                map.put("Content-Type", "application/json");
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void onSelectTime(TimePicker view, int hourOfDay, int minute) {
        timeStart.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
    }

    private void onSelectDate(Object o) {
        dateStart.setText(materialDatePicker.getHeaderText());
    }

}