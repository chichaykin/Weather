package com.mich.weather.utils;


import com.mich.weather.repositories.WeatherLocationPojo;
import com.mich.weather.repositories.WeatherLocationPojo_Table;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.SQLite;

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


}
