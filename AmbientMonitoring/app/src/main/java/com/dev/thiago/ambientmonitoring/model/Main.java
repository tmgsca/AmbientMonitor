package com.dev.thiago.ambientmonitoring.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thiago on 26/02/16.
 */
public class Main {

    private Float temp;

    private Float pressure;

    private Float humidity;

    @SerializedName("temp_max")
    private Float maxTemp;

    @SerializedName("temp_min")
    private Float minTemp;

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public Float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Float minTemp) {
        this.minTemp = minTemp;
    }
}
