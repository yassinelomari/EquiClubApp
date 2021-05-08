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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.equiclubapp.Models.Client;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Clients/";
    private static final int PERMISSION_FILE = 23;
    private static final int ACCESS_FILE = 43;

    Client client;

    CircleImageView clientImg;
    ImageButton chooseImgBtn;
    Button save;

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

        docIdField = findViewById(R.id.addClientDoc);
        clientImg = findViewById(R.id.clientImg);
        save = findViewById(R.id.saveClient);
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

        List<String> items = new ArrayList<>(Arrays.asList("CINE","EPORT","SEJOUR",""));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.doc_list_item, items);
        docIdField.setAdapter(adapter);

        chooseImgBtn.setOnClickListener(this::onClickChooseImg);
        save.setOnClickListener(this::onClickSave);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACCESS_FILE && resultCode == Activity.RESULT_OK  && data != null){
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
                clientImg.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onClickChooseImg(View view) {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FILE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "choose photo"), ACCESS_FILE);
        }
    }

    private String encodeBitmapImage()
    {
        BitmapDrawable drawable = (BitmapDrawable) clientImg.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage=byteArrayOutputStream.toByteArray();
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
        client = new Client(11, fName, lName, dBirth, "pathPhoto", doc, numDoc,
                dInscription, email, tele, dEnsurence, dLicence);
        Log.d(EditClientActivity.class.getSimpleName(), "onClickSave1: ");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientId", null);
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
            jsonObject.put("passwd", client.getIdentityNumber());
            jsonObject.put("priceRate", 100);
            jsonObject.put("isActive", true);
            jsonObject.put("notes", "");
            jsonObject.put("photo", encodeBitmapImage());
            //jsonObject.put("base64Image",encodeBitmapImage());
            Log.d(EditClientActivity.class.getSimpleName(), "onClickSave: " + jsonObject);
            Log.d(EditClientActivity.class.getSimpleName(), "photo: " + encodeBitmapImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL_BASE + URL_WS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Toast.makeText(getApplicationContext(), "Client uploaded succefuly",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> map=new HashMap<String, String>();
                map.put("accept", "application/json");
                map.put("Content-Type", "application/json");
                return map;
            }
        };


        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }
}