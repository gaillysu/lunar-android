package com.medcorp.lunar.model;

/**
 * Created by med on 16/8/4.
 */
public class WatchInformation {

    private byte watchID;

    /**
     * Model Number
     1 - Paris
     2 -  New York
     3 -  ShangHai
     ……
     */
    private byte watchModel;

    public byte getWatchID() {
        return watchID;
    }

    public void setWatchID(byte watchID) {
        this.watchID = watchID;
    }

    public byte getWatchModel() {
        return watchModel;
    }

    public void setWatchModel(byte watchModel) {
        this.watchModel = watchModel;
    }
}
