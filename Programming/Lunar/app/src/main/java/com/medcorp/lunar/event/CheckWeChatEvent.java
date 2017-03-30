package com.medcorp.lunar.event;

/**
 * Created by Jason on 2017/3/6.
 */

public class CheckWeChatEvent {
    private int status;
    private String errorMsg;

    public CheckWeChatEvent(int status, String errorMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
