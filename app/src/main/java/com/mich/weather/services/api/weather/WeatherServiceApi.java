package com.mich.weather.services.api.weather;


import com.mich.weather.data.WeatherResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface WeatherServiceApi {

    @GET("/data/2.5/forecast?units=metric")
    Observable<WeatherResponse> getCityWeather(@Query("q") String city);

    @GET("/data/2.5/forecast?units=metric")
    Observable<WeatherResponse> getCurrentWeather(@Query("lon") double longitude,
                                                  @Query("lat") double latitude);

}
