package com.mich.weather.presenters;


import android.support.annotation.StringRes;

import com.mich.weather.repositories.WeatherLocationPojo;

import java.util.List;

public interface MainView {
    void updateSpinnerData(List<WeatherLocationPojo> list);

    void replaceWeatherFragment();

    void spinnerAddLocation(WeatherLocationPojo location);

    void spinnerSelectLocation(WeatherLocationPojo location);

    void spinnerRemoveLocation(WeatherLocationPojo location);

    void snackMessage(@StringRes int message);

    void showProgress(boolean show);

    WeatherLocationPojo getCurrentLocation();
}
