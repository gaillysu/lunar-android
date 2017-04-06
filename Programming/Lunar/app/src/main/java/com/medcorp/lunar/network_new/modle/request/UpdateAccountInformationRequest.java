package com.medcorp.lunar.network_new.modle.request;

import com.medcorp.lunar.network_new.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class UpdateAccountInformationRequest implements BaseRequest {

    private UserBean user;

    public UpdateAccountInformationRequest(int id, String first_name, String email,
                                           String last_name, String birthday, int length, int weight, int sex) {
        this.user = new UserBean(id, first_name, email, last_name, birthday, length, weight, sex);
    }

    private class UserBean {
        private int id;
        private String first_name;
        private String email;
        private String last_name;
        private String birthday;
        private int length;
        private int weight;
        private int sex;

        public UserBean(int id, String first_name, String email, String last_name, String birthday, int length, int weight, int sex) {
            this.id = id;
            this.first_name = first_name;
            this.email = email;
            this.last_name = last_name;
            this.birthday = birthday;
            this.length = length;
            this.weight = weight;
            this.sex = sex;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
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
