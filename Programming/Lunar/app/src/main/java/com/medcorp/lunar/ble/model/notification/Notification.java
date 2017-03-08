package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitable;

import java.io.Serializable;

/**
 * Created by Karl on 9/30/15.
 */
public abstract  class Notification implements NotificationVisitable,Serializable {

    private boolean  state;
    public abstract String getTag();
    public abstract String getOnOffTag();
    public abstract int getStringResource();
    public abstract int getImageResource();
    public abstract NevoLed getDefaultColor();

    public Notification(boolean state) {
        this.state = state;
    }

    public boolean isOn() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < getTag().length(); i++) {
            hash = hash*31 + getTag().charAt(i);
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (hashCode() == o.hashCode()){
            return true;
        }
        return super.equals(o);
    }
}
