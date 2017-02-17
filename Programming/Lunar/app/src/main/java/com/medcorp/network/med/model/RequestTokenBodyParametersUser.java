package com.medcorp.network.med.model;

/**
 * Created by Administrator on 2016/6/8.
 */
public class RequestTokenBodyParametersUser {
    private String email;

    public RequestTokenBodyParametersUser() {
    }

    public RequestTokenBodyParametersUser(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
