package com.medcorp.network.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicRecordErrorModel {
    String code;
    String message;
    String errors;
    String activity_id;

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

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }
}
