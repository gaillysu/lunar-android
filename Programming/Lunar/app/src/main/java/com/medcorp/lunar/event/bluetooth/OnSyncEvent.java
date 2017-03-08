package com.medcorp.lunar.event.bluetooth;

/**
 * Created by karl-john on 3/3/16.
 */
public class OnSyncEvent {

    public enum SYNC_EVENT{
        STARTED,
        STOPPED, // means big sync stopped
        TODAY_SYNC_STOPPED  //means today sync stopped
    }

    public final SYNC_EVENT status;

    public OnSyncEvent(SYNC_EVENT status) {
        this.status = status;
    }

    public SYNC_EVENT getStatus() {
        return status;
    }
}
