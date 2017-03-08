package com.medcorp.lunar.network.validic.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Karl on 3/16/16.
 */
public class CreateUserRequestObjectUser {
    @Expose
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
