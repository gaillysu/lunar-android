package com.medcorp.lunar.ble.model.color;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class OrangeLed extends NevoLed {

    private final String TAG = "ORANGE";
    public final static int COLOR = 0x080000;

    @Override
    public int getHexColor() {
        return COLOR;
    }

    @Override
    public int getStringResource() {
        return R.string.notification_led_orange;
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
