package com.medcorp.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class SleepDAO {
    /**
     * field name and initialize value, Primary field
     */
    public static final String fID = "ID";
    @DatabaseField(id = true)
    private int ID = (int) Math.floor(Math.random()*Integer.MAX_VALUE);

    /**
     * this is created by saving validic record
     */
    public static final String fCloudRecordID = "cloudRecordID";
    @DatabaseField
    private String cloudRecordID;

    /**
     * which user ID
     */
    public static final String fNevoUserID = "nevoUserID";
    @DatabaseField
    private String nevoUserID;

    /**
     * created date
     */
    public static final String fCreatedDate = "CreatedDate";
    @DatabaseField
    private long CreatedDate;

    /**
     *  date, one day which is Year/Month/Day
     */
    public static final String fDate = "Date";
    @DatabaseField
    private long Date;

    /**
     * one day's total sleep time, unit is minute
     * TotalSleepTime = TotalWakeTime + TotalLightTime + TotalDeepTime
     */
    public static final String fTotalSleepTime = "TotalSleepTime";
    @DatabaseField
    private int TotalSleepTime;

    /**
     * one day's total wake time, unit is minute
     */
    public static final String fTotalWakeTime = "TotalWakeTime";
    @DatabaseField
    private int TotalWakeTime;

    /**
     * one day's total light sleep time, unit is minute
     */
    public static final String fTotalLightTime = "TotalLightTime";
    @DatabaseField
    private int TotalLightTime;

    /**
     * one day's total deep time, unit is minute
     */
    public static final String fTotalDeepTime = "TotalDeepTime";
    @DatabaseField
    private int TotalDeepTime;


    /**
     * one day's hourly sleep time, such as: int HourlySleep[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    public static final String fHourlySleep = "HourlySleep";
    @DatabaseField
    private String HourlySleep;

    /**
     * one day's hourly wake time, such as: int HourlyWake[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    public static final String fHourlyWake = "HourlyWake";
    @DatabaseField
    private String HourlyWake;

    /**
     * one day's hourly light sleep time, such as: int HourlyLight[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    public static final String fHourlyLight = "HourlyLight";
    @DatabaseField
    private String HourlyLight;

    /**
     * one day's hourly deep sleep time, such as: int HourlyDeep[n] = {60,60,...30,60}, here "n" is fixed to 24
     * array to string  is "[60,60,...30,60]" that will be saved to the table
     */
    public static final String fHourlyDeep = "HourlyDeep";
    @DatabaseField
    private String HourlyDeep;

    /**
     *  Sleep start time, perhaps it is the yesterday's sometime or today's sometime
     */
    public static final String fStart = "Start";
    @DatabaseField
    private long Start;

    /**
     *  sleep end time, it is today's sometime
     */
    public static final String fEnd = "End";
    @DatabaseField
    private long End;

    /**
     *  sleep quality [0..100]
     *  sleep quality  =  100 *（TotalLightTime + TotalDeepTime) / TotalSleepTime
     */
    public static final String fSleepQuality = "SleepQuality";
    @DatabaseField
    private int SleepQuality;


    /**
     * remarks field, save extend  infomation
     * it is a Json string
     */

    public static final String fRemarks = "Remarks";
    @DatabaseField
    private String Remarks;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNevoUserID() {
        return nevoUserID;
    }

    public void setNevoUserID(String nevoUserID) {
        this.nevoUserID = nevoUserID;
    }

    public long getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        CreatedDate = createdDate;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    public int getTotalSleepTime() {
        return TotalSleepTime;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        TotalSleepTime = totalSleepTime;
    }

    public int getTotalWakeTime() {
        return TotalWakeTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        TotalWakeTime = totalWakeTime;
    }

    public int getTotalLightTime() {
        return TotalLightTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        TotalLightTime = totalLightTime;
    }

    public int getTotalDeepTime() {
        return TotalDeepTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        TotalDeepTime = totalDeepTime;
    }

    public String getHourlySleep() {
        return HourlySleep;
    }

    public void setHourlySleep(String hourlySleep) {
        HourlySleep = hourlySleep;
    }

    public String getHourlyWake() {
        return HourlyWake;
    }

    public void setHourlyWake(String hourlyWake) {
        HourlyWake = hourlyWake;
    }

    public String getHourlyLight() {
        return HourlyLight;
    }

    public void setHourlyLight(String hourlyLight) {
        HourlyLight = hourlyLight;
    }

    public String getHourlyDeep() {
        return HourlyDeep;
    }

    public void setHourlyDeep(String hourlyDeep) {
        HourlyDeep = hourlyDeep;
    }

    public long getStart() {
        return Start;
    }

    public void setStart(long start) {
        Start = start;
    }

    public long getEnd() {
        return End;
    }

    public void setEnd(long end) {
        End = end;
    }

    public int getSleepQuality() {
        return SleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        SleepQuality = sleepQuality;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getCloudRecordID() {
        return cloudRecordID;
    }

    public void setCloudRecordID(String cloudRecordID) {
        this.cloudRecordID = cloudRecordID;
    }
}
