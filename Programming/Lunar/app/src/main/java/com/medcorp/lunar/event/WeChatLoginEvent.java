package com.medcorp.lunar.event;

/**
 * Created by Jason on 2017/3/6.
 */

public class WeChatLoginEvent {

    private int status;
    private String errorMsg;

    public WeChatLoginEvent(int status, String errorMsg) {
        this.errorMsg = errorMsg;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
