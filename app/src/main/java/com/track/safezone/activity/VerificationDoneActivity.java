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

import static com.track.safezone.activity.CameraUploadFirstImageActivity.CameraError.ErrorCodes.IMAGE_CAPTURED_INCORRECTLY;

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

        progressBar = findViewById(R.id.progressBar_verifying_photo);

        compareFaces();

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

                            if (!sharedPreferences.contains("leastSignificantBits_1") || !sharedPreferences.contains("leastSignificantBits_2")) {
                                exceptionMessage = "Sorry, something went wrong! " + IMAGE_CAPTURED_INCORRECTLY;
                                return null;
                            }
                            long image1LeastSignificantBits = sharedPreferences.getLong("leastSignificantBits_1", 0l);
                            long image1MostSignificantBits = sharedPreferences.getLong("mostSignificantBits_1", 0l);
                            long image2LeastSignificantBits = sharedPreferences.getLong("leastSignificantBits_2", 0l);
                            long image2MostSignificantBits = sharedPreferences.getLong("mostSignificantBits_2", 0l);


                            UUID original = new UUID(image1MostSignificantBits,image1LeastSignificantBits);
                            UUID retake = new UUID(image2MostSignificantBits, image2LeastSignificantBits);

                            publishProgress("100");
                            return faceServiceClient.verify(original, retake);

                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        progressBar.setProgress(0);
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        progressBar.setProgress(50);
                    }
                    @Override
                    protected void onPostExecute(VerifyResult result) {
                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                    }
                };

        detectTask.execute();
        try {
            VerifyResult result = detectTask.get();
            ImageView checkMark = findViewById(R.id.image_icon_tick_verification_success);
            TextView resultText = findViewById(R.id.text_thank_you);
            TextView verifyingPhotoMsg = findViewById(R.id.text_verifying_photo);

            ViewHelper.hideViews(progressBar, verifyingPhotoMsg);

            progressBar.setProgress(100);
            if (result != null && result.isIdentical) {
                Log.e(TAG, "doInBackground: " + "The face are identical. Confidence score:  " + result.confidence );
                ViewHelper.showViews(resultText, checkMark);
            } else {
                Log.e(TAG, "doInBackground: " + "Photos do not match : failed confidence" + result.confidence );
                Toast.makeText(this, "Sorry, image verification failed!", Toast.LENGTH_SHORT).show();
                checkMark.setImageResource(0);
                checkMark.setImageResource(R.drawable.icon_warning);
                ViewHelper.showViews(checkMark);
            }


            Log.d(TAG, "compareFaces: Updating last confirmed observation time.");
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
