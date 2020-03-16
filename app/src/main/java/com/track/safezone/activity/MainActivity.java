package com.track.safezone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.track.safezone.R;
import com.track.safezone.beans.User;
import com.track.safezone.utils.GpsUtils;

public class MainActivity extends AppCompatActivity {


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

        EditText contactNumberInput = (EditText) findViewById(R.id.contactNumber);

        String contactNumberText = contactNumberInput.getText().toString();

        Intent i = new Intent(this, LocationActivity.class);

        User u = new User(contactNumberText, "","" ,"");

        i.putExtra("user", u);

        startActivity(i);

    }
}
