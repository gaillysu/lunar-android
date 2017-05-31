package com.medcorp.lunar.model;

/**
 * Created by gaillysu on 15/11/24.
 */
public class Battery {

    private byte batteryLevel;
    private byte batteryCapacity;

    public Battery(byte level,byte batteryCapacity) {
        this.batteryLevel = level;
        this.batteryCapacity = batteryCapacity;
    }

    public byte getBatteryLevel() {return batteryLevel;}

    public byte getBatteryCapacity() {
        return batteryCapacity;
    }
}
