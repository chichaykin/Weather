package com.mich.weather;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;

import com.mich.weather.data.WeatherResponse;
import com.mich.weather.di.components.ActivityComponent;
import com.mich.weather.di.modules.AppModule;
import com.mich.weather.di.modules.WeatherModule;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.services.api.location.LocationService;
import com.mich.weather.services.api.weather.WeatherServiceApi;
import com.mich.weather.utils.L;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class App extends Application {
    private static final String BASE_URL = "http://api.openweathermap.org";
    private static final long CACHE_SIZE = 1024 * 1024 * 10;


    private ActivityComponent mActivityComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);

        mAppComponent = DaggerAppComponent.builder()
                .applicationModule(new AppModule(this))
                .build();
        mAppComponent.inject(this);


    }


    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }
}