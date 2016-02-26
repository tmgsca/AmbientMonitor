package com.dev.thiago.ambientmonitoring.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by thiago on 22/02/16.
 */
public class Room extends RealmObject {

    @PrimaryKey
    @Required
    private Integer id;

    @Required
    private String name;

    @SerializedName("is_tracked")
    private Boolean isTracked = false;

    private RealmList<Measure> measures;

    @SerializedName("max_winter_temp")
    private Float maxWinterTemp;

    @SerializedName("min_winter_temp")
    private Float minWinterTemp;

    @SerializedName("max_summer_temp")
    private Float maxSummerTemp;

    @SerializedName("min_summer_temp")
    private Float minSummerTemp;

    @SerializedName("max_winter_humidity")
    private Float maxWinterHumidity;

    @SerializedName("min_winter_humidity")
    private Float minWinterHumidity;

    @SerializedName("max_summer_humidity")
    private Float maxSummerHumidity;

    @SerializedName("min_summer_humidity")
    private Float minSummerHumidity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RealmList<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(RealmList<Measure> measures) {
        this.measures = measures;
    }

    public Boolean getIsTracked() {
        return isTracked;
    }

    public void setIsTracked(Boolean isTracked) {
        this.isTracked = isTracked;
    }

    public Float getMaxWinterTemp() {
        return maxWinterTemp;
    }

    public void setMaxWinterTemp(Float maxWinterTemp) {
        this.maxWinterTemp = maxWinterTemp;
    }

    public Float getMinWinterTemp() {
        return minWinterTemp;
    }

    public void setMinWinterTemp(Float minWinterTemp) {
        this.minWinterTemp = minWinterTemp;
    }

    public Float getMaxSummerTemp() {
        return maxSummerTemp;
    }

    public void setMaxSummerTemp(Float maxSummerTemp) {
        this.maxSummerTemp = maxSummerTemp;
    }

    public Float getMinSummerTemp() {
        return minSummerTemp;
    }

    public void setMinSummerTemp(Float minSummerTemp) {
        this.minSummerTemp = minSummerTemp;
    }

    public Float getMaxWinterHumidity() {
        return maxWinterHumidity;
    }

    public void setMaxWinterHumidity(Float maxWinterHumidity) {
        this.maxWinterHumidity = maxWinterHumidity;
    }

    public Float getMinWinterHumidity() {
        return minWinterHumidity;
    }

    public void setMinWinterHumidity(Float minWinterHumidity) {
        this.minWinterHumidity = minWinterHumidity;
    }

    public Float getMaxSummerHumidity() {
        return maxSummerHumidity;
    }

    public void setMaxSummerHumidity(Float maxSummerHumidity) {
        this.maxSummerHumidity = maxSummerHumidity;
    }

    public Float getMinSummerHumidity() {
        return minSummerHumidity;
    }

    public void setMinSummerHumidity(Float minSummerHumidity) {
        this.minSummerHumidity = minSummerHumidity;
    }
}
