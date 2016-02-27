package com.dev.thiago.ambientmonitoring;

/**
 * Created by thiago on 27/02/16.
 */
public class SensorChangedEvent {

    private Float temperature;

    private Float humidity;

    public SensorChangedEvent(Float temperature, Float humidity) {

        this.temperature = temperature;
        this.humidity = humidity;
    }

    public SensorChangedEvent() {

    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }
}
