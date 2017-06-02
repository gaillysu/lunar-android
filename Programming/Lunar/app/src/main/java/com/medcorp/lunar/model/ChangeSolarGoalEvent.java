package com.medcorp.lunar.model;

/**
 * Created by Jason on 2017/6/2.
 */

public class ChangeSolarGoalEvent {

    private boolean change;

    public ChangeSolarGoalEvent(boolean change) {
        this.change = change;
    }


    public boolean isChange() {
        return change;
    }
}
