package com.mich.weather.di.modules;

import android.app.Activity;
import android.content.SharedPreferences;

import com.mich.weather.di.scopes.PerActivity;
import com.mich.weather.presenters.MainPresenter;
import com.mich.weather.presenters.MainPresenterImpl;
import com.mich.weather.presenters.MainView;
import com.mich.weather.services.api.location.LocationService;
import com.mich.weather.services.api.weather.WeatherServiceApi;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @SuppressWarnings("unused")
    @Provides
    @PerActivity
    MainPresenter providesPresenter(SharedPreferences preferences,
                                    LocationService locationService, WeatherServiceApi weatherServiceApi) {
        return new MainPresenterImpl(mActivity.getApplicationContext(),
                (MainView)mActivity, preferences, locationService, weatherServiceApi);
    }

}