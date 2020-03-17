package com.track.safezone;

import android.app.Application;
import android.content.Intent;

import com.track.safezone.services.BackgroundLocationTrackingService;

public class SafeZoneApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, BackgroundLocationTrackingService.class));
    }
}
