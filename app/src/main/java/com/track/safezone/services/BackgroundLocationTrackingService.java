package com.track.safezone.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.track.safezone.R;
import com.track.safezone.activity.MainActivity;

public class BackgroundLocationTrackingService extends Service {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    private static final String TAG = "BackgroundLocationTrack";
    private static final String CHANNEL_ID = "Activity and location tracking";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        // do your jobs here

        Log.e(TAG, "onStartCommand: WOOOOOOO");

        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        NotificationCompat.Builder notificationBuilder = null;

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = createNotificationForNewVersions();
        } else {
            notificationBuilder = new NotificationCompat.Builder(this,
                    NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification_running)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Service is running background");

        }

        Notification notification = notificationBuilder.setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIF_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationCompat.Builder createNotificationForNewVersions() {

        CharSequence name = getString(R.string.channel_name_sticky_tracking);
        String description = getString(R.string.channel_description_sticky_tracking);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        return new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification_running)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setChannelId(channel.getId());

    }
}
