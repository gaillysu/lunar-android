package com.medcorp.lunar.event.bluetooth;


import com.medcorp.lunar.model.WatchInfomation;

/**
 * Created by med on 16/11/8.
 */

public class GetWatchInfoChangedEvent {
 final WatchInfomation watchInfomation;

    public GetWatchInfoChangedEvent(WatchInfomation watchInfomation) {
        this.watchInfomation = watchInfomation;
    }

    public WatchInfomation getWatchInfomation() {
        return watchInfomation;
    }
}
