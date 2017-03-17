package com.medcorp.lunar.network.med.model;


import com.medcorp.lunar.network.base.BaseResponse;

/**
 * Created by Jason on 2017/3/6.
 */

public class WeChatLoginModel extends BaseResponse {

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {

        private int id;
        private String first_name;
        private int last_longitude;
        private int last_latitude;
        private String wechat;

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

        public String getWechat() {
            return wechat;
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }
    }
}
