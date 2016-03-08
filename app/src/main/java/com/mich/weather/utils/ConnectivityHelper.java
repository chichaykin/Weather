package com.mich.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mich.weather.App;

public class ConnectivityHelper {
    private static final ConnectivityManager sConnectivityManager =
            (ConnectivityManager) App.sContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = sConnectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

}
