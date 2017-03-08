package com.medcorp.lunar.network.med.model;

/**
 * Created by med on 16/5/3.
 *
 */
public class LoginUserObject {
    private String token;
    private LoginUserParameters params;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginUserParameters getParams() {
        return params;
    }

    public void setParams(LoginUserParameters params) {
        this.params = params;
    }
}
