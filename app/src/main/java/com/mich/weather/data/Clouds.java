
package com.mich.weather.data;

@SuppressWarnings("unused")
public class Clouds {

    private Double all;

    public Double getAll() {
        return all;
    }

    public void setAll(Double all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return "Clouds{" +
                "all=" + all +
                '}';
    }
}
