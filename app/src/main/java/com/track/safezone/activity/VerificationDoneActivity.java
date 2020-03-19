package com.track.safezone.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.track.safezone.R;
import com.track.safezone.utils.ViewHelper;

public class VerificationDoneActivity extends AppCompatActivity {


    private ProgressBar progressBar;
    private int progressStatus = 0;

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_done);

        ImageView success = findViewById(R.id.image_icon_tick_verification_success);
        TextView check = findViewById(R.id.text_thank_you);
        TextView verifiyingPhotoMsg = findViewById(R.id.text_verifying_photo);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_verifying_photo);

        // Start long running operation in a background thread
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        ViewHelper.hideViews(progressBar, verifiyingPhotoMsg);
                        ViewHelper.showViews(check, success);

                    }
                    // your UI code here
                });


            }
        });
        t.start();


    }
}
