package com.medcorp.network.med.model;

/**
 * Created by Administrator on 2016/6/8.
 */
public class RequestTokenBodyParameters {

    private RequestTokenBodyParametersUser user;

    public RequestTokenBodyParameters(){
    }

    public RequestTokenBodyParameters(String email){
        this.user = new RequestTokenBodyParametersUser(email);
    }

    public RequestTokenBodyParametersUser getUser() {
        return user;
    }

    public void setUser(RequestTokenBodyParametersUser user) {
        this.user = user;
    }
}
