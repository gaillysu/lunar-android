package com.medcorp.network.med.model;

import net.medcorp.library.user.BaseResponse;

/**
 * Created by Jason on 2016/8/9.
 *
 */
public class LoginUserModel extends BaseResponse {
    private UserWithLocation user;

    public UserWithLocation getUser() {
        return user;
    }

    public void setUser(UserWithLocation user) {
        this.user = user;
    }
}
