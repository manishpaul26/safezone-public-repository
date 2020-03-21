package com.track.safezone.database;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.track.safezone.beans.User;

import java.util.Date;

public interface SafeZoneDatabase {


    void addUserDetails(User user);

    void updatePhoneNumber(User user);

    void updateUserLocationAndIsolationTime(User user);

    void startQuarantineActivity(Date date);

    boolean getQuarantineActivityStatus();

    LatLng getLocationData(Context applicationContext);

    void updateLastObservationTime();
}
