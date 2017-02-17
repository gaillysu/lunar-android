package com.medcorp.event.bluetooth;

import com.medcorp.model.Battery;

/**
 * Created by karl-john on 3/3/16.
 */
public class BatteryEvent {
    private final Battery battery;

    public BatteryEvent(Battery battery) {
        this.battery = battery;
    }

    public Battery getBattery() {
        return battery;
    }
}
