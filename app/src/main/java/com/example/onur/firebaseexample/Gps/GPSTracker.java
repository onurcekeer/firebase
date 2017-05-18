package com.example.onur.firebaseexample.Gps;

/**
 * Created by onur on 17.3.2017 at 09:43.
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.onur.firebaseexample.Activity.SimpleTabsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GPSTracker extends Service implements LocationListener {

    String userID;

    private Context mContext;
    String TAG = "Gps";
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    FirebaseAuth auth;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15;

    protected LocationManager locationManager;


    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public GPSTracker(){
    }


    @Override
    public void onCreate() {


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mContext= this;
        super.onCreate();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            userID = user.getUid();
        }


        getLocation();
        onLocationChanged(getLocation());
        Log.e("Servis","Servis Başlatıldı");
    }

/*
    public void onDestroy(){

        //Log.e("Servis","Servis yeniden başlatıldı.");
  //      Intent broadcastIntent = new Intent("RestartService");
    //    sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mContext= this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                getLocation();
                onLocationChanged(getLocation());
                Log.e("Servis","Thread");
            }
        }).start();

        Log.e("Servis","OnStartCommand");


        return Service.START_STICKY;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Log.e("Gps",String.valueOf(locationManager));

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.e("Gps", "Gps and network not enabled");
            }
            else
                {
                if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    this.canGetLocation = true;

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Gps", "Network");
                    Log.e(TAG,String.valueOf(getLatitude()));
                    Log.e(TAG,String.valueOf(getLongitude()));
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        Log.e("Network last location",String.valueOf(getLatitude()));
                        Log.e("Network last location",String.valueOf(getLongitude()));
                    }

                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");

                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                                Log.e("Gps last location",String.valueOf(getLatitude()));
                                Log.e("Gps last location",String.valueOf(getLongitude()));
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG,String.valueOf(getLatitude()));
        Log.e(TAG,String.valueOf(getLongitude()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationPost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String LocationPost() throws IOException {

        OkHttpClient client = new OkHttpClient();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user!=null){
            userID = user.getUid();
            String url = "http://expo2017.vm.smartict.com.tr/api/addLoc?lat="+String.valueOf(latitude)+"&lon="+String.valueOf(longitude)+"&user_id="+userID;
            Log.e("Request",url);


            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        else {
            return "";
        }
    }
}

