package com.track.safezone.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.track.safezone.activity.ConfirmObservationStatusActivity;
import com.track.safezone.notification.Channel;
import com.track.safezone.notification.SafeZoneNotification;
import com.track.safezone.notification.impl.UrgentSafezoneNotification;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive: INSIIIIDEDE ALAAAAAARM");



        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        Channel channel = new Channel("urgent", "Upload Status", "Please upload your location and photo", NotificationManager.IMPORTANCE_HIGH);


       // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define

        SafeZoneNotification urgentSafeZoneNotification = new UrgentSafezoneNotification(channel, context.getApplicationContext(), notificationManager, ConfirmObservationStatusActivity.class);

        notificationManager.notify((int) Math.random() /1000, urgentSafeZoneNotification.getNotification());




    }


}
