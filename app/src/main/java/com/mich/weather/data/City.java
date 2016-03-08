package com.mich.weather.data;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class City {
    private String name;
    private String country;
    @SerializedName("coord")
    private Coordinates coordinates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
