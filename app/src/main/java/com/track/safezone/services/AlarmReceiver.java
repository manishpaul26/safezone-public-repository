package com.track.safezone.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.track.safezone.activity.ConfirmObservationStatusActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive: INSIIIIDEDE ALAAAAAARM");
        Intent contentIntent = new Intent(context, ConfirmObservationStatusActivity.class);
        context.startActivity(contentIntent);

    }


}
