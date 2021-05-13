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
import com.example.equiclubapp.Models.User;
import com.example.equiclubapp.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteMonitorAdapter extends ArrayAdapter<User> {
    private List<User> monitorListFull;

    public AutoCompleteMonitorAdapter(@NonNull Context context, @NonNull List<User> monitorList) {
        super(context, 0, monitorList);
        monitorListFull = new ArrayList<>(monitorList);
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<User> suggests = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggests.addAll(monitorListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(User user : monitorListFull){
                    if (user.getUserFname().toLowerCase().contains(filterPattern) ||
                            user.getUserLname().toLowerCase().contains(filterPattern)) {
                        suggests.add(user);
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
            return ((User) resultValue).getUserFname();
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
        User userItem = this.getItem(position);
        //String jpgPath = clientItem.getPathPhoto().replace("jpeg", "jpg");

        cName.setText(userItem.getUserFname() + " " + userItem.getUserLname());
        identity.setText(userItem.getUserEmail());
        return item;
    }

    @Override
    public User getItem(int position) {
        return monitorListFull.get(position);
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }
}
