package com.medcorp.event.bluetooth;

/**
 * Created by karl-john on 4/3/16.
 */
public class InitializeEvent {

    public enum INITIALIZE_STATUS{
        START,
        END
    }

    private final INITIALIZE_STATUS status;

    public InitializeEvent(INITIALIZE_STATUS status) {
        this.status = status;
    }

    public INITIALIZE_STATUS getStatus() {
        return status;
    }
}
