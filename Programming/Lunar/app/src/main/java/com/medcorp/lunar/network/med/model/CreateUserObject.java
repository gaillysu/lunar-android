package com.medcorp.lunar.network.med.model;



/**
 * Created by med on 16/5/3.
 */
public class CreateUserObject {
    private String token;
    private CreateUserParameters params;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CreateUserParameters getParams() {
        return params;
    }

    public void setParams(CreateUserParameters params) {
        this.params = params;
    }
}
