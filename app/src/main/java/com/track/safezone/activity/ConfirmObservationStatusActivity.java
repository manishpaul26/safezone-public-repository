package com.track.safezone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.track.safezone.R;
import com.track.safezone.database.SafeZoneDatabase;
import com.track.safezone.database.impl.FirebaseDB;
import com.track.safezone.utils.LocationTrack;

public class ConfirmObservationStatusActivity extends AppCompatActivity {


    private static final String TAG = "ConfirmObservationStatu";

    LocationTrack locationTrack;
    private SafeZoneDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_observation_status);

        this.database = FirebaseDB.getInstance();

        Button confirmPresenceButton = (Button) findViewById(R.id.button_confirm_periodic_location);

        confirmPresenceButton.setOnClickListener(v -> {
            locationTrack = new LocationTrack(ConfirmObservationStatusActivity.this);

            if (locationTrack.canGetLocation()) {

                LatLng latLng = database.getLocationData(getApplicationContext());
                if (latLng != null) {
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();

                    if (longitude > latLng.longitude + 0.000400 || longitude < longitude - 0.000400
                            || latitude > latLng.latitude + 0.000400 || latitude < latLng.latitude - 0.000400) {

                        TextView textConfirmMessage = findViewById(R.id.text_confirm_periodic_location);
                        TextView textWarningError = findViewById(R.id.text_warning_error_outside);

                        textConfirmMessage.setVisibility(View.INVISIBLE);
                        textWarningError.setVisibility(View.VISIBLE);
                        textWarningError.setTextColor(Color.RED);

                        Toast.makeText(getApplicationContext(), "GET BACCKKKK!!!", Toast.LENGTH_SHORT).show();
                    } else {

                        // location alright, now upload image
                        Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, CameraUploadFirstImageActivity.class);
                        intent.putExtra(Constants.RETURN_ACTIVITY, CameraUploadFirstImageActivity.class);
                        startActivity(intent);

                    }
                } else {
                    /// TODO handle error
                }


            } else {
                locationTrack.showSettingsAlert();
            }
        });
    }
}
