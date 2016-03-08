package com.mich.weather.data;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WeatherForecast {
    @SerializedName("dt")
    private long timestamp;
    private Main main;
    @SerializedName("weather")
    private List<Weather> weathers = new ArrayList<>();
    private Clouds clouds;
    private Wind wind;
    @SerializedName("dt_txt")
    private String time;

    public WeatherForecast() {}

    public WeatherForecast(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeathers() {
        return weathers;
    }

    public void setWeather(List<Weather> weather) {
        this.weathers = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" +
                "timestamp=" + timestamp +
                ", main=" + main +
                ", weather=" + weathers +
                ", clouds=" + clouds +
                ", wind=" + wind +
                ", time='" + time + '\'' +
                '}';
    }
}
