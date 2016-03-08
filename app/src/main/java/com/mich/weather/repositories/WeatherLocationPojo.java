package com.mich.weather.repositories;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;

@SuppressWarnings("FieldCanBeLocal")
@Table(database = AppDatabase.class,
        uniqueColumnGroups = {@UniqueGroup(groupNumber = 1, uniqueConflict = ConflictAction.FAIL)},
        allFields = true)
public class WeatherLocationPojo extends BaseModel {

    @PrimaryKey
    @Unique(unique = false, uniqueGroups = 1)
    public String city;
    @Unique(unique = false, uniqueGroups = 1)
    public String country;
    private final double latitude = 0.0;
    private final double longitude = 0.0;

    public WeatherLocationPojo() {
    }

    public WeatherLocationPojo(String city, String country) {
        this.city = city;
        this.country = country;
    }

    @Override
    public String toString() {
        return "WeatherLocationPojo{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
