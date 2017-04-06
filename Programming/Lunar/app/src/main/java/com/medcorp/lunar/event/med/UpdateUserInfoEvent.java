package com.medcorp.lunar.event.med;

/**
 * Created by Jason on 2017/4/6.
 */

public class UpdateUserInfoEvent {
    public boolean status;

    public UpdateUserInfoEvent(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
