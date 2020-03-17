package com.track.safezone.database;

import com.track.safezone.beans.User;

import java.util.Date;

public interface SafeZoneDatabase {


    void addUserDetails(User user);

    void updatePhoneNumber(User user);

    void updateUserLocation(User user);

    void startQuarantineActivity(Date date);
}
