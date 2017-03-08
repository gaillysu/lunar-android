package com.medcorp.lunar.network.validic.model;

import com.google.gson.annotations.Expose;

/**
 * Created by gaillysu on 16/3/17.
 */
public class RoutineModel {
    @Expose
    int steps;

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
