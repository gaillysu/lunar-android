package com.medcorp.lunar.event;


import com.medcorp.lunar.network.model.response.UserLoginResponse;

/**
 * Created by karl-john on 17/5/16.
 */
public class LoginEvent {

    public enum status{
        SUCCESS,FAILED
    }

    private status loginStatus;
    final private UserLoginResponse loginUserModel;

    public LoginEvent(status loginStatus,UserLoginResponse loginUserModel) {
        this.loginStatus = loginStatus;
        this.loginUserModel = loginUserModel;
    }

    public status getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(status loginStatus) {
        this.loginStatus = loginStatus;
    }

    public UserLoginResponse getLoginUserModel() {
        return loginUserModel;
    }
}
