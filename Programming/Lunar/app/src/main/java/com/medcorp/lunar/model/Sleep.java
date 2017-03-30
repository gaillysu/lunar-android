package com.medcorp.lunar.model;

import io.realm.RealmObject;

/**
 * Created by gaillysu on 15/11/17.
 */
public class Sleep extends RealmObject implements Comparable<Sleep> {

    private int id = (int) Math.floor(Math.random() * Integer.MAX_VALUE);

    //IMPORTANT,HERE MUST NOT set cloudRecordID any value, pls use default value null,when we sync with cloud, it will be filled by the cloud record ID
    private String cloudRecordID;

    private String nevoUserID;

    private long createdDate;
    private long date;

    private int totalSleepTime;

    private int totalWakeTime;

    private int totalLightTime;

    private int totalDeepTime;
    //for avoid got exception, here you'd better put default values: int[24] ={0...0}
    private String hourlySleep = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    private String hourlyWake = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    private String hourlyLight = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    private String hourlyDeep = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    private long start;

    private long end;

    private int sleepQuality;

    private String remarks;

    public Sleep() {

    }

    public Sleep(long createdDate) {
        this.createdDate = createdDate;
    }

    public Sleep(long createdDate, long date, int totalSleepTime, int totalWakeTime, int totalLightTime,
                 int totalDeepTime, String hourlySleep, String hourlyWake, String hourlyLight, String hourlyDeep,
                 long start, long end, int sleepQuality, String remarks) {
        this.createdDate = createdDate;
        this.date = date;
        this.totalSleepTime = totalSleepTime;
        this.totalWakeTime = totalWakeTime;
        this.totalLightTime = totalLightTime;
        this.totalDeepTime = totalDeepTime;
        this.hourlySleep = hourlySleep;
        this.hourlyWake = hourlyWake;
        this.hourlyLight = hourlyLight;
        this.hourlyDeep = hourlyDeep;
        this.start = start;
        this.end = end;
        this.sleepQuality = sleepQuality;
        this.remarks = remarks;
    }


    public void setTotalSleepTime(int totalSleepTime) {
        this.totalSleepTime = totalSleepTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        this.totalWakeTime = totalWakeTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        this.totalLightTime = totalLightTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        this.totalDeepTime = totalDeepTime;
    }

    public void setHourlySleep(String hourlySleep) {
        this.hourlySleep = hourlySleep;
    }

    public void setHourlyWake(String hourlyWake) {
        this.hourlyWake = hourlyWake;
    }

    public void setHourlyLight(String hourlyLight) {
        this.hourlyLight = hourlyLight;
    }

    public void setHourlyDeep(String hourlyDeep) {
        this.hourlyDeep = hourlyDeep;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public long getDate() {
        return date;
    }

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public int getTotalWakeTime() {
        return totalWakeTime;
    }

    public int getTotalLightTime() {
        return totalLightTime;
    }

    public int getTotalDeepTime() {
        return totalDeepTime;
    }

    public String getHourlySleep() {
        return hourlySleep;
    }

    public String getHourlyWake() {
        return hourlyWake;
    }

    public String getHourlyLight() {
        return hourlyLight;
    }

    public String getHourlyDeep() {
        return hourlyDeep;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public void setDate(long date) {
        this.date = date;
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

    @Override
    public int compareTo(Sleep another) {
        if (getDate() < another.getDate()) {
            return -1;
        } else if (getDate() > another.getDate()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Sleep{" +
                "id=" + id +
                ", cloudRecordID='" + cloudRecordID + '\'' +
                ", nevoUserID='" + nevoUserID + '\'' +
                ", createdDate=" + createdDate +
                ", date=" + date +
                ", totalSleepTime=" + totalSleepTime +
                ", totalWakeTime=" + totalWakeTime +
                ", totalLightTime=" + totalLightTime +
                ", totalDeepTime=" + totalDeepTime +
                ", hourlySleep='" + hourlySleep + '\'' +
                ", hourlyWake='" + hourlyWake + '\'' +
                ", hourlyLight='" + hourlyLight + '\'' +
                ", hourlyDeep='" + hourlyDeep + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", sleepQuality=" + sleepQuality +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
