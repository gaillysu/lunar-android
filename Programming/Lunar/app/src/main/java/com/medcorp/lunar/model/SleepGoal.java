package com.medcorp.lunar.model;

import io.realm.RealmObject;

/**
 * Created by Jason on 2017/5/27.
 */

public class SleepGoal extends RealmObject {
    private int sleepGoalId =(int) (Math.floor(Math.random() * Integer.MAX_VALUE));
    private String sleepGoalName;
    private int goalDuration;
    private boolean status;

    public SleepGoal() {
    }

    public SleepGoal(String goalName, int goalDuration, boolean status) {
        this.sleepGoalName = goalName;
        this.goalDuration = goalDuration;
        this.status = status;
    }

    public int getSleepGoalId() {
        return sleepGoalId;
    }

    public String getGoalName() {
        return sleepGoalName;
    }

    public void setGoalName(String goalName) {
        this.sleepGoalName = goalName;
    }

    public int getGoalDuration() {
        return goalDuration;
    }

    public void setGoalDuration(int goalDuration) {
        this.goalDuration = goalDuration;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SleepGoal{" +
                "goalName='" + sleepGoalName + '\'' +
                ", goalDuration=" + goalDuration +
                ", status=" + status +
                '}';
    }
}
