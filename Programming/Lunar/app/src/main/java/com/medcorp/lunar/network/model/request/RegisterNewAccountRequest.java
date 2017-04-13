package com.medcorp.lunar.network.model.request;

import com.medcorp.lunar.network.model.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class RegisterNewAccountRequest implements BaseRequest {

    private UserBean user;

    public RegisterNewAccountRequest(String first_name, String last_name, String email, String password
            , String birthday, int length, int weight, int sex) {
        this.user = new UserBean(first_name, last_name, email, password, birthday, length, weight, sex);
    }

    public static class UserBean {

        private String first_name;
        private String last_name;
        private String email;
        private String password;
        private String birthday;
        private int length;
        private int weight;
        private int sex;

        public UserBean(String first_name, String last_name, String email
                , String password, String birthday, int length, int weight, int sex) {
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.password = password;
            this.birthday = birthday;
            this.length = length;
            this.weight = weight;
            this.sex = sex;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }
    }
}
