package com.medcorp.network.med.model;

import com.medcorp.network.base.BaseResponse;

/**
 * Created by med on 16/8/23.
 */
public class MedReadMoreRoutineRecordsModel extends BaseResponse{
    private MedRoutineRecordWithID[] steps;

    public MedRoutineRecordWithID[] getSteps() {
        return steps;
    }

    public void setSteps(MedRoutineRecordWithID[] steps) {
        this.steps = steps;
    }
}
