package com.example.equiclubapp.ListesAdapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.equiclubapp.ClientsActivity;
import com.example.equiclubapp.Models.User;
import com.example.equiclubapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends ArrayAdapter<User> {

    /*private static final String URL_BASE = "https://192.168.100.100:44352/api";
    private static final String URL_PHOTO = "/Clients/photo/";*/

    private List<User> usersListFull;
    Map<String, String> types;

    public UserAdapter(@NonNull Context context, @NonNull List<User> usersList) {
        super(context, 0, usersList);
        usersListFull = new ArrayList<>(usersList);
        types = new HashMap<>();
        types.put("OTHER", "");
        types.put("MONITOR", "Moniteur");
        types.put("ADMIN", "Admin");
        types.put("SERVICE ", "Service");
        types.put("GUARD", "Gardien");
        types.put("COMPTA", "Comptable");
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
        User userItem = this.getItem(position);
        //String jpgPath = clientItem.getPathPhoto().replace("jpeg", "jpg");
        if(userItem.getUserphoto() != null && userItem.getUserphoto() != ""){
            VolleySingleton.getInstance(item.getContext()).getImageLoader().get(
                    ApiUrls.BASE + ApiUrls.PHOTO_WS + userItem.getUserphoto(),
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response,
                                               boolean isImmediate) {
                            userItem.setPhoto(response.getBitmap());
                            CircleImageView img = item.findViewById(R.id.clientImg);
                            img.setImageBitmap(userItem.getPhoto());
                            if(userItem.isUserActive())
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
        }
        cName.setText(userItem.getUserFname() + " " + userItem.getUserLname());
        identity.setText(types.get(userItem.getUserType()));
        return item;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<User> suggests = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggests.addAll(usersListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(User user : usersListFull){
                    if (user.getUserFname().toLowerCase().contains(filterPattern) ||
                            user.getUserLname().toLowerCase().contains(filterPattern) ||
                            user.getUserType().toLowerCase().contains(filterPattern)) {
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
            return ((User) resultValue).getUserLname();
        }
    };

    @Override
    public Filter getFilter() {
        return userFilter;
    }
}
