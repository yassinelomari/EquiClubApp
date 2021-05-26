package com.example.equiclubapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;
import com.example.equiclubapp.Models.User;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity  {
    /*private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Users/";
    private static final String URL_WS_DC = "/Users/disable/";
    private static final String URL_WS_EC = "/Users/enable/";
    private static final String URL_PHOTO = "/Clients/photo/";*/

    Map<String, String> types;
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

        types = new HashMap<>();
        types.put("OTHER", "");
        types.put("MONITOR", "Moniteur");
        types.put("ADMIN", "Admin");
        types.put("SERVICE ", "Service");
        types.put("GUARD", "Gardien");
        types.put("COMPTA", "Comptable");

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
        typeView.setText(types.get(user.getUserType()));
        contractView.setText(user.getContractDate().format(formatter));
        loginView.setText(user.getLastLoginTime().format(formatter));
        VolleySingleton.getInstance(getApplicationContext()).getImageLoader().get(
                ApiUrls.BASE + ApiUrls.PHOTO_WS + user.getUserphoto(),
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
        switch (view.getId()){
            case R.id.btnCallUser :
                if(user!=null && user.getUserPhone()!= null) {
                    Intent iCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +
                            user.getUserPhone()));
                    startActivity(iCall);
                } else {
                    Toast.makeText(view.getContext(), "vous ne pouvez pas l'appeler",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnEmailUser:

                if(user!=null && user.getUserEmail()!= null) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", user.getUserEmail(), null));
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(view.getContext(),
                            "vous ne pouvez pas le contacter par email",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnEditUser:
                Intent intent = new Intent(this, EditUserActivity.class);
                intent.putExtra("requestCode", 2);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.btnDeleteUser:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Confirmation");
                alertBuilder.setMessage("Voulez-vous supprimer l'utilisateur " + user.getUserFname()
                        + " " + user.getUserLname());
                alertBuilder.setPositiveButton("Confirm", this::onConfirmDelete);
                alertBuilder.setNegativeButton("Cancel",
                        (DialogInterface dialogInterface, int i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                break;
            case R.id.btnDisableUser:
                String fullUrl = ApiUrls.BASE;
                if (user.isUserActive()) {
                    fullUrl += ApiUrls.USERS_DS_WS + user.getUserId();
                    user.setActive(false);
                }else {
                    fullUrl += ApiUrls.USERS_EN_WS + user.getUserId();
                    user.setActive(true);
                }
                JsonObjectRequest requestState = new JsonObjectRequest(Request.Method.GET,
                        fullUrl, null, (resp) -> {
                    //Log.d(ClientDetailActivity.class.getSimpleName(),resp.toString());
                    Toast.makeText(UserDetailActivity.this , "state changed " +
                            "successfully", Toast.LENGTH_LONG).show();
                    UserDetailActivity.this.finish();
                    UserDetailActivity.this.startActivity(getIntent().putExtra("user", user));

                }, (error) -> Log.e(ClientDetailActivity.class.getSimpleName(),error.getMessage())
                );
                VolleySingleton.getInstance(this).addToRequestQueue(requestState);
                break;
            case R.id.calendarUser:

                break;
        }
    }

    private void onConfirmDelete(DialogInterface dialogInterface, int i) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, ApiUrls.BASE +
                ApiUrls.USERS_WS + user.getUserId(),
                (String response) -> {
                    Log.d("Response", response);
                    Intent intent = new Intent(UserDetailActivity.this, UsersActivity.class);
                    startActivity(intent);
                    Toast.makeText(UserDetailActivity.this, "L'utilisateur supprimer avec succés", Toast.LENGTH_LONG).show();
                },
                error ->Log.e("Error.Response", error.getMessage())
        );
        queue.add(deleteRequest);
        dialogInterface.dismiss();
    }
}