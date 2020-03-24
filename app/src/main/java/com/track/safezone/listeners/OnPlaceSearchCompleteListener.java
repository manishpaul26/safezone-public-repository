package com.track.safezone.listeners;

import com.google.android.libraries.places.api.net.FetchPlaceResponse;

public interface OnPlaceSearchCompleteListener {

    void onPlaceSearchSuccess(FetchPlaceResponse result);

    void onPlaceSearchFailure();
}
