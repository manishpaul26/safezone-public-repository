package com.track.safezone.beans;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

public class User implements Serializable {

    private String firstName;

    private String secondName;

    private String phoneNumber;

    private String emailAddress;

    private Place gpsLocation;
    private String userId;

    public User(String firstName, String secondName, String phoneNumber, String emailAddress, String userId) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Place getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(Place userPlace) {
        this.gpsLocation = userPlace;
    }

    public String getUserId() {
        return this.userId;
    }
}
