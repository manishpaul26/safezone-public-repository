package com.track.safezone.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.track.safezone.R;
import com.track.safezone.adapters.LocationAutoCompleteAdapter;
import com.track.safezone.beans.User;
import com.track.safezone.database.SafeZoneDatabase;
import com.track.safezone.database.impl.FirebaseDB;
import com.track.safezone.listeners.OnPlaceSearchCompleteListener;
import com.track.safezone.places.PlacesAutoCompleteAdapter;
import com.track.safezone.utils.PermissionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnPlaceSearchCompleteListener {

    private GoogleMap mMap;

    private static final String TAG = "LocationActivity";

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private Location userLocation;

    private AutoCompleteTextView searchText;
    private ImageView gpsIcon;

    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;

    private PlacesClient placesClient = null;
    private Button confirmButton;
    private User userData;
    private Place userPlace;

    private SafeZoneDatabase database;
    private int numberOfTriesForLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Bundle extras = getIntent().getExtras();
        this.userData = (User) extras.get("user");

        this.searchText = (AutoCompleteTextView) findViewById(R.id.input_search_location);
        this.gpsIcon = (ImageView) findViewById(R.id.ic_gps);
        this.confirmButton = (Button) findViewById(R.id.button_confirm_location);

        this.database = FirebaseDB.getInstance();

        String GOOGLE_MAPS_API_KEY = getResources().getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(this, GOOGLE_MAPS_API_KEY);
        }

        this.placesClient = Places.createClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);



            //TODO
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                displayNeverAskAgainDialog();
                /*if (PermissionUtils.neverAskAgainSelected(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                }*/
            }
            // [END maps_check_location_permission]
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getUserLocation();

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        enableMyLocation();
        initializeViews();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // ...
            PermissionUtils.RationaleDialog.newInstance(requestCode, false).show(this.getSupportFragmentManager(), "dialog");
            mPermissionDenied = true;

        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    private void initializeViews() {
        Log.d(TAG, "Initializing search bar");


        //searchText field
        this.placesAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(this, placesClient);
        final AdapterView.OnItemClickListener locationAutoCompleteAdapter =
                new LocationAutoCompleteAdapter(placesAutoCompleteAdapter, this, placesClient);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    searchGeoLocation();
                }
                return false;
            }
        });

        searchText.setAdapter(placesAutoCompleteAdapter);
        searchText.setOnItemClickListener(locationAutoCompleteAdapter);

        // gps icon
        gpsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserLocation();
            }
        });


        // confirm button
        confirmButton.setOnClickListener(button -> {
            userData.setGpsLocation(userPlace);
            userData.setIsolation();

            database.updateUserLocationAndIsolationTime(userData);
            Intent intent = new Intent(this, CameraUploadFirstImageActivity.class);
            intent.putExtra(Constants.RETURN_ACTIVITY, StartQuarantineActivity.class);
            //intent.putExtra("user", userData);

            startActivity(intent);

        });
    }

    private void searchGeoLocation() {
        Log.d(TAG, "geoLocate: Inside geolocate");

        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(LocationActivity.this);

        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e){
            Log.e(TAG, "geoLocate: IOEXception", e);
        }

        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            Log.d(TAG, "geoLocate: found a location" + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f, "SafeLocation", true);
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void displayNeverAskAgainDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need Location location location! "
                + "Settings screen.\n\nSelect Permissions -> Enable permission");
        builder.setCancelable(false);
        builder.setPositiveButton("Permit Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void getUserLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        numberOfTriesForLocation++;
        Task fetchLocationTask  = fusedLocationProviderClient.getLastLocation();

        try {

            fetchLocationTask.addOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!!");

                        userLocation = (Location) task.getResult();
                        if (userLocation == null) {
                            throw new NullPointerException("Location could not be retrieved properly");
                        }
                        moveCamera(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 15f, "", false);
                    }
                }

            });

        } catch (SecurityException e) {
            Log.e(TAG, "ERRORRR!!!" + e);
        } catch (NullPointerException e) {
            Log.e(TAG, "getUserLocation: {}", e);
            if (numberOfTriesForLocation < 2) {
                Log.e(TAG, "getUserLocation: Will try to get user location again.. {}", e);
            } else {
                Log.e(TAG, "getUserLocation: Could not get user location {}", e);
            }
        }
    }



    private void hideSoftKeyboard() {
        this.searchText.onEditorAction(IME_ACTION_DONE);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    private void moveCamera(LatLng latLng, float zoom, String title, boolean addMarker) {
        Log.e(TAG, "moveCamera: " + Thread.currentThread());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        if (addMarker) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .draggable(true);
            mMap.addMarker(markerOptions);
        }
    }


    @Override
    public void onPlaceSearchSuccess(FetchPlaceResponse result) {
        moveCamera(result.getPlace().getLatLng(), 15f, "Location", true);
        confirmButton.setVisibility(View.VISIBLE);
        userPlace = result.getPlace();
        hideSoftKeyboard();
    }

    @Override
    public void onPlaceSearchFailure() {
        Toast.makeText(this, "Oops, something went wrong! Please try searching again.", Toast.LENGTH_SHORT).show();
    }
}
