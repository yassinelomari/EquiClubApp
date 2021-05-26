package com.example.equiclubapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditClientActivity extends AppCompatActivity {
    /*private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Clients/";
    private static final String URL_PHOTO = "/Clients/photo/";*/
    private static final int PERMISSION_FILE = 23;
    private static final int ACCESS_FILE = 43;

    Client client;
    int action;
    int clientId;
    boolean imgModified;

    CircleImageView clientImg;
    ImageButton chooseImgBtn;
    Button save;
    Button modify;

    TextInputEditText editFName;
    TextInputEditText editLName;
    TextInputEditText editNumDoc;
    TextInputEditText editEmail;
    TextInputEditText editTele;
    TextInputEditText editBirth;
    TextInputEditText editInscription;
    TextInputEditText editEnsurence;
    TextInputEditText editLicence;
    AutoCompleteTextView docIdField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);
        Bundle extras = getIntent().getExtras();
        action = extras.getInt("requestCode");
        imgModified = false;
        if (action == 2)
            clientId = extras.getInt("clientId");

        docIdField = findViewById(R.id.addClientDoc);
        clientImg = findViewById(R.id.clientImgEdit);
        save = findViewById(R.id.saveClient);
        modify = findViewById(R.id.modifyClient);
        editLicence = findViewById(R.id.addClientLicence);
        editEnsurence = findViewById(R.id.addClientEnsurence);
        editInscription = findViewById(R.id.addClientRegister);
        editBirth = findViewById(R.id.addClientBirth);
        editTele = findViewById(R.id.addClientPhone);
        editEmail = findViewById(R.id.addClientEmail);
        editNumDoc = findViewById(R.id.addClientDocNum);
        editLName = findViewById(R.id.addClientFName);
        editFName = findViewById(R.id.addClientLName);
        chooseImgBtn = findViewById(R.id.btnAddImg);

        List<String> items = new ArrayList<>(Arrays.asList("CINE", "EPORT", "SEJOUR", ""));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.doc_list_item, items);
        docIdField.setAdapter(adapter);

        chooseImgBtn.setOnClickListener(this::onClickChooseImg);
        save.setOnClickListener(this::onClickSave);
        modify.setOnClickListener(this::onClickModify);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClickModify(View view) {
        if(imgModified) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", clientId);
                jsonObject.put("imageBase64", encodeBitmapImage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, ApiUrls.BASE
                    + ApiUrls.PHOTO_WS, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(getApplicationContext(), "Image uploaded succefuly", Toast.LENGTH_LONG).show();
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

        client.setClientId(clientId);
        client.setfName(Objects.requireNonNull(editFName.getText()).toString().trim());
        client.setlName(Objects.requireNonNull(editLName.getText()).toString().trim());
        client.setIdentityDoc(Objects.requireNonNull(docIdField.getText()).toString().trim());
        client.setIdentityNumber(Objects.requireNonNull(editNumDoc.getText()).toString().trim());
        client.setClientEmail(Objects.requireNonNull(editEmail.getText()).toString().trim());
        client.setClientPhone(Objects.requireNonNull(editTele.getText()).toString().trim());
        String sBirth = Objects.requireNonNull(editBirth.getText()).toString().trim();
        String sInscription = Objects.requireNonNull(editInscription.getText()).toString().trim();
        String sEnsurence = Objects.requireNonNull(editEnsurence.getText()).toString().trim();
        String sLicence = Objects.requireNonNull(editLicence.getText()).toString().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        client.setBirthDate(LocalDate.parse(sBirth, formatter).atStartOfDay());
        client.setInscriptionDate(LocalDate.parse(sInscription, formatter).atStartOfDay());
        client.setEnsurenceValidity(LocalDate.parse(sEnsurence, formatter).atStartOfDay());
        client.setLicenceValidity(LocalDate.parse(sLicence, formatter).atStartOfDay());

        sendRequest(Request.Method.PUT, client.getPathPhoto(), client.getClientId(),
                client.getPasswd());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        if (action == 1)
            modify.setVisibility(View.INVISIBLE);
        else if (action == 2) {
            save.setVisibility(View.INVISIBLE);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ApiUrls.BASE
                    + ApiUrls.CLIENTS_WS + clientId, null, (resp) -> {
                try {
                    String fName = (resp.getString("fName") == "null") ? null:resp.getString("fName");
                    String lName = (resp.getString("lName") == "null") ? null:resp.getString("lName");
                    String email = (resp.getString("clientEmail") == "null") ? null:resp.getString("clientEmail");
                    String phone = (resp.getString("clientPhone") == "null") ? null:resp.getString("clientPhone");
                    String idDoc = (resp.getString("identityDoc") == "null") ? "":resp.getString("identityDoc");
                    String idNum = (resp.getString("identityNumber") == "null") ? null:resp.getString("identityNumber");
                    String pathPhoto = resp.getString("photo");
                    boolean isActive = resp.getBoolean("isActive");
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
                    editFName.setText(client.getfName());
                    editLName.setText(client.getlName());
                    editTele.setText(client.getClientPhone());
                    editEmail.setText(client.getClientEmail());
                    editNumDoc.setText(client.getIdentityNumber());
                    docIdField.setText(client.getIdentityDoc());
                    editBirth.setText(client.getBirthDate().format(formatter));
                    editInscription.setText(client.getInscriptionDate().format(formatter));
                    editEnsurence.setText(client.getEnsurenceValidity().format(formatter));
                    editLicence.setText(client.getLicenceValidity().format(formatter));
                    VolleySingleton.getInstance(getApplicationContext()).getImageLoader().get(
                            ApiUrls.BASE + ApiUrls.PHOTO_WS + client.getPathPhoto(),
                            new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer response,
                                                       boolean isImmediate) {
                                    if(!imgModified) {
                                        client.setPhoto(response.getBitmap());
                                        clientImg.setImageBitmap(client.getPhoto());
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(EditClientActivity.class.getSimpleName(), error.getMessage());
                                }
                            }
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(EditClientActivity.this, "error", Toast.LENGTH_LONG)
                            .show();
                }
            }, (error) -> Log.e(EditClientActivity.class.getSimpleName(), error.getMessage())
            );
            VolleySingleton.getInstance(this).addToRequestQueue(request);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCESS_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            CropImage.activity(fileUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setActivityTitle("crop image")
                    .setFixAspectRatio(true)
                    .setCropMenuCropButtonTitle("Done")
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    clientImg.setImageBitmap(bitmap);
                    imgModified = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //clientImg.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onClickChooseImg(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FILE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "choose photo"), ACCESS_FILE);
            //clientImg.setImageResource(android.R.color.transparent);
        }
    }

    private String encodeBitmapImage() {
        BitmapDrawable drawable = (BitmapDrawable) clientImg.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClickSave(View view) {
        String fName = Objects.requireNonNull(editFName.getText()).toString().trim();
        String lName = Objects.requireNonNull(editLName.getText()).toString().trim();
        String doc = Objects.requireNonNull(docIdField.getText()).toString().trim();
        String numDoc = Objects.requireNonNull(editNumDoc.getText()).toString().trim();
        String email = Objects.requireNonNull(editEmail.getText()).toString().trim();
        String tele = Objects.requireNonNull(editTele.getText()).toString().trim();
        String sBirth = Objects.requireNonNull(editBirth.getText()).toString().trim();
        String sInscription = Objects.requireNonNull(editInscription.getText()).toString().trim();
        String sEnsurence = Objects.requireNonNull(editEnsurence.getText()).toString().trim();
        String sLicence = Objects.requireNonNull(editLicence.getText()).toString().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        LocalDateTime dBirth = LocalDate.parse(sBirth, formatter).atStartOfDay();
        LocalDateTime dInscription = LocalDate.parse(sInscription, formatter).atStartOfDay();
        LocalDateTime dEnsurence = LocalDate.parse(sEnsurence, formatter).atStartOfDay();
        LocalDateTime dLicence = LocalDate.parse(sLicence, formatter).atStartOfDay();
        client = new Client(0, fName, lName, dBirth, "pathPhoto", doc, numDoc,
                dInscription, email, tele, dEnsurence, dLicence, true);
        //Log.d(EditClientActivity.class.getSimpleName(), "onClickSave1: ");
        sendRequest(Request.Method.POST, encodeBitmapImage(), null, numDoc);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendRequest(int methode, String photo, Object id, String pwd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientId", id);
            jsonObject.put("sessionToken", null);
            jsonObject.put("fName", client.getfName());
            jsonObject.put("lName", client.getlName());
            jsonObject.put("birthDate", client.getBirthDate().format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("identityDoc", client.getIdentityDoc());
            jsonObject.put("identityNumber", client.getIdentityNumber());
            jsonObject.put("inscriptionDate", client.getInscriptionDate().format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("ensurenceValidity", client.getEnsurenceValidity().format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("licenceValidity", client.getLicenceValidity().format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("clientEmail", client.getClientEmail());
            jsonObject.put("passwd", pwd);
            jsonObject.put("clientPhone", client.getClientPhone());
            jsonObject.put("priceRate", 100);
            jsonObject.put("isActive", client.isActive());
            jsonObject.put("notes", "");
            jsonObject.put("photo", photo);
            //jsonObject.put("base64Image",encodeBitmapImage());
            //Log.d(EditClientActivity.class.getSimpleName(), "onClickSave: " + jsonObject);
            //Log.d(EditClientActivity.class.getSimpleName(), "photo: " + encodeBitmapImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(methode, ApiUrls.BASE + ApiUrls.CLIENTS_WS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(getApplicationContext(), ClientsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Client uploaded succefuly", Toast.LENGTH_LONG).show();
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
}