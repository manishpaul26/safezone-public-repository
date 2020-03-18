package com.track.safezone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.track.safezone.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUploadFirstImageActivity extends AppCompatActivity {

    private static final String TAG = "CameraUploadFirstImageA";

    private Button openCameraButton;
    private ImageView imageView;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private ImageView retakeImageIcon;
    private ImageView confirmImageIcon;

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

            openCameraButton.setVisibility(View.INVISIBLE);
            retakeImageIcon.setVisibility(View.VISIBLE);
            confirmImageIcon.setVisibility(View.VISIBLE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
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
}
