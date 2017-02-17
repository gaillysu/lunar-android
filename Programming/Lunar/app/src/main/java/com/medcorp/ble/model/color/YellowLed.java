package com.medcorp.ble.model.color;

import com.medcorp.ble.model.color.visitor.NevoLedVisitor;
import com.medcorp.R;

/**
 * Created by Karl on 9/30/15.
 */
public class YellowLed extends NevoLed{

    public final static int COLOR = 0x040000;
    private final String TAG = "YELLOW";
    @Override
    public int getHexColor() {
        return COLOR;
    }

    @Override
    public int getStringResource() {
        return R.string.notification_led_yellow;
    }

    @Override
    public int getImageResource() {
        return R.drawable.yellow_dot;
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
