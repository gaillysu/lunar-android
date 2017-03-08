package com.medcorp.lunar.network.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicRoutineRecord {
    String timestamp;
    String utc_offset;
    double steps;
    double distance;
    double floors = 0;
    double elevation = 0 ;
    double calories_burned;
    String activity_id;
    RoutineGoal extras;

    public RoutineGoal getExtras() {
        return extras;
    }

    public void setExtras(RoutineGoal extras) {
        this.extras = extras;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUtc_offset() {
        return utc_offset;
    }

    public void setUtc_offset(String utc_offset) {
        this.utc_offset = utc_offset;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getFloors() {
        return floors;
    }

    public void setFloors(double floors) {
        this.floors = floors;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getCalories_burned() {
        return calories_burned;
    }

    public void setCalories_burned(double calories_burned) {
        this.calories_burned = calories_burned;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }
}
