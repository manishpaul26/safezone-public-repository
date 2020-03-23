package com.track.safezone.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.track.safezone.R;
import com.track.safezone.services.FaceClientService;
import com.track.safezone.utils.ViewHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CameraUploadFirstImageActivity extends AppCompatActivity {

    private static final String TAG = "CameraUploadFirstImageA";

    private Button openCameraButton;
    private ImageView imageView;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private ImageView retakeImageIcon;
    private ImageView confirmImageIcon;
    private ImageView cameraPlaceholderIcon;

    private ProgressDialog detectionProgressDialog;

    private final FaceServiceClient faceServiceClient = FaceClientService.getFaceServiceClient();

    // TODO NO use
    private UUID faceID_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_upload_first_image);

        Bundle bundle = getIntent().getExtras();
        Class clazz = (Class) bundle.get(Constants.RETURN_ACTIVITY);

        openCameraButton =  findViewById(R.id.button_upload_image);
        imageView = findViewById(R.id.image_initial_upload);

        retakeImageIcon = findViewById(R.id.button_retake_image);
        confirmImageIcon = findViewById(R.id.button_confirm_image);

        cameraPlaceholderIcon = (ImageView) findViewById(R.id.image_camera_placeholder_icon);

        detectionProgressDialog = new ProgressDialog(this);

        openCameraButton.setOnClickListener(v -> dispatchTakePictureIntent());
        retakeImageIcon.setOnClickListener(v -> dispatchTakePictureIntent());
        confirmImageIcon.setOnClickListener(v -> proceedToStartObservationActivity(clazz));


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            detectAndFrame(imageBitmap);

            openCameraButton.setVisibility(View.INVISIBLE);
            ViewHelper.showViews(retakeImageIcon, confirmImageIcon, cameraPlaceholderIcon);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        } else {
            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "dispatchTakePictureIntent: ", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.track.safezone",
                        photoFile);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }



    private void proceedToStartObservationActivity(Class clazz) {

        clazz = clazz == null ? StartQuarantineActivity.class : clazz;
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }



    // <snippet_detection_methods>
    // Detect faces by uploading a face image.
    // Frame faces after detection.
    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null          // returnFaceAttributes:
                                    /* new FaceServiceClient.FaceAttributeType[] {
                                        FaceServiceClient.FaceAttributeType.Age,
                                        FaceServiceClient.FaceAttributeType.Gender }
                                    */
                            );

                            SharedPreferences sharedPreferences = getSharedPreferences("com.track.safezone.photopaths", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (result.length > 0 ) {
                                if (sharedPreferences.contains("leastSignificantBits_1")) {
                                    editor.putLong("leastSignificantBits_2", result[0].faceId.getLeastSignificantBits());
                                    editor.putLong("mostSignificantBits_2", result[0].faceId.getMostSignificantBits());

                                    Log.i(TAG, "CAMERAAAAAA doInBackground: CAMERAAA  second image LSB: " + result[0].faceId.getLeastSignificantBits() + " IMAGE STRING:" + result[0].faceId.toString());
                                } else {
                                    editor.putLong("leastSignificantBits_1", result[0].faceId.getLeastSignificantBits());
                                    editor.putLong("mostSignificantBits_1", result[0].faceId.getMostSignificantBits());

                                    Log.i(TAG, "CAMERAAAAAA doInBackground: CAMERAAA  ORIGINAL image LSB: " + result[0].faceId.getLeastSignificantBits() + " IMAGE STRING:" + result[0].faceId.toString());
                                }
                                editor.commit();
                            }

                            if (faceID_1 != null) {
                                VerifyResult verification = faceServiceClient.verify(faceID_1, result[0].faceId);
                                if (verification.isIdentical) {
                                    Log.e(TAG, "doInBackground: " + "FAAAAD MASSSTT!!!" + verification.confidence );
                                    //Toast.makeText(AzureFaceActivity.this, "FAAAAD MASSSTT!!!" + verification.confidence, Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "doInBackground: " + "LELE Confidenc!!!" + verification.confidence );

                                    //Toast.makeText(AzureFaceActivity.this, "LELE Confidence" + verification.confidence, Toast.LENGTH_SHORT).show();
                                }
                            }
                            faceID_1 = result.length > 0 ? result[0].faceId : null;
                            //Log.e(TAG, "doInBackground: " + faceId);
                            if (result == null){
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));
                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }
                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();

                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (result == null) return;

                        ImageView imageView = findViewById(R.id.image_initial_upload);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();
                    }
                };

        detectTask.execute(inputStream);
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
    // </snippet_detection_methods>

    // <snippet_drawrectangles>
    private static Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }
}
