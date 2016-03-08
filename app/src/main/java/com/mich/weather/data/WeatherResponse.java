
package com.mich.weather.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WeatherResponse {
    private City city;
    @SerializedName("list")
    private List<WeatherForecast> forecast = new ArrayList<>();

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<WeatherForecast> getForecast() {
        return forecast;
    }

    public void setForecast(List<WeatherForecast> forecast) {
        this.forecast = forecast;
    }

    @Override
    public String toString() {
        return "WeatherResponse{" +
                "city=" + city +
                ", forecast=" + forecast +
                '}';
    }
}
