package com.example.equiclubapp;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.User;
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

public class EditUserActivity extends AppCompatActivity {
    /*private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Users/";
    private static final String URL_PHOTO = "/Clients/photo/";
    private static final String URL_MODIF_PHOTO = "/Users/photo/";*/
    private static final int PERMISSION_FILE = 23;
    private static final int ACCESS_FILE = 43;
    private static final int RES_ADD_OK = 1;
    private static final int RES_MODIF_OK = 2;


    Map<String, String> types;
    User user;
    int action;
    //int userId;
    boolean imgModified;

    CircleImageView userImg;
    ImageButton chooseImgBtn;
    Button save, modify;

    TextInputEditText editFName, editLName, editEmail, editTele, editContarct;
    AutoCompleteTextView userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        VolleySingleton.handleSSLHandshake();
        Bundle extras = getIntent().getExtras();
        action = extras.getInt("requestCode");
        imgModified = false;
        if (action == 2)
            user = extras.getParcelable("user");

        save = findViewById(R.id.saveUser);
        modify = findViewById(R.id.modifyUser);
        editContarct = findViewById(R.id.addUserContract);
        editTele = findViewById(R.id.addUserPhone);
        editEmail = findViewById(R.id.addUserEmail);
        editFName = findViewById(R.id.addUserFName);
        editLName = findViewById(R.id.addUserLName);
        userType = findViewById(R.id.addUserType);
        chooseImgBtn = findViewById(R.id.btnAddImgUser);
        userImg = findViewById(R.id.userImgEdit);

        userImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        types = new HashMap<>();
        types.put("", "OTHER");
        types.put("Moniteur", "MONITOR");
        types.put("Admin", "ADMIN");
        types.put("Service", "SERVICE");
        types.put("Gardien", "GUARD");
        types.put("Comptable", "COMPTA");
        types.put("OTHER", "");
        types.put("MONITOR", "Moniteur");
        types.put("ADMIN", "Admin");
        types.put("SERVICE ", "Service");
        types.put("GUARD", "Gardien");
        types.put("COMPTA", "Comptable");

        List<String> items = new ArrayList<>(Arrays.asList("Moniteur", "Admin", "Service",
                "Comptable", "Gardien", ""));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.doc_list_item, items);
        userType.setAdapter(adapter);

        chooseImgBtn.setOnClickListener(this::onClickChooseImg);
        save.setOnClickListener(this::onClickSave);
        modify.setOnClickListener(this::onClickModify);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        if (action == 1)
            modify.setVisibility(View.INVISIBLE);
        else if (action == 2 && user != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
            editContarct.setText(user.getContractDate().format(formatter));
            editTele.setText(user.getUserPhone());
            editEmail.setText(user.getUserEmail());
            editFName.setText(user.getUserFname());
            editLName.setText(user.getUserLname());
            //Log.e(EditUserActivity.class.getSimpleName(), "userT"+ user.getUserType());
            userType.setText(types.get(user.getUserType()));
            VolleySingleton.getInstance(getApplicationContext()).getImageLoader().get(
                    ApiUrls.BASE + ApiUrls.PHOTO_WS + user.getUserphoto(),
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response,
                                               boolean isImmediate) {
                            if(!imgModified){
                                user.setPhoto(response.getBitmap());
                                userImg.setImageBitmap(user.getPhoto());
                                //Log.e(EditUserActivity.class.getSimpleName(), "photo from server");
                            }
                        }
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(EditUserActivity.class.getSimpleName(),error.getMessage());
                        }
                    }
            );
            save.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClickModify(View view) {
        if(imgModified) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", user.getUserId());
                jsonObject.put("imageBase64", encodeBitmapImage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, ApiUrls.BASE
                    + ApiUrls.USERS_MODIF_PHOTO_WS, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(getApplicationContext(), "Image uploaded succefuly",
                            Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(),
                            Toast.LENGTH_LONG).show();
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
        user.setUserEmail(Objects.requireNonNull(editEmail.getText()).toString().trim());
        user.setUserFname(Objects.requireNonNull(editFName.getText()).toString().trim());
        user.setUserLname(Objects.requireNonNull(editLName.getText()).toString().trim());
        user.setUserPhone(Objects.requireNonNull(editTele.getText()).toString().trim());
        user.setUserType(Objects.requireNonNull(types.get(userType.getText().toString())));
        String sContract = Objects.requireNonNull(editContarct.getText()).toString().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        LocalDateTime dContract = LocalDate.parse(sContract, formatter).atStartOfDay();
        user.setContractDate(dContract);
        sendRequest(Request.Method.PUT, user.getUserphoto(), user.getUserId(),user.getUserPasswd());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClickSave(View view) {


        /* ********************no test ************************ */
        String fName = Objects.requireNonNull(editFName.getText()).toString().trim();
        String lName = Objects.requireNonNull(editLName.getText()).toString().trim();
        String type = Objects.requireNonNull(userType.getText()).toString().trim();
        String email = Objects.requireNonNull(editEmail.getText()).toString().trim();
        String tele = Objects.requireNonNull(editTele.getText()).toString().trim();
        String sContract = Objects.requireNonNull(editContarct.getText()).toString().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        LocalDateTime dContract = LocalDate.parse(sContract, formatter).atStartOfDay();
        int adminLevel = (type == "Admin") ? 100:1;
        user = new User(0, email, adminLevel, LocalDateTime.now(), true, fName,
                lName, "", types.get(type), encodeBitmapImage(), dContract, tele,
                "#FFFFFF");

        sendRequest(Request.Method.POST, encodeBitmapImage(), null, user.getUserPhone());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendRequest(int method, String userphoto, Object userId, String pwd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("sessionToken", null);
            jsonObject.put("userEmail", user.getUserEmail());
            jsonObject.put("userPasswd", pwd);
            jsonObject.put("adminLevel", user.getAdminLevel());
            jsonObject.put("lastLoginTime", user.getLastLoginTime().format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("isActive", user.isUserActive());
            jsonObject.put("userFname", user.getUserFname());
            jsonObject.put("userLname", user.getUserLname());
            jsonObject.put("description", user.getDescription());
            jsonObject.put("userType", user.getUserType());
            jsonObject.put("userphoto", userphoto);
            jsonObject.put("contractDate", user.getContractDate().format(DateTimeFormatter.ISO_DATE_TIME));
            jsonObject.put("userPhone", user.getUserPhone());
            jsonObject.put("displayColor", user.getDisplayColor());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(method, ApiUrls.BASE + ApiUrls.USERS_WS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "user uploaded succefuly", Toast.LENGTH_LONG).show();
                if(action == 1) {
                    try {
                        user.setUserphoto(response.getString("photo"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                    intent.putExtra("newUser", user);
                    setResult(RES_ADD_OK, intent);
                    finish();
                } else if(action == 2){
                    Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                    startActivity(intent);
                }



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
        BitmapDrawable drawable = (BitmapDrawable) userImg.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }

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
                    userImg.setImageBitmap(bitmap);
                    //Log.e(EditUserActivity.class.getSimpleName(), "photo from galerie");
                    imgModified = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}