package com.medcorp.lunar.network.model.response;

import com.medcorp.lunar.network.model.base.BaseResponse;

/**
 * Created by Jason on 2017/4/5.
 */

public class UserLoginResponse extends BaseResponse {
    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * id : 51
         * last_name : Chow
         * first_name : Karl-John
         * birthday : {"date":"2000-01-01 00:00:00.000000","timezone_type":3,"timezone":"Europe/Amsterdam"}
         * weight : 73
         * length : 175
         * sex : 1
         * last_longitude : 0
         * last_latitude : 0
         * email : karl@med-corp.net
         * verified_email : false
         */

        private int id;
        private String last_name;
        private String first_name;
        private BirthdayBean birthday;
        private int weight;
        private int length;
        private int sex;
        private int last_longitude;
        private int last_latitude;
        private String email;
        private boolean verified_email;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public BirthdayBean getBirthday() {
            return birthday;
        }

        public void setBirthday(BirthdayBean birthday) {
            this.birthday = birthday;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getLast_longitude() {
            return last_longitude;
        }

        public void setLast_longitude(int last_longitude) {
            this.last_longitude = last_longitude;
        }

        public int getLast_latitude() {
            return last_latitude;
        }

        public void setLast_latitude(int last_latitude) {
            this.last_latitude = last_latitude;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isVerified_email() {
            return verified_email;
        }

        public void setVerified_email(boolean verified_email) {
            this.verified_email = verified_email;
        }

        public static class BirthdayBean {
            /**
             * date : 2000-01-01 00:00:00.000000
             * timezone_type : 3
             * timezone : Europe/Amsterdam
             */

            private String date;
            private int timezone_type;
            private String timezone;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public int getTimezone_type() {
                return timezone_type;
            }

            public void setTimezone_type(int timezone_type) {
                this.timezone_type = timezone_type;
            }

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }
        }
    }
}
