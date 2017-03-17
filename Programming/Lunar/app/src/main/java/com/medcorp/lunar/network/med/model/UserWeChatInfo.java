package com.medcorp.lunar.network.med.model;

/**
 * Created by Jason on 2017/3/6.
 */

public class UserWeChatInfo {

    private String first_name;
    private String wechat_id;

    public UserWeChatInfo(String first_name, String wechat_id) {
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
