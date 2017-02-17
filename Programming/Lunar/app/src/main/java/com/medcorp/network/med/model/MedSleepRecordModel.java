package com.medcorp.network.med.model;

import com.medcorp.network.base.BaseResponse;

/**
 * Created by med on 16/8/25.
 */
public class MedSleepRecordModel extends BaseResponse {
    private MedSleepRecordWithID sleep;

    public MedSleepRecordWithID getSleep() {
        return sleep;
    }

    public void setSleep(MedSleepRecordWithID sleep) {
        this.sleep = sleep;
    }
}
