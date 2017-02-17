package com.medcorp.event.validic;

import com.medcorp.network.validic.model.ValidicReadMoreSleepRecordsModel;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicReadMoreSleepRecordsModelEvent {
    private ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel;

    public ValidicReadMoreSleepRecordsModelEvent(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
        this.validicReadMoreSleepRecordsModel = validicReadMoreSleepRecordsModel;
    }

    public ValidicReadMoreSleepRecordsModel getValidicReadMoreSleepRecordsModel() {
        return validicReadMoreSleepRecordsModel;
    }

    public void setValidicReadMoreSleepRecordsModel(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
        this.validicReadMoreSleepRecordsModel = validicReadMoreSleepRecordsModel;
    }
}
