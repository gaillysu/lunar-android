package com.medcorp.lunar.network.med.model;

/**
 * Created by Jason on 2017/3/6.
 */

public class WeChatLoginObject {

    private String token;
    private WeChatLoginParams params;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public WeChatLoginParams getParams() {
        return params;
    }

    public void setParams(WeChatLoginParams params) {
        this.params = params;
    }
}
