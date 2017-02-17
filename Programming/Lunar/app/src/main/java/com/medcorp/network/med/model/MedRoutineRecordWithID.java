package com.medcorp.network.med.model;

/**
 * Created by med on 16/8/23.
 */
public class MedRoutineRecordWithID {
    private int id;
    private int uid;
    private String steps;
    private DateWithTimeZone date;
    private int calories;
    private int active_time;
    private double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public DateWithTimeZone getDate() {
        return date;
    }

    public void setDate(DateWithTimeZone date) {
        this.date = date;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getActive_time() {
        return active_time;
    }

    public void setActive_time(int active_time) {
        this.active_time = active_time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
