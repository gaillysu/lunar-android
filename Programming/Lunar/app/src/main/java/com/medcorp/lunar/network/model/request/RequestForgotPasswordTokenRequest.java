package com.medcorp.lunar.network.model.request;

import com.medcorp.lunar.network.model.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class RequestForgotPasswordTokenRequest implements BaseRequest {

    private UserBean user;

    public RequestForgotPasswordTokenRequest(String email) {
        this.user = new UserBean(email);
    }

    public static class UserBean {
        private String email;

        public UserBean(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
