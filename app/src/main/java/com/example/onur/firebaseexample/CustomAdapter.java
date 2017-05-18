package com.example.onur.firebaseexample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.type;


public class CustomAdapter extends BaseAdapter {

    private List<User> mUser;
    private LayoutInflater layoutInflater;
    private ConnectivityManager cm;

    public CustomAdapter(Activity activity, List<User> users){

        cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUser = users;

    }
    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return type;
    }

    @Override
    public int getCount() {
        return mUser.size();
    }

    @Override
    public Object getItem(int position) {
        return mUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View satirView = layoutInflater.inflate(R.layout.satir_layout,null);
        User users = mUser.get(position);
        CircleImageView profilImage = (CircleImageView) satirView.findViewById(R.id.iv_profil);
        profilImage.setImageResource(R.drawable.default_user);

        if(isOnline()) {

            Picasso.with(layoutInflater.getContext())
                    .load(users.getProfil())
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(R.drawable.default_user)
                    .error(R.drawable.default_user)
                    .into(profilImage);
        }

        TextView text_username = (TextView)satirView.findViewById(R.id.tv_username);
        TextView text_status = (TextView)satirView.findViewById(R.id.tv_status);

        text_username.setText(users.getUsername());
        text_status.setText(users.getStatus());

        return satirView;
    }

    private boolean isOnline() {
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}