package com.medcorp.lunar.network.validic.model;

/**
 * Created by boy on 2016/6/12.
 */
public class ChangePasswordObject {
    private String token;
    private  ChangePasswordUserParameters params;

    public  ChangePasswordUserParameters getParams() {
        return params;
    }

    public void setParams( ChangePasswordUserParameters params) {
        this.params = params;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
