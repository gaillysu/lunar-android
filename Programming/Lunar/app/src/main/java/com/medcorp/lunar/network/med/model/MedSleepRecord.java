package com.medcorp.lunar.network.med.model;

/**
 * Created by med on 16/8/25.
 */
public class MedSleepRecord {
    private int uid;
    private String deep_sleep;
    private String light_sleep;
    private String wake_time;
    private String date;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDeep_sleep() {
        return deep_sleep;
    }

    public void setDeep_sleep(String deep_sleep) {
        this.deep_sleep = deep_sleep;
    }

    public String getLight_sleep() {
        return light_sleep;
    }

    public void setLight_sleep(String light_sleep) {
        this.light_sleep = light_sleep;
    }

    public String getWake_time() {
        return wake_time;
    }

    public void setWake_time(String wake_time) {
        this.wake_time = wake_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
