package com.example.onur.firebaseexample.Notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by onur on 15.3.2017 at 13:37.
 */

public class FCMInitializationService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMInitService";

    @Override
    public void onTokenRefresh() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "FCM Device Token:" + fcmToken);
        //Save or send FCM registration token
    }
}