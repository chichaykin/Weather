package com.mich.weather.repositories;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = AppDatabase.NAME, allFields = true)
public class WeatherLocationPojo extends BaseModel {
    public static final String CURRENT_LOCATION_NAME = "current";

    @PrimaryKey
    public String Name;
    public double Latitude = 0.0;
    public double Longitude = 0.0;

    @Override
    public String toString() {
        return "WeatherLocationPojo{" +
                "Name='" + Name + '\'' +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                '}';
    }
}
