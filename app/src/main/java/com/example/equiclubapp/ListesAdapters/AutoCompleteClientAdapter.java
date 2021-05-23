package com.example.equiclubapp.ListesAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.equiclubapp.Models.Client;
import com.example.equiclubapp.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteClientAdapter extends ArrayAdapter<Client> {

    private List<Client> clientListFull;


    public AutoCompleteClientAdapter(@NonNull Context context, @NonNull List<Client> clientList) {
        super(context, 0, clientList);
        clientListFull = new ArrayList<>(clientList);
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
                            client.getIdentityNumber().contains(filterPattern)) {
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.client_item_for_autocomplete, parent, false);
        }
        item = convertView;
        //ImageView icon = item.findViewById(R.id.clientImg);
        TextView cName = item.findViewById(R.id.clientName);
        TextView identity = item.findViewById(R.id.clientIdentity);
        Client clientItem = this.getItem(position);
        //String jpgPath = clientItem.getPathPhoto().replace("jpeg", "jpg");

        cName.setText(clientItem.getfName() + " " + clientItem.getlName());
        identity.setText(clientItem.getIdentityDoc() + " : " + clientItem.getIdentityNumber());
        return item;
    }

    /*@Override
    public int getCount() {
        return autoCompleteList.size();
    }*/

    @Override
    public Client getItem(int position) {
        return clientListFull.get(position);
    }

    @Override
    public Filter getFilter() {
        return clientFilter;
    }


}