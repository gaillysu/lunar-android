package com.medcorp.event.bluetooth;

/**
 * Created by karl-john on 3/3/16.
 */
public class FindWatchEvent {

    private final boolean success;

    public FindWatchEvent(boolean success){
            this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
