package com.medcorp.lunar.network.validic.model;

import com.google.gson.annotations.Expose;

/**
 * Created by med on 16/3/23.
 */
public class AddSleepRecordRequestObject {
    @Expose
     ValidicSleepRecord sleep;

    @Expose
    String access_token;

    public  ValidicSleepRecord getSleep() {
        return sleep;
    }

    public void setSleep( ValidicSleepRecord sleep) {
        this.sleep = sleep;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
