package com.medcorp.network.validic.model;

/**
 * Created by med on 16/3/23.
 */
public class ValidicSleepRecordModel {
    String code;
    String message;
    ValidicSleepRecordModelBase sleep;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ValidicSleepRecordModelBase getSleep() {
        return sleep;
    }

    public void setSleep(ValidicSleepRecordModelBase sleep) {
        this.sleep = sleep;
    }
}
