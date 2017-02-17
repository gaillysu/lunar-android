package com.medcorp.event;

/**
 * Created by med on 16/12/20.
 */

public class SetSunriseAndSunsetTimeRequestEvent {

    public enum STATUS {
        START,
        SUCCESS,
        FAILED
    }
    public final STATUS status;

    public SetSunriseAndSunsetTimeRequestEvent(STATUS status) {
        this.status = status;
    }

    public STATUS getStatus() {
        return status;
    }
}
