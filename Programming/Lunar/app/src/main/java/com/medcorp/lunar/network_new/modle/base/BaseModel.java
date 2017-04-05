package com.medcorp.lunar.network_new.modle.base;

/**
 * Created by DengGang on 2017/1/20.
 */

public class BaseModel<T extends BaseRequest>{

    private String token;
    private T params;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
}
