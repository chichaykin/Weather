package com.mich.weather.services.api;


import com.mich.weather.data.WeatherLocation;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;


public interface WeatherServiceApi {

    @GET("/data/2.5/weather?units=metric")
    Observable<WeatherLocation> getCityWeather(@Query("q") String city);

    @GET("/data/2.5/weather?units=metric")
    Observable<WeatherLocation> getCurrentWeather(@Query("lon") double longitude,
                                                  @Query("lat") double latitude);

}
