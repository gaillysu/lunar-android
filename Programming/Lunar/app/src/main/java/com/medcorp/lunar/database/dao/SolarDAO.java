package com.medcorp.lunar.database.dao;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by med on 16/8/30.
 */
public class SolarDAO extends RealmObject {

    /**
     * field name and initialize value, Primary field
     */
    @PrimaryKey
    private int id;

    /**
     * createDate is YYYY-MM-DD HH:MM:SS, means create or update date
     */
    private Date createdDate;

    /**
     * date is YYYY-MM-DD,which day it is.
     */
    private Date date;
    private int userId;

    /**
     * default value is "[0,0,0,....0]", 24 length array
     */
    private String hourlyHarvestingTime = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    /**
     * unit in "minute"
     */
    private int totalHarvestingTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
