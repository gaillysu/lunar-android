package com.medcorp.lunar.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Karl on 11/25/15.
 */
public class Goal extends RealmObject {

    @PrimaryKey

    private int id;
    private String label;
    private boolean status;
    private int steps = 10000;

    public Goal() {

    }

    public Goal(int id, String label, boolean status, int steps) {
        this(label,status,steps);
        this.id = id;
    }

    public Goal(String label, boolean status, int steps) {
        this.label = label;
        this.status = status;
        this.steps = steps;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(label);
        builder.append(":");
        builder.append(steps);
        builder.append(" steps");
        return builder.toString();
    }
}