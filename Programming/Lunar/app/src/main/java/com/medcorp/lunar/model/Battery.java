package com.medcorp.lunar.model;

/**
 * Created by gaillysu on 15/11/24.
 */
public class Battery {

    private byte batteryLevel;

    public Battery(byte level) {
        this.batteryLevel = level;
    }

    public byte getBatteryLevel() {return batteryLevel;}
}
