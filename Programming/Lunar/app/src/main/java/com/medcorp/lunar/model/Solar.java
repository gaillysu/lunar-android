package com.medcorp.lunar.model;


import com.medcorp.lunar.util.Common;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by karl-john on 25/8/2016.
 */

public class Solar extends RealmObject {


    private int id = (int) (Math.floor(Math.random() * Integer.MAX_VALUE));

    /**
     * createdDate is the created/updated date, format is YYYY-MM-DD HH:MM:SS
     */
    private Date createdDate;

    /**
     * date is the daily date,format is YYYY-MM-DD
     */
    private long date;

    private int userId;
    //"[0,0,0,....0]", 24 length array, unit is in minutes
    private String hourlyHarvestingTime;
    //unit is in minutes
    private int totalHarvestingTime;

    //just realm use this method
    public Solar() {

    }

    public Solar(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Solar(Date createdDate, long date, int userId, String hourlyHarvestingTime, int totalHarvestingTime) {
        this.createdDate = createdDate;
        this.date = date;
        this.userId = userId;
        this.hourlyHarvestingTime = hourlyHarvestingTime;
        this.totalHarvestingTime = totalHarvestingTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHourlyHarvestingTime() {
        return hourlyHarvestingTime;
    }

    public void setHourlyHarvestingTime(String hourlyHarvestingTime) {
        this.hourlyHarvestingTime = hourlyHarvestingTime;
    }

    public int getTotalHarvestingTime() {
        return totalHarvestingTime;
    }

    public void setTotalHarvestingTime(int totalHarvestingTime) {
        this.totalHarvestingTime = totalHarvestingTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int[] getHourlyHarvestingTimeInt() {
        return Common.convertJSONArrayIntToArray(getHourlyHarvestingTime());
    }

    @Override
    public String toString() {
        return "Solar{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", date=" + date +
                ", userId=" + userId +
                ", hourlyHarvestingTime='" + hourlyHarvestingTime + '\'' +
                ", totalHarvestingTime=" + totalHarvestingTime +
                '}';
    }
}
