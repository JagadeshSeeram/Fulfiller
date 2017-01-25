package com.biglynx.fulfiller.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.biglynx.fulfiller.MainActivity;
import com.biglynx.fulfiller.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by snehitha.chinnapally on 1/25/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "FireBaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification());
        }
    }

    private void showNotification(RemoteMessage.Notification notificationMsg) {
        if (notificationMsg != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 12, intent, PendingIntent.FLAG_ONE_SHOT);

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.appicon)
                    .setContentTitle(notificationMsg.getTitle())
                    .setContentText(notificationMsg.getBody())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(12, notification);

        }
    }
}
