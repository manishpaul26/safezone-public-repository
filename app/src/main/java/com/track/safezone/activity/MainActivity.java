package com.track.safezone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.track.safezone.R;
import com.track.safezone.beans.User;
import com.track.safezone.utils.GpsUtils;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

    }

    public void proceedToLocation(View view) {

        EditText mFirstName = (EditText) findViewById(R.id.input_firstName);
        EditText mLastName = (EditText) findViewById(R.id.input_lastName);
        EditText mEmailAddress = findViewById(R.id.emailAddress);

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String emailAddress = mEmailAddress.getText().toString();

        //emsil field read

        String name = firstName + " " + lastName;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();



        Thread userDetailsUpdateThread = new Thread(() -> {
            user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
        });

        userDetailsUpdateThread.start();

        Intent i = new Intent(this, DependentDetailsActivity.class);
        i.putExtra("user", new User.UserBuilder(firstName, lastName).phone(user.getPhoneNumber()).address(emailAddress).build());
        startActivity(i);


        // email
        // New activity ->

    }
}
