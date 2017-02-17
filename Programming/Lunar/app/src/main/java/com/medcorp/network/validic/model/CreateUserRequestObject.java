package com.medcorp.network.validic.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Karl on 3/16/16.
 */
public class CreateUserRequestObject {
    @Expose
    CreateUserRequestObjectUser user;
    @Expose
    private String pin;
    @Expose
    private String access_token;

    public CreateUserRequestObjectUser getUser() {
        return user;
    }

    public void setUser(CreateUserRequestObjectUser user) {
        this.user = user;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
