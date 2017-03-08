package com.medcorp.lunar.network.med.model;


import net.medcorp.library.user.BaseResponse;

/**
 * Created by med on 16/5/3.
 */
public class CreateUserModel extends BaseResponse {
    private UserWithID user;

    public UserWithID getUser() {
        return user;
    }

    public void setUser(UserWithID user) {
        this.user = user;
    }
}
