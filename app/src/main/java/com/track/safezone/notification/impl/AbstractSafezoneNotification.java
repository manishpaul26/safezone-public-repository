package com.track.safezone.notification.impl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.track.safezone.R;
import com.track.safezone.activity.ConfirmObservationStatusActivity;
import com.track.safezone.notification.Channel;
import com.track.safezone.notification.SafeZoneNotification;

public abstract class AbstractSafezoneNotification implements SafeZoneNotification {

    private static final String TAG = "AbstractSafezoneNotific";

    private NotificationCompat.Builder notificationBuilder = null;

    private Channel channel;

    public AbstractSafezoneNotification(Channel channel) {
        this.channel = channel;
    }


    protected Notification createNotification(Context context, NotificationManager notificationManager, Class<?> confirmObservationStatusActivityClass) {

        Intent contentIntent = new Intent(context.getApplicationContext(), ConfirmObservationStatusActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, contentIntent, 0);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = createNotificationForNewVersions(notificationManager, context, pendingIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(context,
                    channel.getChannelId()) // don't forget create a notification channel first
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification_running)
                    .setContentTitle("SafeZone")
                    .setContentIntent(pendingIntent)
                    .setContentText("Service is running background");


        }
        return notificationBuilder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationCompat.Builder createNotificationForNewVersions(NotificationManager notificationManager, Context context, PendingIntent pendingIntent) {

        Log.d(TAG, "createNotificationForNewVersions: Building notification");

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = this.channel.getChannel();

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(channel);

        return new NotificationCompat.Builder(context,
                channel.getId()) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification_running)
                .setContentTitle("SafeZone")
                .setContentText(channel.getDescription())
                .setContentIntent(pendingIntent)
                .setChannelId(channel.getId());

    }
}
