package com.medcorp.event.google.fit;

/**
 * Created by karl-john on 17/3/16.
 */
public class GoogleFitUpdateEvent {
    private final boolean success;

    public GoogleFitUpdateEvent(boolean status) {
        this.success = status;
    }

    public boolean isSuccess() {
        return success;
    }
}
