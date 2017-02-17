package com.medcorp.event;

import com.medcorp.network.med.model.LoginUserModel;

/**
 * Created by karl-john on 17/5/16.
 */
public class LoginEvent {

    public enum status{
        SUCCESS,FAILED
    }

    private status loginStatus;
    final private LoginUserModel loginUserModel;

    public LoginEvent(status loginStatus,LoginUserModel loginUserModel) {
        this.loginStatus = loginStatus;
        this.loginUserModel = loginUserModel;
    }

    public status getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(status loginStatus) {
        this.loginStatus = loginStatus;
    }

    public LoginUserModel getLoginUserModel() {
        return loginUserModel;
    }
}
