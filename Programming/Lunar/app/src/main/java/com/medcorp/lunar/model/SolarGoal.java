package com.medcorp.lunar.model;

import io.realm.RealmObject;

/**
 * Created by Jason on 2017/5/27.
 */

public class SolarGoal extends RealmObject{

    private int solarGoalId =(int) (Math.floor(Math.random() * Integer.MAX_VALUE));
    private String name;
    private int time;
    private boolean status;

    public SolarGoal() {
    }

    public SolarGoal(String name, int time, boolean status) {
        this.name = name;
        this.time = time;
        this.status = status;
    }

    public int getSolarGoalId() {
        return solarGoalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SolarGoal{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", status=" + status +
                '}';
    }
}
