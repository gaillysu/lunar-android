package com.medcorp.lunar.ble.model.color;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class DeepSkyBlueLed extends NevoLed {

    public final static int COLOR = 0x00BFFF;
    private final String TAG = "DeepSkyBlue";

    @Override
    public int getHexColor() {
        return COLOR;
    }

    @Override
    public int getStringResource() {
        return R.string.notification_led_deep_sky_blue;
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
