package com.track.safezone.utils;

import android.view.View;

public class ViewHelper {


    public static void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    public static void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    public static void showViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }


}
