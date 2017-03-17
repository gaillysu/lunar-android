package com.medcorp.lunar.network.med.model;

/**
 * Created by Jason on 2017/3/6.
 */

public class CheckWeChatObject {
    private String token;
    private CheckWeChatParams params;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CheckWeChatParams getParams() {
        return params;
    }

    public void setParams(CheckWeChatParams params) {
        this.params = params;
    }
}
