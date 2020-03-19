package com.track.safezone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.track.safezone.R;

public class ConfirmationScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_screen);


        Button nextButton = findViewById(R.id.button_send_to_start_quarantine);

        nextButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ConfirmObservationStatusActivity.class);
            startActivity(i);
        });


    }
}
