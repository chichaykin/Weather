package com.mich.weather.repositories;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "WeatherDatabase";
    public static final int VERSION = 1;
}
