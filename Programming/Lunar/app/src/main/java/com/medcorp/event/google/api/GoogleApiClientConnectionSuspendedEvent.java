package com.medcorp.event.google.api;

/**
 * Created by karl-john on 17/3/16.
 */
public class GoogleApiClientConnectionSuspendedEvent {
    private int state;

    public GoogleApiClientConnectionSuspendedEvent(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
