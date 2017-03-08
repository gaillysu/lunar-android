package com.medcorp.lunar.ble.model.color.visitor;

import com.medcorp.lunar.ble.model.color.BlueLed;
import com.medcorp.lunar.ble.model.color.GreenLed;
import com.medcorp.lunar.ble.model.color.LightGreenLed;
import com.medcorp.lunar.ble.model.color.OrangeLed;
import com.medcorp.lunar.ble.model.color.RedLed;
import com.medcorp.lunar.ble.model.color.YellowLed;

/**
 * Created by Karl on 9/30/15.
 */
public interface NevoLedVisitor<T>{
    public T visit(BlueLed led);
    public T visit(GreenLed led);
    public T visit(LightGreenLed led);
    public T visit(OrangeLed led);
    public T visit(RedLed led);
    public T visit(YellowLed led);
    public T visit(NevoLedVisitable led);
}
