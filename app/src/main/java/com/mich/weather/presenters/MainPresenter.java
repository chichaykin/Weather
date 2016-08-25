package com.mich.weather.presenters;


import com.mich.weather.data.WeatherResponse;
import com.mich.weather.repositories.WeatherLocationPojo;

public interface MainPresenter {

    void onRefresh();

    void onActivityCreate();

    void onDestroy();

    void onSpinnerClick(WeatherLocationPojo item);

    void addLocation(String city, String country);

    void removeLocation(WeatherLocationPojo location);

    WeatherResponse getCurrentWeatherData();
}
