package com.medcorp.event.bluetooth;

/**
 * Created by med on 16/11/17.
 */

public class SunRiseAndSunSetWithZoneOffsetChangedEvent {
    private final byte timeZoneOffset;
    private final byte sunriseHour;
    private final byte sunriseMin;
    private final byte sunsetHour;
    private final byte sunsetMin;

    public SunRiseAndSunSetWithZoneOffsetChangedEvent(byte timeZoneOffset, byte sunriseHour, byte sunriseMin, byte sunsetHour, byte sunsetMin) {
        this.timeZoneOffset = timeZoneOffset;
        this.sunriseHour = sunriseHour;
        this.sunriseMin = sunriseMin;
        this.sunsetHour = sunsetHour;
        this.sunsetMin = sunsetMin;
    }

    public byte getSunriseHour() {
        return sunriseHour;
    }

    public byte getSunriseMin() {
        return sunriseMin;
    }

    public byte getSunsetHour() {
        return sunsetHour;
    }

    public byte getSunsetMin() {
        return sunsetMin;
    }

    public byte getTimeZoneOffset() {
        return timeZoneOffset;
    }
}