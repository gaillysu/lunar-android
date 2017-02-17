package com.medcorp.event.bluetooth;

/**
 * Created by Jason on 2016/12/19.
 */

public class HomeTimeEvent {

    private final byte hour;
    private final byte minute;

    public HomeTimeEvent(byte hour, byte minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public byte getHour() {
        return hour;
    }

    public byte getMinute() {
        return minute;
    }
}
