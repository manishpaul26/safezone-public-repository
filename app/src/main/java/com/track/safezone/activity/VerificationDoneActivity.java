package com.track.safezone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.track.safezone.R;
import com.track.safezone.database.impl.FirebaseDB;
import com.track.safezone.services.FaceClientService;
import com.track.safezone.utils.ViewHelper;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class VerificationDoneActivity extends AppCompatActivity {

    private static final String TAG = "VerificationDoneActivit";

    private ProgressBar progressBar;
    private int progressStatus = 0;

    private Handler handler = new Handler();

    //ProgressDialog detectionProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_done);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_verifying_photo);

        //detectionProgressDialog = new ProgressDialog(this);


        compareFaces();

/*
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
                        Thread.sleep(5);
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

                        FirebaseDB.getInstance().updateLastObservationTime();

                    }
                    // your UI code here
                });


            }
        });
        t.start();*/


    }

    private void compareFaces() {

        AsyncTask<InputStream, String, VerifyResult> detectTask =
                new AsyncTask<InputStream, String, VerifyResult>() {
                    String exceptionMessage = "";


                    VerifyResult verification;

                    @Override
                    protected VerifyResult doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            FaceServiceClient faceServiceClient =  FaceClientService.getFaceServiceClient();
                            SharedPreferences sharedPreferences = getSharedPreferences("com.track.safezone.photopaths", MODE_PRIVATE);

                            long image1LeastSignificantBits = sharedPreferences.getLong("leastSignificantBits_1", 0l);
                            long image1MostSignificantBits = sharedPreferences.getLong("mostSignificantBits_1", 0l);
                            long image2LeastSignificantBits = sharedPreferences.getLong("leastSignificantBits_2", 0l);
                            long image2MostSignificantBits = sharedPreferences.getLong("mostSignificantBits_2", 0l);


                            UUID original = new UUID(image1MostSignificantBits,image1LeastSignificantBits);
                            UUID retake = new UUID(image2MostSignificantBits, image2LeastSignificantBits);


                            verification = faceServiceClient.verify(original, retake);
                            if (verification.isIdentical) {
                                Log.e(TAG, "doInBackground: " + "FAAAAD MASSSTT!!!" + verification.confidence );
                                //Toast.makeText(AzureFaceActivity.this, "FAAAAD MASSSTT!!!" + verification.confidence, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "doInBackground: " + "LELE Confidenc!!!" + verification.confidence );

                                //Toast.makeText(AzureFaceActivity.this, "LELE Confidence" + verification.confidence, Toast.LENGTH_SHORT).show();
                            }
                            if (verification == null){
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress("100");
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                        return verification;
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        progressBar.setProgress(0);
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        progressBar.setProgress(50);
                    }
                    @Override
                    protected void onPostExecute(VerifyResult result) {
                        //TODO: update face frames
                        //detectionProgressDialog.dismiss();


                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (verification == null) {
                            showError("KUCH TOH GADBAD HAI!");
                        };
                    }
                };

        detectTask.execute();
        try {
            VerifyResult result = detectTask.get();
            ImageView success = findViewById(R.id.image_icon_tick_verification_success);
            TextView check = findViewById(R.id.text_thank_you);
            TextView verifiyingPhotoMsg = findViewById(R.id.text_verifying_photo);

            ViewHelper.hideViews(progressBar, verifiyingPhotoMsg);

            progressBar.setProgress(100);
            if (result.isIdentical) {

                ViewHelper.showViews(check, success);
            } else {
                Toast.makeText(this, "Sorry, image verification failed!", Toast.LENGTH_SHORT).show();
                success.setImageResource(R.drawable.icon_warning);
            }


            Log.d(TAG, "compareFaces: IM HEREEEEEE waiting to send to DB");
            if (result != null) {
                // TODO add to DB
                FirebaseDB.getInstance().updateLastObservationTime();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .create().show();
    }
}
