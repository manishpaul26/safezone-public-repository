package com.track.safezone.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.track.safezone.R;
import com.track.safezone.database.SafeZoneDatabase;
import com.track.safezone.database.impl.FirebaseDB;
import com.track.safezone.utils.ViewHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StartQuarantineActivity extends AppCompatActivity {


    private static final String TAG = "StartQuarantineActivity";

    private SafeZoneDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quarantine);


        this.database = FirebaseDB.getInstance();
        Button startTrackingButton =  (Button) findViewById(R.id.button_start_tracking);
        TextView observationStarted =  (TextView) findViewById(R.id.text_observation_started);
        TextView observationLegalMsg =  (TextView) findViewById(R.id.textLegalDescription);

        startTrackingButton.setOnClickListener(view -> {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            try {
                database.startQuarantineActivity(dateFormat.parse(dateFormat.format(date)));
            } catch (ParseException e) {
                Log.e(TAG, "onCreate: ", e);
            }

            ViewHelper.hideViews(startTrackingButton, observationLegalMsg);
            ViewHelper.showViews(observationStarted);
        });
    }
}
