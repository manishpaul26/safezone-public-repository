package com.track.safezone.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.track.safezone.listeners.OnPlaceSearchCompleteListener;
import com.track.safezone.places.PlacesAutoCompleteAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocationAutoCompleteAdapter implements AdapterView.OnItemClickListener {
    private final PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    private final OnPlaceSearchCompleteListener onPlaceSearchCompleteListener;
    private final PlacesClient placesClient;

    public LocationAutoCompleteAdapter(PlacesAutoCompleteAdapter placesAutoCompleteAdapter, OnPlaceSearchCompleteListener onPlaceSearchCompleteListener, PlacesClient placesClient) {
        this.placesAutoCompleteAdapter = placesAutoCompleteAdapter;
        this.onPlaceSearchCompleteListener = onPlaceSearchCompleteListener;
        this.placesClient = placesClient;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        PlacesAutoCompleteAdapter.PlaceAutocomplete place = placesAutoCompleteAdapter.getItem(position);

        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(String.valueOf(place.placeId), new ArrayList<>(Arrays.asList(Place.Field.LAT_LNG)));
        Task<FetchPlaceResponse> placeTask = placesClient.fetchPlace(fetchPlaceRequest);



        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.


        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores * 2, numCores *2,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());


        placeTask.addOnCompleteListener(executor, new OnCompleteListener<FetchPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FetchPlaceResponse> task) {

                if (placeTask.isSuccessful()) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FetchPlaceResponse result = placeTask.getResult();
                            onPlaceSearchCompleteListener.onPlaceSearchSuccess(result);
                        }

                    });

                } else {
                    onPlaceSearchCompleteListener.onPlaceSearchFailure();
                }
                // ...
            }
        });

    }
}
