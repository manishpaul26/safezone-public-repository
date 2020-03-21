package com.track.safezone.beans;

import android.util.Log;

import com.google.android.libraries.places.api.model.Place;
import com.track.safezone.activity.Constants;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

public class User implements Serializable {

    private static final String TAG = "SafeZone User";

    private String firstName;

    private String secondName;

    private String phoneNumber;

    private String emailAddress;

    private boolean isUnderIsolation;

    private Date isolationStartTime;

    private Date isolationEndTime;

    private ArrayList<DependentDetails> dependentDetails;


    private Place gpsLocation;
    private String userId;

    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        this.secondName = builder.secondName;
        this.phoneNumber = builder.phoneNumber;
        this.emailAddress = builder.emailAddress;
        this.dependentDetails = builder.dependentDetails;
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

    public void setIsolation() {

        DateFormat dateFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        Date date = new Date();
        try {
            dateFormat.parse(dateFormat.format(date));
            Calendar isolationEndTime = Calendar.getInstance();
            isolationEndTime.add(Calendar.DAY_OF_MONTH, 14);

            this.isUnderIsolation = true;
            this.isolationStartTime = dateFormat.parse(dateFormat.format(date));
            this.isolationEndTime = isolationEndTime.getTime();

        } catch (ParseException e) {
            Log.e(TAG, "setIsolation: ", e);
        }

    }


    public ArrayList<DependentDetails> getDependentDetails() {
        return dependentDetails;
    }

    public static class UserBuilder
    {
        private final String firstName;
        private final String secondName;
        private String phoneNumber;
        private String emailAddress;
        private ArrayList<DependentDetails> dependentDetails;

        public UserBuilder(String firstName, String secondName) {
            this.firstName = firstName;
            this.secondName = secondName;
        }

        public UserBuilder phone(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        public UserBuilder address(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public UserBuilder dependentDetails(ArrayList<DependentDetails> dependentDetails){
            this.dependentDetails=dependentDetails;
            return this;
        }
        //Return the finally consrcuted User object
        public User build() {
            User user =  new User(this);
            return user;
        }
    }
}

