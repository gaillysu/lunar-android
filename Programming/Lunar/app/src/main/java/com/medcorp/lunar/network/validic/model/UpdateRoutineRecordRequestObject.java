package com.medcorp.lunar.network.validic.model;

import com.google.gson.annotations.Expose;

/**
 * Created by gaillysu on 16/3/17.
 */
public class UpdateRoutineRecordRequestObject {

    @Expose
    String access_token;

    @Expose
    RoutineModel routine;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public RoutineModel getRoutine() {
        return routine;
    }

    public void setRoutine(RoutineModel routine) {
        this.routine = routine;
    }
}
