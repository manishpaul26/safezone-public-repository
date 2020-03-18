package com.track.safezone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.track.safezone.R;
import com.track.safezone.database.SafeZoneDatabase;
import com.track.safezone.database.impl.FirebaseDB;

import java.util.Map;

public class LauncherLogoActvitity extends AppCompatActivity {

    private static final String TAG = "LauncherLogoActvitity";

    private FirebaseAuth mAuth;

    private SafeZoneDatabase firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Inside logo launcher activity..");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_logo_actvitity);

        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: Getting firebase instance now..");

        firebaseDB = FirebaseDB.getInstance();
        Map<String, Object> currentUser = FirebaseDB.userData;
        Intent intent = new Intent(this, LoginSignUpActivity.class);

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentUser == null) {
            intent = new Intent(this, LoginSignUpActivity.class);
        } else {
            boolean isUnderObservation = firebaseDB.getQuarantineActivityStatus();

            if (isUnderObservation) {
                intent = new Intent(this, ConfirmObservationStatusActivity.class);
            }
        }
        startActivity(intent);
    }
}
