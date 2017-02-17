package com.medcorp.event.bluetooth;

/**
 * Created by med on 17/1/5.
 */

public class DigitalTimeChangedEvent {
    private final boolean isLocalTime;
    public DigitalTimeChangedEvent(boolean isLocalTime) {
        this.isLocalTime = isLocalTime;
    }

    public boolean isLocalTime() {
        return isLocalTime;
    }
}
