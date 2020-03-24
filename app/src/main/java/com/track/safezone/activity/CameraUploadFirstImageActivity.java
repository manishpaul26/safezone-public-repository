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

        AsyncTask<InputStream, String, Face> detectTask =
                new AsyncTask<InputStream, String, Face>() {
                    CameraError cameraError = null;

                    @Override
                    protected Face doInBackground(InputStream... params) {
                        try {
                            Face face = null;
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null          // returnFaceAttributes:
                            );

                            SharedPreferences sharedPreferences = getSharedPreferences("com.track.safezone.photopaths", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (result != null && result.length > 0) {
                                if (result.length == 1) {
                                    face = result[0];
                                    if (sharedPreferences.contains("leastSignificantBits_1")) {
                                        editor.putLong("leastSignificantBits_2", face.faceId.getLeastSignificantBits());
                                        editor.putLong("mostSignificantBits_2", face.faceId.getMostSignificantBits());

                                        Log.i(TAG, "CAMERAAAAAA doInBackground: CAMERAAA  second image LSB: " + result[0].faceId.getLeastSignificantBits() + " IMAGE STRING:" + result[0].faceId.toString());
                                    } else {
                                        editor.putLong("leastSignificantBits_1", face.faceId.getLeastSignificantBits());
                                        editor.putLong("mostSignificantBits_1", face.faceId.getMostSignificantBits());

                                        Log.i(TAG, "CAMERAAAAAA doInBackground: CAMERAAA  ORIGINAL image LSB: " + result[0].faceId.getLeastSignificantBits() + " IMAGE STRING:" + result[0].faceId.toString());
                                    }
                                    editor.commit();
                                } else {
                                    cameraError = new CameraError("More than one face detected. Please ensure that no one is around you while clicking the photo", CameraError.ErrorCodes.IMAGE_MORE_THAN_ONE_FACE);
                                }

                            } else {
                                publishProgress("Unable to detect..");
                                cameraError = new CameraError("Sorry, please click a clear photo of your face with good lighting.", CameraError.ErrorCodes.IMAGE_CAPTURED_INCORRECTLY);
                            }

                            return face;
                        } catch (Exception e) {
                            cameraError = new CameraError(String.format(
                                    "Image capturing failed: %s", e.getMessage()), CameraError.ErrorCodes.IMAGE_UNKNOWN_ERROR);
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
                    protected void onPostExecute(Face result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();

                        if (cameraError != null) {
                            showError(cameraError.toString());

                        }

                        if (result == null) {
                            return;
                        } else{
                            ImageView imageView = findViewById(R.id.image_initial_upload);
                            imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result));
                            imageBitmap.recycle();
                    }
                    }
                };

        detectTask.execute(inputStream);
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openCameraButton.performClick();
                    }})
                .create().show();
    }
    // </snippet_detection_methods>

    // <snippet_drawrectangles>
    private static Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face face) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        if (face != null) {
            FaceRectangle faceRectangle = face.faceRectangle;
            canvas.drawRect(
                    faceRectangle.left,
                    faceRectangle.top,
                    faceRectangle.left + faceRectangle.width,
                    faceRectangle.top + faceRectangle.height,
                    paint);

        }
        return bitmap;
    }

    class CameraError {
        private String errorString;

        private String errorCode;

        public CameraError(String errorString, String errorCode) {
            this.errorString = errorString;
            this.errorCode = errorCode;
        }

        public String getErrorString() {
            return errorString;
        }

        public String getErrorCode() {
            return errorCode;
        }

        @Override
        public String toString() {
            return errorString + errorCode;
        }

        class ErrorCodes extends com.track.safezone.utils.ErrorCodes {
            public static final String IMAGE_CAPTURED_INCORRECTLY = ERROR_CODE  + "111";
            public static final String IMAGE_MORE_THAN_ONE_FACE = ERROR_CODE  + "222";
            public static final String IMAGE_UNKNOWN_ERROR = ERROR_CODE  + "333";
        }
    }
}
