package com.medcorp.lunar.network.model.request;

import com.medcorp.lunar.network.model.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class WeChatLoginRequest implements BaseRequest {

    private UserBean user;

    public WeChatLoginRequest(String weChat_id) {
        this.user = new UserBean(weChat_id);
    }

    private class UserBean {
        private String wechat_id;

        public UserBean(String wechat_id) {
            this.wechat_id = wechat_id;
        }

        public String getWechat_id() {
            return wechat_id;
        }

        public void setWechat_id(String wechat_id) {
            this.wechat_id = wechat_id;
        }
    }
}
