package com.track.safezone.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
    public static final double RADIUS = 0.500900;

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

                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setProgress(0);
                progressDialog.setMessage("Fetching your location..");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setProgress(0);
                progressDialog.setMax(100);
                progressDialog.show();
                LatLng latLng = database.getLocationData(getApplicationContext());
                progressDialog.hide();
                confirmPresenceButton.setEnabled(false);

                if (latLng != null) {
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();

                    if (longitude > latLng.longitude + RADIUS || longitude < longitude - RADIUS
                            || latitude > latLng.latitude + RADIUS || latitude < latLng.latitude - RADIUS) {

                        TextView textConfirmMessage = findViewById(R.id.text_confirm_periodic_location);
                        TextView textWarningError = findViewById(R.id.text_warning_error_outside);

                        textConfirmMessage.setVisibility(View.INVISIBLE);
                        textWarningError.setVisibility(View.VISIBLE);
                        textWarningError.setTextColor(Color.RED);
                        FirebaseDB.getInstance().updatePersonOutsideQuarantine();
                        Toast.makeText(getApplicationContext(), "Please go back to your isolation location.", Toast.LENGTH_SHORT).show();
                    } else {

                        // location alright, now upload image
                        Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setTitle("Upload Photo").setMessage("Please upload a clear photo of your face.");

                        alertDialogBuilder.setPositiveButton("Okay", (dialog, which) -> {
                            Intent intent = new Intent(this, CameraUploadFirstImageActivity.class);
                            intent.putExtra(Constants.RETURN_ACTIVITY, VerificationDoneActivity.class);
                            startActivity(intent);
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
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
