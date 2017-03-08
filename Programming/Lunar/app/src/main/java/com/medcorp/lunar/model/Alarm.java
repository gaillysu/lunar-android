package com.medcorp.lunar.model;

/**
 * Created by gaillysu on 15/4/21.
 */
public class Alarm {

    private int id = -1;
    private int hour;
    private int minute;
    /**
     * weekDay format: bit0~bit3 is for saving the really weekday (eg: Sunday is 1,...Saturday is 7)
     * bit7 is for saving alarm on or off, 1 means on, 0 is off.
     */
    private byte weekDay;
    private String label;
    private byte alarmType;
    private byte alarmNumber;

    public Alarm(int hour, int minute,byte weekDay,String label,byte alarmStyle ,byte alarmNumber)
    {
        this.hour = hour;
        this.minute = minute;
        this.weekDay = weekDay;
        this.label = label;
        this.alarmType = alarmStyle;
        this.alarmNumber =alarmNumber;
    }

    public byte getAlarmNumber() {
        return alarmNumber;
    }

    public void setAlarmNumber(byte alarmNumber) {
        this.alarmNumber = alarmNumber;
    }

    public byte getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(byte alarmStyle) {
        this.alarmType = alarmStyle;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public byte getWeekDay() {
        return weekDay;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setWeekDay(byte weekDay) {
        this.weekDay = weekDay;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        if (hour == 0){
            builder.append("00");
        } else if (hour < 10){
            builder.append("0" + hour);
        }else{
            builder.append(hour);
        }
        builder.append(":");
        if (minute== 0){
            builder.append("00");
        } else if (minute< 10){
            builder.append("0" + minute);
        }else{
            builder.append(minute);
        }
        return builder.toString();

    }
}
