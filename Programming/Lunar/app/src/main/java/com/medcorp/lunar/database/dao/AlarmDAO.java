package com.medcorp.lunar.database.dao;

import io.realm.RealmObject;

/**
 * Created by Karl on 11/25/15.
 */
public class AlarmDAO extends RealmObject{

    private int id;
    private String alarm;
    private String label;
    private byte weekDay;
    private byte alarmType;
    private byte alarmNumber;

    public byte getAlarmNumber() {
        return alarmNumber;
    }

    public void setAlarmNumber(byte alarmNumber) {
        this.alarmNumber = alarmNumber;
    }

    public byte getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(byte alarmType) {
        this.alarmType = alarmType;
    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public byte getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(byte weekDay) {
        this.weekDay = weekDay;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
