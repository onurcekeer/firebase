package com.example.onur.firebaseexample.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.onur.firebaseexample.Activity.SimpleTabsActivity;
import com.example.onur.firebaseexample.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by onur on 15.3.2017 at 13:39.
 */

public class FCMCallbackService extends FirebaseMessagingService {
    private static final String TAG = "FCMCallbackService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From:" + remoteMessage.getFrom());
        Log.d(TAG, "Message Body:" + remoteMessage.getNotification().getBody());
        sendNotification(remoteMessage.getNotification());
    }

    private void sendNotification(RemoteMessage.Notification notification) {
        int color = getResources().getColor(R.color.notification_color);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, SimpleTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.messenger_bubble_small_white)
                .setColor(color)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getBody()))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
