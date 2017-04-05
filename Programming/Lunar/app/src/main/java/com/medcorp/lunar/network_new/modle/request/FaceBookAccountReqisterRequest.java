package com.medcorp.lunar.network_new.modle.request;

/**
 * Created by Jason on 2017/4/5.
 */

public class FaceBookAccountReqisterRequest {

    private UserBean user;

    public FaceBookAccountReqisterRequest(String first_name, String email, String facebook_id,
                                          String birthday, int length, int weight, int sex) {
        this.user = new UserBean(first_name, email, facebook_id, birthday, length, weight, sex);
    }

    private class UserBean {

        private String first_name;
        private String email;
        private String facebook_id;
        private String birthday;
        private int length;
        private int weight;
        private int sex;

        public UserBean(String first_name, String email,
                        String facebook_id, String birthday, int length, int weight, int sex) {
            this.first_name = first_name;
            this.email = email;
            this.facebook_id = facebook_id;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFacebook_id() {
            return facebook_id;
        }

        public void setFacebook_id(String facebook_id) {
            this.facebook_id = facebook_id;
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
