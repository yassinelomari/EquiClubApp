package com.example.equiclubapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    /*private static final String URL_BASE = "https://192.168.1.100:44352/api";
    private static final String URL_WS = "/Clients/1";*/

    Client client;
    int clientId;

    TextView nameView, identityView, ensurenceDescView, licenceDescView, ensurenceView, licenceView;
    TextInputEditText emailView, phoneView, birthView, registrationView;
    CircleImageView imgClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        clientId = sharedPreferences.getInt("idUser", 0);

        VolleySingleton.handleSSLHandshake();

        nameView = findViewById(R.id.full_name);
        phoneView = findViewById(R.id.clientTele);
        emailView = findViewById(R.id.clientEmail);
        identityView = findViewById(R.id.clientIdentity);
        ensurenceDescView = findViewById(R.id.desc2);
        licenceDescView = findViewById(R.id.desc1);
        birthView = findViewById(R.id.birthClient);
        registrationView = findViewById(R.id.registerClient);
        ensurenceView = findViewById(R.id.EnsurenceClient);
        licenceView = findViewById(R.id.LicenceClient);
        imgClient = findViewById(R.id.profile_image);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ApiUrls.BASE + ApiUrls.CLIENTS_WS
                + clientId, null, (resp) -> {
            //Log.d(ProfileActivity.class.getSimpleName(),resp.toString());
            try {
                //Log.d(ProfileActivity.class.getSimpleName(),"len" + resp.length());
                String fName = (resp.getString("fName") == "null") ? null:
                        resp.getString("fName");
                String lName = (resp.getString("lName") == "null") ? null:
                        resp.getString("lName");
                String email = (resp.getString("clientEmail") == "null") ? null:
                        resp.getString("clientEmail");
                String phone = (resp.getString("clientPhone") == "null") ? null:
                        resp.getString("clientPhone");
                String idDoc = (resp.getString("identityDoc") == "null") ? "":
                        resp.getString("identityDoc");
                String idNum = (resp.getString("identityNumber") == "null") ? null:
                        resp.getString("identityNumber");
                boolean isActive = resp.getBoolean("isActive");
                String pathPhoto = resp.getString("photo");
                LocalDateTime birthDate = LocalDateTime.parse(resp.getString("birthDate"),
                        DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime registrationDate = LocalDateTime.parse(
                        resp.getString("inscriptionDate"), DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime ensurenceDate = LocalDateTime.parse(
                        resp.getString("ensurenceValidity"), DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime licenceDate = LocalDateTime.parse(
                        resp.getString("licenceValidity"), DateTimeFormatter.ISO_DATE_TIME);
                client = new Client(clientId, fName, lName, birthDate, pathPhoto, idDoc, idNum,
                        registrationDate, email, phone, ensurenceDate, licenceDate, isActive);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                nameView.setText(client.getfName() + " " + client.getlName());
                phoneView.setText(client.getClientPhone());
                emailView.setText(client.getClientEmail());
                identityView.setText(client.getIdentityNumber());
                birthView.setText(client.getBirthDate().format(formatter));
                registrationView.setText(client.getInscriptionDate().format(formatter));
                long ensurDays = ChronoUnit.DAYS.between(LocalDateTime.now(),
                        client.getEnsurenceValidity());
                long licDays = ChronoUnit.DAYS.between(LocalDateTime.now(),
                        client.getLicenceValidity());
                ensurenceView.setText(((ensurDays > 0) ? ensurDays : 0) + " Jrs");
                licenceView.setText(((licDays > 0) ? licDays : 0) + " Jrs");
                ensurenceDescView.setText(((ensurDays > 0) ? "valide" : "expiré"));
                licenceDescView.setText(((licDays > 0) ? "valide" : "expiré"));
                VolleySingleton.getInstance(getApplicationContext()).getImageLoader().get(
                        ApiUrls.BASE + ApiUrls.PHOTO_CLIENTID_WS + clientId,
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response,
                                                   boolean isImmediate) {
                                client.setPhoto(response.getBitmap());
                                imgClient.setImageBitmap(client.getPhoto());
                            }
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(ProfileActivity.class.getSimpleName(),error.getMessage());
                            }
                        }
                );
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        }, (error) -> Log.e(ProfileActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}