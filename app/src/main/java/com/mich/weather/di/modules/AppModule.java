package com.mich.weather.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;

import com.mich.weather.R;
import com.mich.weather.data.WeatherResponse;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.services.api.location.LocationService;
import com.mich.weather.services.api.weather.WeatherServiceApi;
import com.mich.weather.utils.L;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

@Module
public class AppModule {

    private Application mApplication;
    private LocationService mLocationService;

    public AppModule(Application application) {
        mApplication = application;
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

    private List<WeatherLocationPojo> createDB() {
        List<WeatherLocationPojo> list = new ArrayList<>();
        Resources resources = mApplication.getResources();
        //current
        WeatherLocationPojo current = new WeatherLocationPojo(CURRENT_LOCATION_NAME, null);
        current.insert();
        list.add(current);

        for(String city : resources.getStringArray(R.array.default_cities)) {
            current = new WeatherLocationPojo(city, null);
            current.insert();
            list.add(current);
        }
        return list;
    }


    @Provides
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

    private Observable<WeatherResponse> getCurrentLocation(LocationService locationService, final WeatherServiceApi api) {
        return locationService.getLocation()
                .flatMap(new Func1<Location, Observable<WeatherResponse>>() {
                    @Override
                    public Observable<WeatherResponse> call(Location location) {
                        return api.getCurrentWeather(
                                location.getLongitude(), location.getLatitude());
                    }
                });
    }

    public Observable<WeatherResponse> getCurrentLocationObservable(boolean resetCachedLocation) {
        if (resetCachedLocation) {
            mLocationService.resetLocation();
        }
        return getCurrentLocation(mLocationService, );
    }

    public Observable<WeatherResponse> geCityObservable(WeatherLocationPojo location) {
        StringBuilder builder = new StringBuilder(location.city);
        if (!StringUtils.isBlank(location.country)) {
            builder.append(",").append(location.country);
        }
        return getWeatherService().getCityWeather(builder.toString());
    }

    @Provides
    @Singleton
    public LocationService locationService() {
        CURRENT_LOCATION_NAME = mApplication.getResources().getString(R.string.Current);
        final LocationManager locationManager = (LocationManager) mApplication
                .getSystemService(Context.LOCATION_SERVICE);
        mLocationService = new LocationService(locationManager);
    }
}
