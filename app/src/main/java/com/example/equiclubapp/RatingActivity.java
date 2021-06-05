package com.example.equiclubapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    int clientId;

    ImageView emoji1, emoji2, emoji3, emoji4;
    TextInputEditText rating;
    Button rateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rating);

        VolleySingleton.handleSSLHandshake();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        clientId = sharedPreferences.getInt("idUser", 0);
        // initialise rating field
        rating = findViewById(R.id.rating);
        //get ImageView
        emoji1 = findViewById(R.id.emoji1);
        emoji2 = findViewById(R.id.emoji2);
        emoji3 = findViewById(R.id.emoji3);
        emoji4 = findViewById(R.id.emoji4);
        rateButton = findViewById(R.id.saveRate);
        //ImageView with listener
        emoji1.setOnClickListener(this);
        emoji2.setOnClickListener(this);
        emoji3.setOnClickListener(this);
        emoji4.setOnClickListener(this);
        rateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emoji4:
                rating.setText("Love it");
                break;
            case R.id.emoji3:
                rating.setText("Like it");
                break;
            case R.id.emoji2:
                rating.setText("It is OK");
                break;
            case R.id.emoji1:
                rating.setText("Dislike it");
                break;
            case R.id.saveRate:
                String rate = Objects.requireNonNull(rating.getText()).toString();
                if(!rate.equals("")){
                    JsonObjectRequest requestState = new JsonObjectRequest(Request.Method.GET,
                            ApiUrls.BASE + ApiUrls.CLIENTS_WS + "rating/" + clientId + "/" + rate, null,
                            (resp) -> {
                        Log.e(RatingActivity.class.getSimpleName(), "sss : " + resp.toString());
                        Toast.makeText(RatingActivity.this , "rate changed " +
                                "successfully", Toast.LENGTH_LONG).show();
                        /*ClientDetailActivity.this.finish();
                        ClientDetailActivity.this.startActivity(getIntent());*/

                    }, (error) -> Log.e(ClientDetailActivity.class.getSimpleName(),error.getMessage())
                    );
                    VolleySingleton.getInstance(this).addToRequestQueue(requestState);
                }
                break;
        }
    }
}