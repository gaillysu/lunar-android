package com.medcorp.lunar.database.dao;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by med on 16/8/30.
 */
public class SolarDAO {

    /**
     * field name and initialize value, Primary field
     */
    public static final String fID = "ID";
    @DatabaseField(generatedId = true)
    private int ID = (int) Math.floor(Math.random()*Integer.MAX_VALUE);

    /**
     * createDate is YYYY-MM-DD HH:MM:SS, means create or update date
     */
    public static final String fCreatedDate = "createdDate";
    @DatabaseField
    private Date createdDate;

    /**
     * date is YYYY-MM-DD,which day it is.
     */
    public static final String fDate = "date";
    @DatabaseField
    private Date date;

    public static final String fUserId = "userId";
    @DatabaseField
    private int userId;

    /**
     * default value is "[0,0,0,....0]", 24 length array
     */
    public static final String fHourlyHarvestingTime = "hourlyHarvestingTime";
    @DatabaseField
    private String hourlyHarvestingTime="[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    /*
    unit in "minute"
     */
    public static final String fTotalHarvestingTime = "totalHarvestingTime";
    @DatabaseField
    private int totalHarvestingTime;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
