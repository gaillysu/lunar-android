package com.medcorp.lunar.network.med.model;

/**
 * Created by med on 16/8/23.
 */
public class MedRoutineRecordObject {
    private String token;
    private MedRoutineRecordParameters params;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MedRoutineRecordParameters getParams() {
        return params;
    }

    public void setParams(MedRoutineRecordParameters params) {
        this.params = params;
    }
}
