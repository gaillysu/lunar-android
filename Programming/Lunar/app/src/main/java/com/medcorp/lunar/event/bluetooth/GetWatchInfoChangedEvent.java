package com.medcorp.lunar.event.bluetooth;


import com.medcorp.lunar.model.WatchInformation;

/**
 * Created by med on 16/11/8.
 */

public class GetWatchInfoChangedEvent {
 final WatchInformation watchInformation;

    public GetWatchInfoChangedEvent(WatchInformation watchInformation) {
        this.watchInformation = watchInformation;
    }

    public WatchInformation getWatchInformation() {
        return watchInformation;
    }
}
