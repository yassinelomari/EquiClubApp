package com.example.equiclubapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClientDetailActivity extends AppCompatActivity {

    /*private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Clients/";
    private static final String URL_WS_DC = "/Clients/disable/";
    private static final String URL_WS_EC = "/Clients/enable/";
    private static final String URL_PHOTO = "/Clients/photo/";*/

    Client client;
    int clientId;

    TextView nameView, phoneView, emailView, identityView, identityDocView, birthView;
    TextView registrationView, ensurenceView, licenceView;
    ImageButton btnCall, btnSendEmail;
    Button edit, delete, calender, disable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        Bundle extras = getIntent().getExtras();
        clientId = extras.getInt("clientId");

        nameView = findViewById(R.id.clientName);
        phoneView = findViewById(R.id.clientTele);
        emailView = findViewById(R.id.clientEmail);
        identityView = findViewById(R.id.clientIdentity);
        identityDocView = findViewById(R.id.idDoc);
        birthView = findViewById(R.id.birthClient);
        registrationView = findViewById(R.id.registerClient);
        ensurenceView = findViewById(R.id.EnsurenceClient);
        licenceView = findViewById(R.id.LicenceClient);
        btnCall = findViewById(R.id.btnCallClient);
        btnSendEmail = findViewById(R.id.btnEmailClient);
        edit = findViewById(R.id.btnEditClient);
        delete = findViewById(R.id.btnDeleteClient);
        calender = findViewById(R.id.calendarClient);
        disable = findViewById(R.id.btnDisableClient);

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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ApiUrls.BASE + ApiUrls.CLIENTS_WS
                + clientId, null, (resp) -> {
            //Log.d(ClientDetailActivity.class.getSimpleName(),resp.toString());
            try {
                //Log.d(ClientDetailActivity.class.getSimpleName(),"len" + resp.length());
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
                identityDocView.setText(client.getIdentityDoc());
                birthView.setText(client.getBirthDate().format(formatter));
                registrationView.setText(client.getInscriptionDate().format(formatter));
                ensurenceView.setText(client.getEnsurenceValidity().format(formatter));
                licenceView.setText(client.getLicenceValidity().format(formatter));
                VolleySingleton.getInstance(getApplicationContext()).getImageLoader().get(
                        ApiUrls.BASE + ApiUrls.PHOTO_WS + client.getPathPhoto(),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response,
                                                   boolean isImmediate) {
                                client.setPhoto(response.getBitmap());
                                CircleImageView img = findViewById(R.id.clientDetailImg);
                                img.setImageBitmap(client.getPhoto());
                                if(client.isActive())
                                    img.setBorderColor(Color.GREEN);
                                else {
                                    img.setBorderColor(Color.RED);
                                    //disable.setText("Activer");
                                }
                            }
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(ClientDetailActivity.class.getSimpleName(),error.getMessage());
                            }
                        }
                );
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ClientDetailActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        }, (error) -> Log.e(ClientDetailActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void onClickButtons(View view) {
        switch (view.getId()){
            case R.id.btnCallClient :
                if(client!=null && client.getClientPhone()!= null) {
                    Intent iCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +
                            client.getClientPhone()));
                    startActivity(iCall);
                } else {
                    Toast.makeText(view.getContext(), "vous ne pouvez pas l'appeler",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnEmailClient:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",client.getClientEmail(), null));
                startActivity(emailIntent);
                break;
            case R.id.btnEditClient:
                Intent intent = new Intent(this, EditClientActivity.class);
                intent.putExtra("requestCode", 2);
                intent.putExtra("clientId", client.getClientId());
                startActivity(intent);
                break;
            case R.id.btnDeleteClient:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Confirmation");
                alertBuilder.setMessage("Voulez-vous supprimer le client " + client.getlName()
                                        + " " + client.getfName());
                alertBuilder.setPositiveButton("Confirm", this::onConfirmDelete);
                alertBuilder.setNegativeButton("Cancel",
                        (DialogInterface dialogInterface, int i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                break;
            case R.id.btnDisableClient:
                String fullUrl = ApiUrls.BASE;
                if (client.isActive())
                    fullUrl += ApiUrls.CLIENTS_DS_WS + client.getClientId();
                else
                    fullUrl += ApiUrls.CLIENTS_EN_WS + client.getClientId();
                JsonObjectRequest requestState = new JsonObjectRequest(Request.Method.GET,
                        fullUrl, null, (resp) -> {
                    //Log.d(ClientDetailActivity.class.getSimpleName(),resp.toString());
                    Toast.makeText(ClientDetailActivity.this , "state changed " +
                            "successfully", Toast.LENGTH_LONG).show();
                    ClientDetailActivity.this.finish();
                    ClientDetailActivity.this.startActivity(getIntent());

                }, (error) -> Log.e(ClientDetailActivity.class.getSimpleName(),error.getMessage())
                );
                VolleySingleton.getInstance(this).addToRequestQueue(requestState);
                break;
            case R.id.calendarClient:
                Intent tt = new Intent(this, CalendarActivity.class);
                tt.putExtra("clientId", clientId);
                startActivity(tt);
                break;
        }
    }

    private void onConfirmDelete(DialogInterface dialogInterface, int i) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, ApiUrls.BASE +
                ApiUrls.CLIENTS_WS + client.getClientId(),
                (String response) -> {
                    Log.d("Response", response);
                    Intent intent = new Intent(ClientDetailActivity.this, ClientsActivity.class);
                    startActivity(intent);
                    this.finish();
                    },
                error ->Log.e("Error.Response", error.getMessage())
        );
        queue.add(deleteRequest);
        dialogInterface.dismiss();
        Toast.makeText(this, "Client supprimer avec succés", Toast.LENGTH_LONG).show();
    }
}