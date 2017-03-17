package com.medcorp.lunar.event;

/**
 * Created by Jason on 2017/3/6.
 */

public class WeChatTokenEvent {

    private String code;

    public WeChatTokenEvent(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
