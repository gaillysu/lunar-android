package com.medcorp.event;

/**
 * Created by Jason on 2017/2/17.
 */

public class ChangeGoalEvent {
    private boolean changeGoal;

    public ChangeGoalEvent(boolean b) {
        changeGoal = b;
    }

    public boolean isChangeGoal() {
        return changeGoal;
    }

    public void setChangeGoal(boolean changeGoal) {
        this.changeGoal = changeGoal;
    }
}
