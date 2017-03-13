package com.medcorp.lunar.database.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by gaillysu on 15/11/17.
 */
public class SleepDAO extends RealmObject {
    /**
     * field name and initialize value, Primary field
     */
    @PrimaryKey
    private int ID = (int) Math.floor(Math.random() * Integer.MAX_VALUE);

    /**
     * this is created by saving validic record
     */
    private String cloudRecordID;

    /**
     * which user ID
     */
    private String nevoUserID;

    /**
     * created date
     */
    private long createdDate;

    /**
     * date, one day which is Year/Month/Day
     */
    private long date;

    /**
     * one day's total sleep time, unit is minute
     * TotalSleepTime = TotalWakeTime + TotalLightTime + TotalDeepTime
     */
    private int totalSleepTime;

    /**
     * one day's total wake time, unit is minute
     */
    private int totalWakeTime;

    /**
     * one day's total light sleep time, unit is minute
     */
    private int totalLightTime;

    /**
     * one day's total deep time, unit is minute
     */
    private int totalDeepTime;


    /**
     * one day's hourly sleep time, such as: int HourlySleep[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    private String hourlySleep;

    /**
     * one day's hourly wake time, such as: int HourlyWake[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    private String hourlyWake;

    /**
     * one day's hourly light sleep time, such as: int HourlyLight[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    private String hourlyLight;

    /**
     * one day's hourly deep sleep time, such as: int HourlyDeep[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    private String hourlyDeep;

    /**
     * Sleep start time, perhaps it is the yesterday's sometime or today's sometime
     */
    private long start;

    /**
     * sleep end time, it is today's sometime
     */
    private long end;

    /**
     * sleep quality [0..100]
     * sleep quality  =  100 *（TotalLightTime + TotalDeepTime) / TotalSleepTime
     */
    private int sleepQuality;


    /**
     * remarks field, save extend  infomation
     * it is a Json string
     */
    private String remarks;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCloudRecordID() {
        return cloudRecordID;
    }

    public void setCloudRecordID(String cloudRecordID) {
        this.cloudRecordID = cloudRecordID;
    }

    public String getNevoUserID() {
        return nevoUserID;
    }

    public void setNevoUserID(String nevoUserID) {
        this.nevoUserID = nevoUserID;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        this.totalSleepTime = totalSleepTime;
    }

    public int getTotalWakeTime() {
        return totalWakeTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        this.totalWakeTime = totalWakeTime;
    }

    public int getTotalLightTime() {
        return totalLightTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        this.totalLightTime = totalLightTime;
    }

    public int getTotalDeepTime() {
        return totalDeepTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        this.totalDeepTime = totalDeepTime;
    }

    public String getHourlySleep() {
        return hourlySleep;
    }

    public void setHourlySleep(String hourlySleep) {
        this.hourlySleep = hourlySleep;
    }

    public String getHourlyWake() {
        return hourlyWake;
    }

    public void setHourlyWake(String hourlyWake) {
        this.hourlyWake = hourlyWake;
    }

    public String getHourlyLight() {
        return hourlyLight;
    }

    public void setHourlyLight(String hourlyLight) {
        this.hourlyLight = hourlyLight;
    }

    public String getHourlyDeep() {
        return hourlyDeep;
    }

    public void setHourlyDeep(String hourlyDeep) {
        this.hourlyDeep = hourlyDeep;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
