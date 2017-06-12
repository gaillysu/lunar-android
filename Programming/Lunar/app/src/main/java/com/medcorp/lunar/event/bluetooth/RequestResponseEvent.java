package com.medcorp.lunar.event.bluetooth;

/***
 * Created by karl-john on 3/3/16.
 */
public class RequestResponseEvent {

    private final boolean success;

    public RequestResponseEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
