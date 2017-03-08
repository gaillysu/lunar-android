package com.medcorp.lunar.event.validic;


import com.medcorp.lunar.model.Sleep;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicAddSleepRecordEvent {
    private Sleep sleep;

    public ValidicAddSleepRecordEvent(Sleep sleep) {
        this.sleep = sleep;
    }

    public Sleep getSleep() {
        return sleep;
    }

    public void setSleep(Sleep sleep) {
        this.sleep = sleep;
    }
}
