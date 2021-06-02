package com.example.equiclubapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.equiclubapp.ListesAdapters.ApiUrls;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editLogin, editPwd;
    CheckBox keepCnx;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        VolleySingleton.handleSSLHandshake();

        editLogin = findViewById(R.id.loginEdit);
        editPwd = findViewById(R.id.pwdEdit);
        keepCnx = findViewById(R.id.keepConnection);
        btnLogin = findViewById(R.id.btnConnection);

        btnLogin.setOnClickListener(this::OnClickConnect);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Boolean autoLogin = sharedPreferences.getBoolean("autoLogin", false);
        if(autoLogin){
            String login = sharedPreferences.getString("login", "");
            String pwd = sharedPreferences.getString("pwd", "");
            authentification(login, pwd);
        }
    }

    private void OnClickConnect(View view) {
        String login = Objects.requireNonNull(editLogin.getText()).toString().trim();
        String pwd = Objects.requireNonNull(editPwd.getText()).toString().trim();
        authentification(login, pwd);
    }

    private void authentification(String login, String pwd){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                ApiUrls.BASE + ApiUrls.LOGIN_WS + login + "/" + pwd,
                null, (resp) -> {
            try {
                String status = resp.getString("status");
                if(status.equals("SUCCESS")){
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String role = resp.getString("role");
                    editor.putInt("idUser", resp.getInt("id"));
                    editor.putString("role", role);
                    Boolean autoLogin = keepCnx.isChecked();
                    editor.putBoolean("autoLogin", autoLogin);
                    if(autoLogin){
                        editor.putString("login", login);
                        editor.putString("pwd", pwd);
                    }
                    editor.apply();
                    //Log.e(EditClientActivity.class.getSimpleName(), "role" + role);
                    if (role.equals("CLIENT")) {
                        Intent intent = new Intent(LoginActivity.this, PersonnelActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } else if (role.equals("ADMIN")){
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }else {
                        Intent intent = new Intent(LoginActivity.this, MonitorActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                } else if (status == "FAIL") {
                    Toast.makeText(LoginActivity.this,
                            "Login ou mot de passe incorrecte", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this , "error",
                        Toast.LENGTH_LONG).show();
            }
        }, (error) -> Log.e(EditSeanceActivity.class.getSimpleName(),error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}