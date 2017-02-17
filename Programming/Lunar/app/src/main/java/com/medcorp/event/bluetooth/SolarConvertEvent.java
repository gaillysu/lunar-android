package com.medcorp.event.bluetooth;

/**
 * Created by med on 16/9/13.
 */
public class SolarConvertEvent {
    private final int pv_adc;

    public SolarConvertEvent(int pv_adc) {
        this.pv_adc = pv_adc;
    }

    public int getPv_adc() {
        return pv_adc;
    }
}
