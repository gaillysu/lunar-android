package com.medcorp.model;

/**
 * Created by Karl on 10/31/15.
 */
public class HourRange {

    private int startMinutes;
    private int endMinutes;

    public HourRange(int startMinutes, int endMinutes) {
        this.startMinutes = startMinutes;
        this.endMinutes = endMinutes;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    public int getEndMinutes() {
        return endMinutes;
    }

}
