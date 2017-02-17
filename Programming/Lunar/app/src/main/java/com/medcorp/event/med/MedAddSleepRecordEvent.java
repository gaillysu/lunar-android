package com.medcorp.event.med;

import com.medcorp.model.Sleep;

/**
 * Created by karl-john on 17/5/16.
 */
public class MedAddSleepRecordEvent {
    private Sleep sleep;

    public MedAddSleepRecordEvent(Sleep sleep) {
        this.sleep = sleep;
    }

    public Sleep getSleep() {
        return sleep;
    }

    public void setSleep(Sleep sleep) {
        this.sleep = sleep;
    }
}
