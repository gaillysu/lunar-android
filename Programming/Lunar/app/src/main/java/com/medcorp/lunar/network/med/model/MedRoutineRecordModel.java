package com.medcorp.lunar.network.med.model;

import net.medcorp.library.user.BaseResponse;

/**
 * Created by med on 16/8/23.
 */
public class MedRoutineRecordModel extends BaseResponse {
    private MedRoutineRecordWithID steps;

    public MedRoutineRecordWithID getSteps() {
        return steps;
    }

    public void setSteps(MedRoutineRecordWithID steps) {
        this.steps = steps;
    }
}
