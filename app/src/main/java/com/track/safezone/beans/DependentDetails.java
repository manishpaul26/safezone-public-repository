package com.track.safezone.beans;

import java.io.Serializable;

public class DependentDetails implements Serializable {

    private static final String TAG = "Dependent User Details";

    private String firstName;

    private String phoneNumber;

    public DependentDetails(String firstName, String phoneNumber) {
        this.firstName = firstName;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
