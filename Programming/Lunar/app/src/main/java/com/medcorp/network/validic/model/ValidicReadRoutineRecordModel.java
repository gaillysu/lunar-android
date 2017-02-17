package com.medcorp.network.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicReadRoutineRecordModel {
    ValidicSummary summary;
    ValidicRoutineRecordModelBase routine;

    public ValidicSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidicSummary summary) {
        this.summary = summary;
    }

    public ValidicRoutineRecordModelBase getRoutine() {
        return routine;
    }

    public void setRoutine(ValidicRoutineRecordModelBase routine) {
        this.routine = routine;
    }
}
