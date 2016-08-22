package com.mich.weather.di.components;


import android.content.SharedPreferences;
import android.location.LocationManager;

import com.mich.weather.di.modules.AppModule;
import com.mich.weather.di.modules.WeatherModule;
import com.mich.weather.services.api.weather.WeatherServiceApi;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, WeatherModule.class})
public interface AppComponent {

    WeatherServiceApi WeatherApi();
    SharedPreferences sharedPreferences();
    LocationManager locationManager();
}