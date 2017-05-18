package com.example.onur.firebaseexample.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onur.firebaseexample.CustomAdapter;
import com.example.onur.firebaseexample.R;
import com.example.onur.firebaseexample.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class OneFragment extends Fragment {

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
            recyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            return recyclerView;

        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView picture;

            public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.fragment_one, parent, false));
                picture = (ImageView) itemView.findViewById(R.id.tile_picture);

            }
        }

        /**
         * Adapter to display recycler view.
         */

        public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

            ArrayList<String> imageArray = null;
            FirebaseDatabase database;
            DatabaseReference myRef;
            Context mContext;
            int LENGHT;


            public ContentAdapter(Context context) {

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("Users");
                mContext = context;
                imageArray = new ArrayList<>();

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        imageArray.clear();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            imageArray.add(postSnapshot.child("profil").getValue().toString());
                        }

                        LENGHT = imageArray.size();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.wtf("Firebase", "Failed to read value.", error.toException());
                    }
                });

            }


            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {

                Picasso.with(mContext)
                        .load(imageArray.get(position))
                        .resize(100, 100)
                        .centerCrop()
                        .placeholder(R.drawable.default_user)
                        .error(R.drawable.default_user)
                        .into(holder.picture);
            }

            @Override
            public int getItemCount() {
                return LENGHT;
            }
        }
    }
