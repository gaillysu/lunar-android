package com.medcorp.lunar.network.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicRoutineRecordModel {
    String code;
    String message;
    ValidicRoutineRecordModelBase routine;

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

    public ValidicRoutineRecordModelBase getRoutine() {
        return routine;
    }

    public void setRoutine(ValidicRoutineRecordModelBase routine) {
        this.routine = routine;
    }
}
