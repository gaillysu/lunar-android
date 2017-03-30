package com.medcorp.lunar.network.med.model;

/**
 * Created by Jason on 2017/3/6.
 */

public class WeChatLogin {
    private String wechat_id;

    public WeChatLogin(String wechat_id) {
        this.wechat_id = wechat_id;
    }

    public String getWechat_id() {

        return wechat_id;
    }

    public void setWechat_id(String wechat_id) {
        this.wechat_id = wechat_id;
    }
}
