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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.track.safezone.beans.User;
import com.track.safezone.database.SafeZoneDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDB implements SafeZoneDatabase {

    private static final String TAG = "FirebaseDB";

    private static FirebaseFirestore mDatabase;
    private static FirebaseUser currentUser;

    private static FirebaseDB firebase;
    private static CollectionReference collection;
    public static Map<String, Object> userData;

    private FirebaseDB() {
    }


    public static SafeZoneDatabase getInstance() {

        Log.d(TAG, "getInstance: Inside Firebase : Initializing and getting User details");

        if (mDatabase == null || currentUser == null) {
            firebase = new FirebaseDB();
            mDatabase = FirebaseFirestore.getInstance();
            mDatabase.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build());

            collection = mDatabase.collection("underobservation-users");

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                Task<DocumentSnapshot> task = collection.document(currentUser.getUid()).get();

                while (!task.isComplete()) {
                    //Log.e(TAG, "getLocationData: Still runninggg");
                }
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userData = document.getData();
                    }
                }
                ;
            }

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
    public void updateUserLocationAndIsolationTime(User user) {
        collection.document(currentUser.getUid()).set(user);
    }

    @Override
    public void startQuarantineActivity(Date date) {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 14);

        Map<String, Object> update = new HashMap<>(2);
        update.put("observationStartTime", date);
        update.put("isUnderIsolation", true);
        update.put("underIsolationTill", c.getTime());
        collection.document(currentUser.getUid()).update(update);
    }

    @Override
    public boolean getQuarantineActivityStatus() {
        return userData.containsKey("observationStartTime");
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

    @Override
    public void updateLastObservationTime() {
        collection.document(currentUser.getUid()).update("lastObservationTime", Calendar.getInstance().getTime());
    }

    @Override
    public void updatePersonOutsideQuarantine() {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 14);

        Map<String, Object> update = new HashMap<>(2);
        update.put("detectedOutsideQuarantine", true);
        update.put("detectedOutsideQuarantineTime", c.getTime());
        collection.document(currentUser.getUid()).update(update);
    }

}
