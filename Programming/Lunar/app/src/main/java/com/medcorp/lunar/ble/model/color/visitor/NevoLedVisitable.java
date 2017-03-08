package com.medcorp.lunar.ble.model.color.visitor;

/**
 * Created by Karl on 10/2/15.
 */
public interface NevoLedVisitable {

    public <T> T accept(NevoLedVisitor<T> visitor);

}
