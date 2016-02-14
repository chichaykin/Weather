package com.mich.weather;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.mich.weather.data.Coord;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.services.api.WeatherService;
import com.mich.weather.services.api.WeatherServiceApi;
import com.mich.weather.utils.L;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func0;

public class App extends Application {

    public static Context sContext;
    private SharedPreferences mPreferences;
    private static App sInstance;
    private WeatherServiceApi mWeatherService;
    private List<WeatherLocationPojo> mList;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        mPreferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);

        sInstance = this;
        sContext = getApplicationContext();
        mWeatherService = WeatherService.apiService();

    }

    private List<WeatherLocationPojo> createDB() {
        List<WeatherLocationPojo> list = new ArrayList<>();
        WeatherLocationPojo current = new WeatherLocationPojo();
        current.Name = WeatherLocationPojo.CURRENT_LOCATION_NAME;
        current.insert();
        list.add(current);

        WeatherLocationPojo city = new WeatherLocationPojo();
        city.Name = "New York";
        city.Latitude = 43.000351;
        city.Longitude = -75.499901;
        city.insert();
        list.add(city);

        WeatherLocationPojo city2 = new WeatherLocationPojo();
        city2.Name = "London";
        city2.Latitude = 40.445;
        city2.Longitude = -95.234978;
        city2.insert();
        list.add(city2);

        return list;
    }

    public SharedPreferences getPreferences() {
        return mPreferences;
    }

    public List<WeatherLocationPojo> getLocationList() {
        return mList;
    }


    public Observable<WeatherLocationPojo> getLocations() {
        return Observable.defer(new Func0<Observable<WeatherLocationPojo>>() {
            @Override
            public Observable<WeatherLocationPojo> call() {
                mList = new Select().all().from(WeatherLocationPojo.class).queryList();
                L.d("Stored locations: %s", mList.toString());
                if (mList.isEmpty()) {
                    mList = createDB();
                }
                return Observable.from(mList);
            }
        });
    }

    public Coord build(WeatherLocationPojo pojo, Location location) {
        if (WeatherLocationPojo.CURRENT_LOCATION_NAME.equals(pojo.Name)) {
            if (pojo.Latitude == 0.0) {
                if (location == null) {
                    throw new RuntimeException("Cannot get current location");
                }
                pojo.Longitude = location.getLongitude();
                pojo.Latitude = location.getLatitude();
                pojo.save();
            }
        }
        Coord loc = new Coord();
        loc.setLat(pojo.Latitude);
        loc.setLon(pojo.Longitude);
        return loc;
    }

    public WeatherServiceApi getWeatherService() {
        return mWeatherService;
    }

}