package com.example.equiclubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.equiclubapp.ListesAdapters.ClientAdapter;
import com.example.equiclubapp.ListesAdapters.VolleySingleton;
import com.example.equiclubapp.Models.Client;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ClientsActivity extends AppCompatActivity {
    private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_WS = "/Clients";

    List<Client> clients;

    ImageButton btnAdd;
    ListView clientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        VolleySingleton.handleSSLHandshake();

        clients = new ArrayList<Client>();

        btnAdd = findViewById(R.id.btnAddClient);
        clientsList = findViewById(R.id.clientList);

        btnAdd.setOnClickListener(this::onClickAdd);

        clientsList.setOnItemClickListener(this::onClientItemClick);
    }

    @Override
    protected void onResume() {
        super.onResume();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_BASE + URL_WS,
                null, (resp) -> {
            //Log.d(ClientsActivity.class.getSimpleName(),resp.toString());
            try {
                Log.d(ClientsActivity.class.getSimpleName(),"len" + resp.length());
                if(clients.isEmpty()) {
                    for (int i = 0; i < resp.length(); i++) {
                        int id = resp.getJSONObject(i).getInt("clientId");
                        String fName = resp.getJSONObject(i).getString("fName");
                        String lName = resp.getJSONObject(i).getString("lName");
                        String email = resp.getJSONObject(i).getString("clientEmail");
                        String phone = resp.getJSONObject(i).getString("clientPhone");
                        String idDoc = resp.getJSONObject(i).getString("identityDoc");
                        String idNum = resp.getJSONObject(i).getString("identityNumber");
                        String pathPhoto = resp.getJSONObject(i).getString("photo");
                        clients.add(new Client(id, fName, lName, email, phone, idDoc, idNum, pathPhoto));
                    }
                }
                if(!clients.isEmpty())
                    clientsList.setAdapter(new ClientAdapter(ClientsActivity.this, clients));

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ClientsActivity.this , "error", Toast.LENGTH_LONG)
                        .show();
            }
        },
                (error) -> Log.e(ClientsActivity.class.getSimpleName(),error.getMessage())

        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void onClickAdd(View view) {
        Intent intent = new Intent(this, EditClientActivity.class);
        startActivity(intent);
    }

    private void onClientItemClick(AdapterView<?> parent, View view, int position, long id) {

        Client client = (Client) clientsList.getItemAtPosition(position);
        Intent intent = new Intent(this, ClientDetailActivity.class);
        intent.putExtra("clientId", client.getClientId());
        startActivityForResult(intent, 100);
    }
}