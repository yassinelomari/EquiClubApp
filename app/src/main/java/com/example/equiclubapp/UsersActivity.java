package com.example.equiclubapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.equiclubapp.ListesAdapters.ClientAdapter;
import com.example.equiclubapp.ListesAdapters.UserAdapter;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;
import com.example.equiclubapp.Models.User;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity  {
    private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Users";
    private static final int RES_ADD = 101;
    private static final int RES_ADD_OK = 1;
    private static final int RES_MODIF_OK = 2;


    List<User> users;

    Button btnAdd;
    ListView usersList;
    TextInputEditText editSearch;

    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        VolleySingleton.handleSSLHandshake();

        users = new ArrayList<User>();

        btnAdd = findViewById(R.id.btnAddUser);
        usersList = findViewById(R.id.userList);
        editSearch = findViewById(R.id.editSearchUser);

        btnAdd.setOnClickListener(this::onClickAdd);

        usersList.setOnItemClickListener(this::onClientItemClick);

        editSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userAdapter != null)
                    userAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_BASE + URL_WS,
                null, (resp) -> {
            //Log.d(ClientsActivity.class.getSimpleName(),resp.toString());
            try {
                //Log.d(ClientsActivity.class.getSimpleName(),"len" + resp.length());
                if(users.isEmpty()) {
                    for (int i = 0; i < resp.length(); i++) {
                        int id = resp.getJSONObject(i).getInt("userId");
                        int adminLevel = resp.getJSONObject(i).getInt("adminLevel");
                        String fName = resp.getJSONObject(i).getString("userFname");
                        String lName = resp.getJSONObject(i).getString("userLname");
                        String email = resp.getJSONObject(i).getString("userEmail");
                        String phone = resp.getJSONObject(i).getString("userPhone");
                        String type = resp.getJSONObject(i).getString("userType");
                        String desc = resp.getJSONObject(i).getString("description");
                        String pathPhoto = resp.getJSONObject(i).getString("userphoto");
                        String displayColor = resp.getJSONObject(i).getString("displayColor");
                        LocalDateTime lastLoginTime = LocalDateTime.parse(
                                resp.getJSONObject(i).getString("lastLoginTime"),
                                DateTimeFormatter.ISO_DATE_TIME);
                        LocalDateTime contractDate = LocalDateTime.parse(
                                resp.getJSONObject(i).getString("contractDate"),
                                DateTimeFormatter.ISO_DATE_TIME);
                        boolean isActive = resp.getJSONObject(i).getBoolean("isActive");
                        users.add(new User(id, email, adminLevel, lastLoginTime, isActive, fName,
                                lName, desc, type, pathPhoto, contractDate, phone, displayColor));
                    }
                }
                if(!users.isEmpty()){
                    userAdapter = new UserAdapter(UsersActivity.this, users);
                    usersList.setAdapter(userAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(UsersActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        },
                (error) -> Log.e(ClientsActivity.class.getSimpleName(),error.getMessage())

        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void onClientItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        User user = (User) usersList.getItemAtPosition(position);
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void onClickAdd(View view) {
        Intent intent = new Intent(this, EditUserActivity.class);
        intent.putExtra("requestCode", 1);
        startActivityForResult(intent, RES_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RES_ADD && resultCode == RES_ADD_OK){
            User newUser = data.getExtras().getParcelable("newUser");
            ((UserAdapter)usersList.getAdapter()).add(newUser);
        } else if(resultCode == RES_MODIF_OK) {
            Log.e(UsersActivity.class.getSimpleName(), "bien modifiér");
        }
    }
}