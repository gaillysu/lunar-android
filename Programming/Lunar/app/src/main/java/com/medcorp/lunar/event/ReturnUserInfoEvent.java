package com.medcorp.lunar.event;


import com.medcorp.lunar.network.modle.response.WeChatUserInfoResponse;

/**
 * Created by Jason on 2017/3/6.
 */

public class ReturnUserInfoEvent {

    private WeChatUserInfoResponse mUserInfo;

    public ReturnUserInfoEvent(WeChatUserInfoResponse mUserInfo) {
        this.mUserInfo = mUserInfo;
    }

    public WeChatUserInfoResponse getUserInfo() {
        return mUserInfo;
    }
}
