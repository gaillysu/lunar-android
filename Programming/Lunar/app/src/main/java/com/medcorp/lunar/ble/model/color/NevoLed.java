package com.medcorp.lunar.ble.model.color;

import com.medcorp.lunar.ble.model.color.visitor.NevoLedVisitable;

/**
 * Created by Karl on 9/30/15.
 */
public abstract class NevoLed implements NevoLedVisitable {
    public abstract int getHexColor();
    public abstract int getStringResource();
    public abstract String getTag();

    @Override
    public boolean equals(Object o) {
        if (o instanceof NevoLed){
            return getHexColor() == ((NevoLed) o).getHexColor();
        }
        return super.equals(o);
    }
}
