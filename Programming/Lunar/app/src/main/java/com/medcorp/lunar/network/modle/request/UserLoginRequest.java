package com.medcorp.lunar.network.modle.request;


import com.medcorp.lunar.network.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/1/16.
 */

public class UserLoginRequest implements BaseRequest {

    private UserLoginBean user;

    public UserLoginRequest(String email, String password){
        user = new UserLoginBean(email, password);
    }


    private class UserLoginBean {
        private String email;
        private String password;


        public UserLoginBean(String email, String password){
            this.email = email;
            this.password = password;
        }
        public void setName(String name) {
            this.email = name;
        }

        public String getName() {
            return this.email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return this.password;
        }

        @Override
        public String toString() {
            return "UserLoginBean{" +
                    "email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
}
