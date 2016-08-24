package com.mich.weather.utils;


import android.location.Location;

import com.mich.weather.data.WeatherResponse;
import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.repositories.WeatherLocationPojo_Table;
import com.mich.weather.services.api.location.LocationService;
import com.mich.weather.services.api.weather.WeatherServiceApi;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.StringUtils;

import rx.Observable;
import rx.functions.Func1;

public final class Utils {

    private Utils() {
    }

    public static String formatTemperature(float temperature) {
        return String.valueOf(Math.round(temperature)) + "°C";
    }

    public static String getUrl(String icon) {
        return String.format("http://openweathermap.org/img/w/%s.png", icon);
    }

    public static boolean isCityExist(String city, String country) {
        return !SQLite.select().from(WeatherLocationPojo.class)
                .where(WeatherLocationPojo_Table.city.eq(city).collate(Collate.NOCASE))
                .and(WeatherLocationPojo_Table.country.eq(country).collate(Collate.NOCASE))
                .queryList().isEmpty();
    }

    public static Observable<WeatherResponse> getCurrentLocationObservable(boolean resetCachedLocation, LocationService locationService,
                                                                    WeatherServiceApi weatherApi) {
        if (resetCachedLocation) {
            locationService.resetLocation();
        }
        return getCurrentLocation(locationService, weatherApi);
    }

    private static Observable<WeatherResponse> getCurrentLocation(LocationService locationService, final WeatherServiceApi api) {
        return locationService.getLocation()
                .flatMap(new Func1<Location, Observable<WeatherResponse>>() {
                    @Override
                    public Observable<WeatherResponse> call(Location location) {
                        return api.getCurrentWeather(
                                location.getLongitude(), location.getLatitude());
                    }
                });
    }

    public static Observable<WeatherResponse> geCityObservable(WeatherLocationPojo location, WeatherServiceApi weatherApi) {
        StringBuilder builder = new StringBuilder(location.city);
        if (!StringUtils.isBlank(location.country)) {
            builder.append(",").append(location.country);
        }
        return weatherApi.getCityWeather(builder.toString());
    }


}
