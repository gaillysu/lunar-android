package com.medcorp.network.validic.model;

import com.medcorp.network.med.model.RequestTokenBodyParameters;

/**
 * Created by Administrator on 2016/6/8.
 */
public class RequestTokenBody {
    private String token;
    private RequestTokenBodyParameters params;

    public RequestTokenBody(){}

    public RequestTokenBody(String token, RequestTokenBodyParameters params) {
        this.token = token;
        this.params = params;
    }

    public RequestTokenBodyParameters getParams() {
        return params;
    }

    public void setParams(RequestTokenBodyParameters params) {
        this.params = params;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
