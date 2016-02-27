package com.dev.thiago.ambientmonitoring.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.enums.Season;

/**
 * Created by thiago on 26/02/16.
 */
public class SeasonParameters {

    private Season season;

    private Float maxTemp;

    private Float minTemp;

    private Float maxHumidity;

    private Float minHumidity;

    public SeasonParameters(Season season) {
        this.season = season;
    }

    public int getColorByTemperature(Float temperature, Context context) {

        if (temperature > maxTemp) {

            return ContextCompat.getColor(context, R.color.hot_color);

        } else if (temperature < minTemp) {

            return ContextCompat.getColor(context, R.color.cold_color);

        } else {

            return ContextCompat.getColor(context, R.color.normal_color);
        }
    }

    public int getColorByHumidity(Float humidity, Context context) {

        if (humidity > maxHumidity) {

            return ContextCompat.getColor(context, R.color.wet_color);

        } else if (humidity < minHumidity) {

            return ContextCompat.getColor(context, R.color.dry_color);

        } else {

            return ContextCompat.getColor(context, R.color.normal_color);
        }
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
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

    public Float getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(Float maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public Float getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(Float minHumidity) {
        this.minHumidity = minHumidity;
    }
}
