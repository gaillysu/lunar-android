package com.medcorp.lunar.ble.model.color;


import com.medcorp.lunar.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class UnknownLed extends NevoLed {

    public final static int COLOR = 0x000000;
    private final String TAG = "UNKNOWN";

    @Override
    public int getHexColor() {
        return COLOR;
    }

    @Override
    public int getStringResource() {
        return 0;
    }


    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public <T> T accept(NevoLedVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
