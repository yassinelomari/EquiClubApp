package com.example.equiclubapp.ListesAdapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.equiclubapp.ClientsActivity;
import com.example.equiclubapp.Models.Client;
import com.example.equiclubapp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClientAdapter extends ArrayAdapter<Client> {

    private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_PHOTO = "/Clients/photo/";

    private List<Client> clientListFull;

    public ClientAdapter(@NonNull Context context, @NonNull List<Client> clientList) {
        super(context, 0, clientList);
        clientListFull = new ArrayList<>(clientList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item;
        LayoutInflater li = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        item = li.inflate(R.layout.client_item, parent, false);
        //ImageView icon = item.findViewById(R.id.clientImg);
        TextView cName = item.findViewById(R.id.clientName);
        TextView identity = item.findViewById(R.id.clientIdentity);
        Client clientItem = this.getItem(position);
        //String jpgPath = clientItem.getPathPhoto().replace("jpeg", "jpg");
        VolleySingleton.getInstance(item.getContext()).getImageLoader().get(
                URL_BASE + URL_PHOTO + clientItem.getPathPhoto(),
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response,
                                           boolean isImmediate) {
                        clientItem.setPhoto(response.getBitmap());
                        CircleImageView img = item.findViewById(R.id.clientImg);
                        img.setImageBitmap(clientItem.getPhoto());
                        if(clientItem.isActive())
                            img.setBorderColor(Color.GREEN);
                        else
                            img.setBorderColor(Color.RED);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(ClientsActivity.class.getSimpleName(),error.getMessage());
                    }
                }
        );
        cName.setText(clientItem.getfName() + " " + clientItem.getlName());
        identity.setText(clientItem.getIdentityDoc() + " : " + clientItem.getIdentityNumber());
        return item;
    }

    private Filter clientFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Client> suggests = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggests.addAll(clientListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Client client : clientListFull){
                    if (client.getfName().toLowerCase().contains(filterPattern) ||
                            client.getlName().toLowerCase().contains(filterPattern) ||
                            client.getIdentityNumber().toLowerCase().contains(filterPattern)) {
                        suggests.add(client);
                    }
                }
            }
            results.values = suggests;
            results.count = suggests.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Client) resultValue).getfName();
        }
    };

    @Override
    public Filter getFilter() {
        return clientFilter;
    }
}
