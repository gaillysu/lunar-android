package com.medcorp.lunar.event;

/**
 * Created by Jason on 2017/3/6.
 */

public class CreateWeChatEvent {
    private int status;
    private String msg;

    public CreateWeChatEvent(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
