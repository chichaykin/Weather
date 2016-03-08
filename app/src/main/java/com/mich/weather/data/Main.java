
package com.mich.weather.data;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Main {
    @SerializedName("temp")
    private float temperature;
    private String pressure;
    private String humidity;
    @SerializedName("temp_min")
    private float tempMin;
    @SerializedName("temp_max")
    private float tempMax;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public float getTempMin() {
        return tempMin;
    }

    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    public float getTempMax() {
        return tempMax;
    }

    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }

    @Override
    public String toString() {
        return "Main{" +
                "temperature=" + temperature +
                ", pressure='" + pressure + '\'' +
                ", humidity='" + humidity + '\'' +
                ", tempMin=" + tempMin +
                ", tempMax=" + tempMax +
                '}';
    }
}
