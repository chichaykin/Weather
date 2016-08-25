package com.mich.weather.di.components;


import android.content.SharedPreferences;

import com.mich.weather.di.modules.AppModule;
import com.mich.weather.di.modules.WeatherModule;
import com.mich.weather.services.api.location.LocationService;
import com.mich.weather.services.api.weather.WeatherServiceApi;

import javax.inject.Singleton;

import dagger.Component;

@SuppressWarnings("unused")
@Singleton
@Component(modules={AppModule.class, WeatherModule.class})
public interface AppComponent {
    WeatherServiceApi weatherApi();
    SharedPreferences sharedPreferences();
    LocationService locationManager();
}