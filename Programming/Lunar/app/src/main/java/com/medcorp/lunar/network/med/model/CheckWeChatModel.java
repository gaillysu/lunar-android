package com.medcorp.lunar.network.med.model;


import com.medcorp.lunar.network.base.BaseResponse;

/**
 * Created by Jason on 2017/3/6.
 */

public class CheckWeChatModel extends BaseResponse {


    /**
     * user : {"first_name":"Karl-John","wechat_id":"11112312311111"}
     */

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * first_name : Karl-John
         * wechat_id : 11112312311111
         */

        private String first_name;
        private String wechat_id;

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getWechat_id() {
            return wechat_id;
        }

        public void setWechat_id(String wechat_id) {
            this.wechat_id = wechat_id;
        }
    }
}
