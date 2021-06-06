package com.example.equiclubapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity {

    Button addTask;
    TextInputEditText addTitleTask, addDetailTask, addDateTask, addDureeTask;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        addTask = findViewById(R.id.addTask);
        Bundle extras = getIntent().getExtras();
        userId = extras.getInt("userId");
        //Toast.makeText(AddTaskActivity.this , "- send Id : "+extras.getInt("userId"), Toast.LENGTH_LONG)
                //.show();
        addTitleTask = findViewById(R.id.addTitleTask);
        addDetailTask = findViewById(R.id.addDetailTask);
        addDateTask = findViewById(R.id.addDateTask);
        addDureeTask = findViewById(R.id.addDureeTask);

        addTask.setOnClickListener(this::onClickAdd);
    }
    public void onClickAdd(View v){
        JSONObject jsonObject = new JSONObject();
        Toast.makeText(AddTaskActivity.this ,""+ Objects.requireNonNull(addDateTask.getText()).toString().trim(), Toast.LENGTH_LONG).show();
//        try {
//            jsonObject.put("startDate", Objects.requireNonNull(addDateTask.getText()).toString().trim());
//            jsonObject.put("durationMinut", Objects.requireNonNull(addDureeTask.getText()).toString().trim());
//            jsonObject.put("title", Objects.requireNonNull(addTitleTask.getText()).toString().trim());
//            jsonObject.put("detail", Objects.requireNonNull(addDetailTask.getText().toString()).trim());
//            jsonObject.put("isDone", Objects.requireNonNull(addDateTask.getText()).toString().trim());
//            jsonObject.put("user_Fk",userId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ApiUrls.BASE + ApiUrls.TASKS_WS,
//                jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Toast.makeText(getApplicationContext(), "task uploaded succefuly", Toast.LENGTH_LONG).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.toString() , Toast.LENGTH_LONG).show();
//                // As of f605da3 the following should work
//                NetworkResponse response = error.networkResponse;
//                if (error instanceof ServerError && response != null) {
//                    try {
//                        String res = new String(response.data,
//                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                        // Now you can use any deserializer to make sense of data
//                        JSONObject obj = new JSONObject(res);
//                    } catch (UnsupportedEncodingException e1) {
//                        // Couldn't properly decode data to string
//                        e1.printStackTrace();
//                    } catch (JSONException e2) {
//                        // returned data is not JSONObject?
//                        e2.printStackTrace();
//                    }
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("accept", "application/json");
//                map.put("Content-Type", "application/json");
//                return map;
//            }
//        };
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(request);

    }
}