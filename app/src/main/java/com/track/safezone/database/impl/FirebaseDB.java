package com.track.safezone.database.impl;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.track.safezone.beans.User;
import com.track.safezone.database.SafeZoneDatabase;

import java.util.Date;
import java.util.Map;

public class FirebaseDB implements SafeZoneDatabase {

    private static final String TAG = "FirebaseDB";

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

    @Override
    public LatLng getLocationData(Context context) {

        Task<DocumentSnapshot> task = collection.document(currentUser.getUid()).get();

        while (!task.isComplete()) {
            //Log.e(TAG, "getLocationData: Still runninggg");
        }

        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                Map<String, Object> gpsLocationValues = (Map<String, Object>) document.getData().get("gpsLocation");
                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                Map<String, Double> latLngValues = (Map<String, Double>) gpsLocationValues.get("latLng");
                return new LatLng(latLngValues.get("latitude"), latLngValues.get("longitude"));
            } else {
                Log.d(TAG, "No such document");
            }
        }

        return null;

    }


}
