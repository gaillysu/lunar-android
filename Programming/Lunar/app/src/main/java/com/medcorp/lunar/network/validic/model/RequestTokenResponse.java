package com.medcorp.lunar.network.validic.model;


import com.medcorp.lunar.network.base.BaseResponse;
import com.medcorp.lunar.network.med.model.UserWithPasswordToken;

/**
 * Created by Administrator on 2016/6/8.
 */
public class RequestTokenResponse extends BaseResponse
{
   private UserWithPasswordToken user;

    public UserWithPasswordToken getUser() {
        return user;
    }

    public void setUser(UserWithPasswordToken user) {
        this.user = user;
    }
}
