package com.medcorp.lunar.model;

import io.realm.RealmObject;

/**
 * Created by med on 17/6/5.
 */

public class HourlyForecast extends RealmObject {
    long dt;
    float temp;
    int id;
    String main;

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

}
