package com.dev.thiago.ambientmonitoring.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by thiago on 26/02/16.
 */
public class WeatherWrapper {

    private Main main;

    @SerializedName("weather")
    private List<Weather> weathers;

    private String name;

    @SerializedName("dt")
    private Date date;

    public Main getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public List<Weather> getWeathers() {
        return weathers;
    }

    public void setWeathers(List<Weather> weathers) {
        this.weathers = weathers;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
