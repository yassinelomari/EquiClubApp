package com.example.equiclubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.equiclubapp.Models.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditClientActivity extends AppCompatActivity {

    Client client;

    AutoCompleteTextView docIdField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        docIdField = findViewById(R.id.addClientDoc);

        List<String> items = new ArrayList<>(Arrays.asList("CINE","EPORT","SEJOUR",""));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.doc_list_item, items);
        docIdField.setAdapter(adapter);
    }
}