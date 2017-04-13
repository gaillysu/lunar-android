package com.medcorp.lunar.network.model.request;

import com.medcorp.lunar.network.model.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class ChangePasswordRequest implements BaseRequest {

    private UserBean user;

    public ChangePasswordRequest(String password_token, String email, String id, String password) {
        this.user = new UserBean(password_token, email, id, password);
    }

    private class UserBean {

        private String password_token;
        private String email;
        private String id;
        private String password;

        public UserBean(String password_token, String email, String id, String password) {
            this.password_token = password_token;
            this.email = email;
            this.id = id;
            this.password = password;
        }

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
