package com.medcorp.lunar.network.modle.response;

import com.medcorp.lunar.network.modle.base.BaseResponse;

/**
 * Created by Jason on 2017/4/5.
 */

public class RequestForgotPasswordResponse extends BaseResponse {

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {

        private String password_token;
        private String email;
        private int id;

        public String getPassword_token() {
            return password_token;
        }

        public void setPassword_token(String password_token) {
            this.password_token = password_token;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
