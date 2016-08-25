package com.mich.weather.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import com.mich.weather.di.scopes.ApplicationContext;
import com.mich.weather.services.api.location.LocationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @SuppressWarnings("unused")
    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @SuppressWarnings("unused")
    @Provides
    @ApplicationContext
    Context providesContext() {
        return mApplication;
    }

    @SuppressWarnings("unused")
    @Provides
    @Singleton
    public SharedPreferences providesPreferences() {
        return mApplication.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    @SuppressWarnings("unused")
    @Provides
    @Singleton
    public LocationService providesLocationService() {
        final LocationManager locationManager = (LocationManager) mApplication
                .getSystemService(Context.LOCATION_SERVICE);
        return new LocationService(locationManager);
    }
}
