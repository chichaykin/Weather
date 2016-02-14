package com.mich.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mich.weather.App;

public class ConnectivityHelper {
    private static ConnectivityManager sConnectivityManager =
            (ConnectivityManager) App.sContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

    public static boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = sConnectivityManager.getActiveNetworkInfo();
        return activeNetwork != null ? activeNetwork.isConnected() : false;
    }

}
