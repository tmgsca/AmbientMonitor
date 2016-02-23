package com.dev.thiago.ambientmonitoring.model;

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

    private Boolean isTracked = false;

    private RealmList<Measure> measures;

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
}
