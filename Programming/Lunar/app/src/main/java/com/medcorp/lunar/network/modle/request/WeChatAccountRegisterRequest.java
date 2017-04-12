package com.medcorp.lunar.network.modle.request;

import com.medcorp.lunar.network.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class WeChatAccountRegisterRequest implements BaseRequest {
    private UserBean user;

    public WeChatAccountRegisterRequest(String first_name, String wechat_id) {
        this.user = new UserBean(first_name, wechat_id);
    }

    private class UserBean {
        private String first_name;
        private String wechat_id;

        public UserBean(String first_name, String wechat_id) {
            this.first_name = first_name;
            this.wechat_id = wechat_id;
        }

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
