package com.medcorp.lunar.network.modle.response;

import com.medcorp.lunar.network.modle.base.BaseResponse;

/**
 * Created by Jason on 2017/4/5.
 */

public class CheckEmailResponse extends BaseResponse {

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {

        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
