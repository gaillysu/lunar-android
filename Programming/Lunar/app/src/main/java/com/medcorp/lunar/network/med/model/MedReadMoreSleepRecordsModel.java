package com.medcorp.lunar.network.med.model;


import com.medcorp.lunar.network.base.BaseResponse;

/**
 * Created by med on 16/8/23.
 */
public class MedReadMoreSleepRecordsModel extends BaseResponse {
    private  MedSleepRecordWithID[] sleep;

    public  MedSleepRecordWithID[] getSleep() {
        return sleep;
    }

    public void setSleep( MedSleepRecordWithID[] sleep) {
        this.sleep = sleep;
    }
}
