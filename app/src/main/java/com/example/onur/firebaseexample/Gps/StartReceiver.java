package com.example.onur.firebaseexample.Gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by onur on 23.3.2017 at 17:13.
 */

public class StartReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, GPSTracker.class);
        context.startService(myIntent);
        GPSTracker gpsTracker = new GPSTracker(context);

        Log.e("Gps",String.valueOf(gpsTracker.getLocation()));
    }

    public void StartReceiver(){


    }
}
