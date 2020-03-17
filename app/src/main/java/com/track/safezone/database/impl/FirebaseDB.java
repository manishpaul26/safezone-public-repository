package com.track.safezone.database.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.track.safezone.beans.User;
import com.track.safezone.database.SafeZoneDatabase;

import java.util.Date;

public class FirebaseDB implements SafeZoneDatabase {

    private static FirebaseFirestore mDatabase;
    private static FirebaseUser currentUser;

    private static FirebaseDB firebase;
    private static CollectionReference collection;

    private FirebaseDB() {
    }

    public static SafeZoneDatabase getInstance() {

        if (mDatabase == null || currentUser == null) {
            firebase = new FirebaseDB();
            mDatabase = FirebaseFirestore.getInstance();

            collection = mDatabase.collection("underobservation-users");

            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        return firebase;
    }


    @Override
    public void addUserDetails(User user) {

    }

    @Override
    public void updatePhoneNumber(User user) {
       // mDatabase.child("underobservation-users").child(user.getUserId()).child("phoneNumber").setValue(user.getPhoneNumber());
    }

    @Override
    public void updateUserLocation(User user) {
        collection.document(user.getUserId()).set(user);
    }

    @Override
    public void startQuarantineActivity(Date date) {
        collection.document(currentUser.getUid()).update("observationStartTime", date);
    }

}
