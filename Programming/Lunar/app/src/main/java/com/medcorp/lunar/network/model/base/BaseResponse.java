package com.medcorp.lunar.network.model.base;

/**
 * Created by Jason on 2017/2/6.
 */

public class BaseResponse {

    private double version;
    private String message;
    private int status;

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
