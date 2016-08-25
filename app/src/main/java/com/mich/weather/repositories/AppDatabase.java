package com.mich.weather.repositories;

import com.raizlabs.android.dbflow.annotation.Database;

@SuppressWarnings("WeakerAccess")
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    static final String NAME = "WeatherDatabase";
    @SuppressWarnings("WeakerAccess")
    static final int VERSION = 2;
}
