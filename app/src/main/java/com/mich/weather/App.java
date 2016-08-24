package com.mich.weather;

import android.app.Application;
import android.content.Context;

import com.mich.weather.di.components.AppComponent;
import com.mich.weather.di.components.DaggerAppComponent;
import com.mich.weather.di.modules.AppModule;
import com.mich.weather.di.modules.WeatherModule;
import com.raizlabs.android.dbflow.config.FlowManager;

public class App extends Application {
    private static final String BASE_URL = "http://api.openweathermap.org";
    private static final long CACHE_SIZE = 1024 * 1024 * 10;

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this, R.string.Current))
                .weatherModule(new WeatherModule(BASE_URL, CACHE_SIZE, this.getApplicationContext()))
                .build();
    }

    public static App get(Context context) {
        return (App)context.getApplicationContext();
    }

    public AppComponent getComponent() {
        return mAppComponent;
    }
}