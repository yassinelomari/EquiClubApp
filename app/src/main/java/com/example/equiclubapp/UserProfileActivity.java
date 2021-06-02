package com.example.equiclubapp;

import android.content.SharedPreferences;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.User;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    User user;
    int userId;

    TextView nameView, typeView;
    TextInputEditText emailView, phoneView, contratView;
    CircleImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_profile);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        userId = sharedPreferences.getInt("idUser", 0);

        VolleySingleton.handleSSLHandshake();

        nameView = findViewById(R.id.full_name);
        phoneView = findViewById(R.id.userTele);
        emailView = findViewById(R.id.userEmail);
        typeView = findViewById(R.id.userType);
        contratView = findViewById(R.id.userContrat);
        imgUser = findViewById(R.id.profile_image);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ApiUrls.BASE + ApiUrls.USERS_WS
                + userId, null, (resp) -> {
            //Log.d(ProfileActivity.class.getSimpleName(),resp.toString());
            try {
                //Log.d(ProfileActivity.class.getSimpleName(),"len" + resp.length());
                String fName = (resp.getString("userFname") == "null") ? null:
                        resp.getString("userFname");
                String lName = (resp.getString("userLname") == "null") ? null:
                        resp.getString("userLname");
                String email = (resp.getString("userEmail") == "null") ? null:
                        resp.getString("userEmail");
                String phone = (resp.getString("userPhone") == "null") ? null:
                        resp.getString("userPhone");
                String type = (resp.getString("userType") == "null") ? "":
                        resp.getString("userType");
                String color = (resp.getString("displayColor") == "null") ? null:
                        resp.getString("displayColor");
                String pathPhoto = resp.getString("userphoto");
                LocalDateTime contrat = LocalDateTime.parse(resp.getString("contractDate"),
                        DateTimeFormatter.ISO_DATE_TIME);
                user = new User(userId, email,fName, lName, type, pathPhoto, phone, contrat, color);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                nameView.setText(user.getUserFname() + " " + user.getUserLname());
                phoneView.setText(user.getUserPhone());
                emailView.setText(user.getUserEmail());
                typeView.setText(user.getUserType());
                contratView.setText(user.getContractDate().format(formatter));
                 VolleySingleton.getInstance(getApplicationContext()).getImageLoader().get(
                        ApiUrls.BASE + ApiUrls.PHOTO_CLIENTID_WS + userId,
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response,
                                                   boolean isImmediate) {
                                user.setPhoto(response.getBitmap());
                                imgUser.setImageBitmap(user.getPhoto());
                                //imgUser.setBorderColor(Color.alpha(Integer.parseInt(color)));
                            }
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(ProfileActivity.class.getSimpleName(),error.getMessage());
                            }
                        }
                );
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(UserProfileActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        }, (error) -> Log.e(ProfileActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}