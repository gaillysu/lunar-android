package com.medcorp.lunar.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/***
 * Created by Jason on 2017/6/8.
 */

public class BedtimeModel extends RealmObject{

    @PrimaryKey
    private int id = (int) (Math.floor(Math.random() * Integer.MAX_VALUE));
    private String name;
    private int SleepGoal;
    private byte[] alarmNumber;
    private int hour;
    private int minute;
    private byte[] weekday;
    private boolean enable;

    public BedtimeModel() {
    }

    public BedtimeModel(String name, int sleepGoal, byte[] alarmNumber, int hour, int minute, byte[] weekday, boolean enable) {
        this.name = name;
        SleepGoal = sleepGoal;
        this.alarmNumber = alarmNumber;
        this.hour = hour;
        this.minute = minute;
        this.weekday = weekday;
        this.enable = enable;
    }

    public int getId() {
        return id;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSleepGoal() {
        return SleepGoal;
    }

    public void setSleepGoal(int sleepGoal) {
        SleepGoal = sleepGoal;
    }

    public byte[] getAlarmNumber() {
        return alarmNumber;
    }

    public void setAlarmNumber(byte[] alarmNumber) {
        this.alarmNumber = alarmNumber;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public byte[] getWeekday() {
        return weekday;
    }

    public void setWeekday(byte[] weekday) {
        this.weekday = weekday;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        if (hour == 0) {
            builder.append("00");
        } else if (hour < 10) {
            builder.append("0" + hour);
        } else {
            builder.append(hour);
        }
        builder.append(":");
        if (minute == 0) {
            builder.append("00");
        } else if (minute < 10) {
            builder.append("0" + minute);
        } else {
            builder.append(minute);
        }
        return builder.toString();

    }
}
