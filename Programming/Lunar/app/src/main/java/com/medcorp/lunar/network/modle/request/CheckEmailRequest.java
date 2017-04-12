package com.medcorp.lunar.network.modle.request;

import com.medcorp.lunar.network.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class CheckEmailRequest implements BaseRequest {

    private UserBean user;

    public CheckEmailRequest(String email) {
        this.user = new UserBean(email);
    }

    private class UserBean {
        public UserBean(String email) {
            this.email = email;
        }

        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
