package com.track.safezone.beans;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

public class User implements Serializable {

    private String firstName;

    private String secondName;

    private String phoneNumber;

    private String emailAddress;

    private Place gpsLocation;

    public User(String firstName, String secondName, String phoneNumber, String emailAddress) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
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

    public void setGpsLocation(Place userPlace) {
        this.gpsLocation = userPlace;
    }
}
