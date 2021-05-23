package com.example.equiclubapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;
import com.example.equiclubapp.Models.User;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity  {
    private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Users/";
    private static final String URL_WS_DC = "/Users/disable/";
    private static final String URL_WS_EC = "/Users/enable/";
    private static final String URL_PHOTO = "/Clients/photo/";


    User user;
    int userId;

    TextView nameView, phoneView, emailView, typeView, contractView, loginView;
    ImageButton btnCall, btnSendEmail;
    Button edit, delete, calender, disable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Bundle extras = getIntent().getExtras();
        user = extras.getParcelable("user");
        if(user == null)
            Log.e(UserDetailActivity.class.getSimpleName(), "probleeeema");

        nameView = findViewById(R.id.userName);
        phoneView = findViewById(R.id.userTele);
        emailView = findViewById(R.id.userEmail);
        typeView = findViewById(R.id.userType);
        contractView = findViewById(R.id.contractUser);
        loginView = findViewById(R.id.loginUser);
        btnCall = findViewById(R.id.btnCallUser);
        btnSendEmail = findViewById(R.id.btnEmailUser);
        edit = findViewById(R.id.btnEditUser);
        delete = findViewById(R.id.btnDeleteUser);
        calender = findViewById(R.id.calendarUser);
        disable = findViewById(R.id.btnDisableUser);

        btnCall.setOnClickListener(this::onClickButtons);
        btnSendEmail.setOnClickListener(this::onClickButtons);
        edit.setOnClickListener(this::onClickButtons);
        delete.setOnClickListener(this::onClickButtons);
        calender.setOnClickListener(this::onClickButtons);
        disable.setOnClickListener(this::onClickButtons);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        nameView.setText(user.getUserFname() + " " + user.getUserLname());
        phoneView.setText(user.getUserPhone());
        emailView.setText(user.getUserEmail());
        typeView.setText(user.getUserType());
        contractView.setText(user.getContractDate().format(formatter));
        loginView.setText(user.getLastLoginTime().format(formatter));
        VolleySingleton.getInstance(getApplicationContext()).getImageLoader().get(
                URL_BASE + URL_PHOTO + user.getUserphoto(),
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response,
                                           boolean isImmediate) {
                        user.setPhoto(response.getBitmap());
                        CircleImageView img = findViewById(R.id.userDetailImg);
                        img.setImageBitmap(user.getPhoto());
                        if(user.isUserActive())
                            img.setBorderColor(Color.GREEN);
                        else {
                            img.setBorderColor(Color.RED);
                            disable.setText("Activer");
                        }
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(ClientDetailActivity.class.getSimpleName(),error.getMessage());
                    }
                }
        );
    }

    private void onClickButtons(View view) {

    }
}