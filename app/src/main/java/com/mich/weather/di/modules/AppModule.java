package com.mich.weather.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.support.annotation.StringRes;

import com.mich.weather.R;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.services.api.location.LocationService;
import com.mich.weather.utils.L;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.functions.Func0;

@Module
public class AppModule {

    private final String mCurrentLocationName;
    private Application mApplication;

    public AppModule(Application application, @StringRes int currentLocationName) {
        mApplication = application;
        mCurrentLocationName = mApplication.getResources().getString(currentLocationName);
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    public SharedPreferences getPreferences() {
        return mApplication.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    @Provides
    String provideCurrentLocationName() {
        return mCurrentLocationName;
    }

    @Provides
    @Singleton
    public Observable<List<WeatherLocationPojo>> getStoredLocations() {
        return Observable.defer(new Func0<Observable<List<WeatherLocationPojo>>>() {
            @Override
            public Observable<List<WeatherLocationPojo>> call() {
                List<WeatherLocationPojo> list = new Select().from(WeatherLocationPojo.class).queryList();
                L.d("Stored locations: %s", list.toString());
                if (list.isEmpty()) {
                    list = createDB();
                }
                return Observable.just(list);
            }
        });
    }

    @Provides
    @Singleton
    public LocationService locationService() {
        final LocationManager locationManager = (LocationManager) mApplication
                .getSystemService(Context.LOCATION_SERVICE);
        return new LocationService(locationManager);
    }

    private List<WeatherLocationPojo> createDB() {
        List<WeatherLocationPojo> list = new ArrayList<>();
        Resources resources = mApplication.getResources();
        //current
        WeatherLocationPojo current = new WeatherLocationPojo(mCurrentLocationName, null);
        current.insert();
        list.add(current);

        for(String city : resources.getStringArray(R.array.default_cities)) {
            current = new WeatherLocationPojo(city, null);
            current.insert();
            list.add(current);
        }
        return list;
    }
}
