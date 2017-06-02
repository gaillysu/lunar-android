package com.medcorp.lunar.model;

/**
 * Created by Jason on 2017/6/2.
 */

public class ChangeSleepGoalEvent {

    private boolean change;

    public ChangeSleepGoalEvent(boolean change) {
        this.change = change;
    }

    public boolean isChange() {
        return change;
    }


}
