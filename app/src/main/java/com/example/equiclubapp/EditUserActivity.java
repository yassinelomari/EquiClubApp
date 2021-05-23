package com.example.equiclubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;
import com.example.equiclubapp.Models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserActivity extends AppCompatActivity {
    private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Users/";
    private static final String URL_PHOTO = "/Clients/photo/";
    private static final int PERMISSION_FILE = 23;
    private static final int ACCESS_FILE = 43;
    private static final int RES_ADD_OK = 1;


    Map<String, String> types;
    User user;
    int action;
    int userId;
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
            userId = extras.getInt("userId");

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

        types = new HashMap<>();
        types.put("Moniteur", "MONITOR");
        types.put("Admin", "ADMIN");
        types.put("SuperAdmin", "ADMIN");
        types.put("Palefrenier", "GROOM ");
        types.put("Gardien", "GUARDIAN ");
        types.put("Ménage", "HOUSEWORK");
        types.put("", "");
        List<String> items = new ArrayList<>(Arrays.asList("Moniteur", "Admin", "SuperAdmin",
                "Palefrenier", "Gardien", "Ménage", ""));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.doc_list_item, items);
        userType.setAdapter(adapter);

        chooseImgBtn.setOnClickListener(this::onClickChooseImg);
        save.setOnClickListener(this::onClickSave);
        modify.setOnClickListener(this::onClickModify);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (action == 1)
            modify.setVisibility(View.INVISIBLE);
        else if (action == 2) {
            save.setVisibility(View.INVISIBLE);
        }
    }

    private void onClickModify(View view) {
    }

    private void onClickSave(View view) {
        Intent intent = new Intent(getApplicationContext(), UsersActivity.class);

        setResult(RES_ADD_OK, intent);
        finish();
    }

    private void onClickChooseImg(View view) {
    }
}