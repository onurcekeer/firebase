package com.example.onur.firebaseexample.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.onur.firebaseexample.CustomAdapter;
import com.example.onur.firebaseexample.R;
import com.example.onur.firebaseexample.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TwoFragment extends Fragment {


    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    public CustomAdapter customAdapter;
    public static ArrayList<User> users = new ArrayList<>();
    public String TAG = "Database";
    public User user_;
    SwipeMenuListView listView;
    String TAG2 = "Upload";
    LayoutInflater layoutInflater;

    Bitmap bitmap = null;
    String username_ = null;
    String status_ = null;
    String url_ = "0";
    FirebaseDatabase database;
    DatabaseReference myRef;
    String uniqueID;
    EditText addUsername;
    EditText addUserStatus;
    CircleImageView addUserImage;
    FloatingActionButton fab_AddImage;
    ViewGroup mViewGroup;
    ConnectivityManager cm;
    SwipeMenuCreator creator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mViewGroup = container;
        this.layoutInflater = inflater;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");


        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                //create an action that will be showed on swiping an item in the list
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.DKGRAY));
                // set width of an option (px)
                item1.setWidth(200);
                item1.setTitle("Action 1");
                item1.setTitleSize(18);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);

                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                item2.setBackground(new ColorDrawable(Color.RED));
                item2.setWidth(200);
                item2.setTitle("Action 2");
                item2.setTitleSize(18);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
        };







        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Log.e(TAG, "======="+postSnapshot.child("username").getValue());
                    Log.e(TAG, "======="+postSnapshot.child("status").getValue());
                    Log.e(TAG, "======="+postSnapshot.child("profil").getValue());
                    users.add(new User(postSnapshot.child("username").getValue().toString(),postSnapshot.child("status").getValue().toString(),postSnapshot.child("profil").getValue().toString()));
                }

                customAdapter = new CustomAdapter(getActivity(),users);
                listView.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.wtf(TAG, "Failed to read value.", error.toException());
            }
        });



        View view = inflater.inflate(R.layout.fragment_two, container, false);
        perform(view);


        return view;

    }

    public void perform(View v) {

        FloatingActionButton addUser = (FloatingActionButton)v.findViewById(R.id.add);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        listView = (SwipeMenuListView) v.findViewById(R.id.listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setRefreshing(true);
        listView.setMenuCreator(creator);


        listView.setMenuCreator(creator);
        // set SwipeListener
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Action 1 for " , Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Action 2 for ", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }});

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int GET_FROM_GALLERY = 54;
        Log.e(TAG2,"onActivityForResult");


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = decodeUri(selectedImage);
                addUserImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(layoutInflater.getContext().getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 200;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(layoutInflater.getContext().getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        final byte[] data = baos.toByteArray();

        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://fir-example-81c97.appspot.com/");

                final StorageReference imagesRef = storageRef.child(uniqueID + ".jpg");
                UploadTask uploadTask = imagesRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG2, "failed");
                        Snackbar.make(getView(), "Internet bağlantınızı kontrol edin", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Tamam", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .setActionTextColor(Color.RED)
                                .show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e(TAG2, "success");
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        url_ = downloadUrl.toString();
                        uploadUserData(username_, status_, url_);
                    }
                });
            }
        }).start();

    }

    void showDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
        builder.setView(R.layout.dialog);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok,null);
        builder.setNegativeButton(android.R.string.cancel,null);
        final AlertDialog dialog = builder.create();
        uniqueID = UUID.randomUUID().toString();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button_positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!addUsername.getText().toString().isEmpty() && !addUserStatus.getText().toString().isEmpty()){
                            username_ = addUsername.getText().toString();
                            status_ = addUserStatus.getText().toString();

                            if (bitmap == null) {
                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_user);
                            }

                            if(isOnline()){
                                uploadImage(bitmap);
                            }
                            else {
                                Snackbar.make(getView(), "Internet bağlantınızı kontrol edin", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Tamam", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        })
                                        .setActionTextColor(Color.RED)
                                        .show();
                            }

                            bitmap = null;
                            dialog.dismiss();
                        }
                        else{
                            if(addUsername.getText().toString().isEmpty()) {
                                addUsername.setError("Username is required");
                            }
                            else {
                                addUserStatus.setError("Status is required");
                            }
                        }
                    }
                });
            }
        });


        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        dialog.show();


        addUsername = (EditText)dialog.findViewById(R.id.et_username);
        addUserStatus = (EditText)dialog.findViewById(R.id.et_status);
        addUserImage = (CircleImageView)dialog.findViewById(R.id.iv_add_profil);
        fab_AddImage = (FloatingActionButton)dialog.findViewById(R.id.fab);


        fab_AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(layoutInflater.getContext().getPackageManager()) != null)
                startActivityForResult(intent,54);
            }
        });


    }

    void uploadUserData(String Username,String Status,String Url){
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        Log.e(TAG2,Username+" "+Status+" "+Url);
        user_ = new User(Username,Status,Url);
        reference.child(uniqueID).setValue(user_);

    }

    public boolean isOnline() {
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}