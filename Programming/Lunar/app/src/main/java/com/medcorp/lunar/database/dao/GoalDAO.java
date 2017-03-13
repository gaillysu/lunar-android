package com.medcorp.lunar.database.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Karl on 11/25/15.
 *
 */
public class GoalDAO extends RealmObject{

   @PrimaryKey
    private int id;
    private String label;
    private boolean enabled;
    private int steps;

    public GoalDAO(){}
    public GoalDAO(String label, boolean status, int steps)
    {
        this.label = label;
        this.enabled=status;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
