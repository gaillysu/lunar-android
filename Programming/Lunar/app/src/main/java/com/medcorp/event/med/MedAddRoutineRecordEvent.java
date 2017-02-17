package com.medcorp.event.med;

import com.medcorp.model.Steps;

/**
 * Created by med on 16/8/23.
 */
public class MedAddRoutineRecordEvent {
    final private Steps steps;

    public MedAddRoutineRecordEvent(Steps steps) {
        this.steps = steps;
    }

    public Steps getSteps() {
        return steps;
    }
}
