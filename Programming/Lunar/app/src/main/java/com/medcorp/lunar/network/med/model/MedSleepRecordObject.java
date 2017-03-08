package com.medcorp.lunar.network.med.model;

/**
 * Created by med on 16/8/25.
 */
public class MedSleepRecordObject {
    private String token;
    private  MedSleepRecordParameters params;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public  MedSleepRecordParameters getParams() {
        return params;
    }

    public void setParams( MedSleepRecordParameters params) {
        this.params = params;
    }
}
