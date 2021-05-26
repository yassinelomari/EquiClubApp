package com.example.equiclubapp;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

public class ProfileActivity extends AppCompatActivity {
    private static final String URL_BASE = "https://192.168.1.100:44352/api";
    private static final String URL_WS = "/Clients/1";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_BASE + URL_WS,
                null, (resp) -> {
            Toast.makeText(getApplicationContext(),"card2 connected", Toast.LENGTH_SHORT).show();
        },
                (error) -> Toast.makeText(getApplicationContext(),"card2 connected", Toast.LENGTH_SHORT).show()

        );
    }
}