package com.medcorp.lunar.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Steps extends RealmObject implements Comparable<Steps> {

    private int id = (int) Math.floor(Math.random() * Integer.MAX_VALUE);

    //IMPORTANT,HERE MUST NOT set cloudRecordID any value, pls use default value null,when we sync with cloud, it will be filled by the cloud record ID
    private String cloudRecordID;

    private String nevoUserID;

    private long createdDate;

    private long date;

    private int steps;

    private int walkSteps;

    private int runSteps;

    private int distance;

    private int walkDistance;

    private int runDistance;

    private int walkDuration;

    private int runDuration;

    private int calories;
    //IMPORTANT here must set it an array , length is 24, otherwise, it can't be upload to cloud server
    private String hourlySteps = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    private String hourlyDistance = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    private String hourlyCalories = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    private int inZoneTime;

    private int outZoneTime;

    private int noActivityTime;

    //keep name, means stepsGoal
    private int goal;
    private int distanceGoal;
    private int caloriesGoal;
    private int activeTimeGoal;
    private byte goalReached;

    private String remarks;

    public Steps() {

    }

    public Steps(long createdDate) {
        this.createdDate = createdDate;
    }

    public Steps(long createdDate, long date, int steps, int walkSteps, int runSteps,
                 int distance, int calories, String hourlySteps, String hourlyDistance,
                 String hourlyCalories, int inZoneTime, int outZoneTime, int noActivityTime,
                 int goal, int walkDistance, int runDistance, int walkDuration, int runDuration, String remarks) {

        this.createdDate = createdDate;
        this.date = date;
        this.steps = steps;
        this.walkSteps = walkSteps;
        this.runSteps = runSteps;
        this.distance = distance;
        this.calories = calories;
        this.hourlySteps = hourlySteps;
        this.hourlyDistance = hourlyDistance;
        this.hourlyCalories = hourlyCalories;
        this.inZoneTime = inZoneTime;
        this.outZoneTime = outZoneTime;
        this.noActivityTime = noActivityTime;
        this.goal = goal;
        this.walkDistance = walkDistance;
        this.runDistance = runDistance;
        this.walkDuration = walkDuration;
        this.runDuration = runDuration;
        this.remarks = remarks;
    }


    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setWalkSteps(int walkSteps) {
        this.walkSteps = walkSteps;
    }

    public void setRunSteps(int runSteps) {
        this.runSteps = runSteps;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setHourlySteps(String hourlySteps) {
        this.hourlySteps = hourlySteps;
    }

    public void setHourlyDistance(String hourlyDistance) {
        this.hourlyDistance = hourlyDistance;
    }

    public void setHourlyCalories(String hourlyCalories) {
        this.hourlyCalories = hourlyCalories;
    }

    public void setInZoneTime(int inZoneTime) {
        this.inZoneTime = inZoneTime;
    }

    public void setOutZoneTime(int outZoneTime) {
        this.outZoneTime = outZoneTime;
    }

    public void setNoActivityTime(int noActivityTime) {
        this.noActivityTime = noActivityTime;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public long getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }

    public int getWalkSteps() {
        return walkSteps;
    }

    public int getRunSteps() {
        return runSteps;
    }

    public int getDistance() {
        return distance;
    }

    public int getCalories() {
        return calories;
    }

    public String getHourlySteps() {
        return hourlySteps;
    }

    public String getHourlyDistance() {
        return hourlyDistance;
    }

    public String getHourlyCalories() {
        return hourlyCalories;
    }

    public int getInZoneTime() {
        return inZoneTime;
    }

    public int getOutZoneTime() {
        return outZoneTime;
    }

    public int getNoActivityTime() {
        return noActivityTime;
    }

    public int getGoal() {
        return goal;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(int walkDistance) {
        this.walkDistance = walkDistance;
    }

    public int getRunDistance() {
        return runDistance;
    }

    public void setRunDistance(int runDistance) {
        this.runDistance = runDistance;
    }

    public int getWalkDuration() {
        return walkDuration;
    }

    public void setWalkDuration(int walkDuration) {
        this.walkDuration = walkDuration;
    }

    public int getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(int runDuration) {
        this.runDuration = runDuration;
    }

    public String getCloudRecordID() {
        return cloudRecordID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCloudRecordID(String cloudRecordID) {
        this.cloudRecordID = cloudRecordID;
    }

    public String getNevoUserID() {
        return nevoUserID;
    }

    public void setNevoUserID(String nevoUserID) {
        this.nevoUserID = nevoUserID;
    }


    public int getDistanceGoal() {
        return distanceGoal;
    }

    public void setDistanceGoal(int distanceGoal) {
        this.distanceGoal = distanceGoal;
    }

    public int getCaloriesGoal() {
        return caloriesGoal;
    }

    public void setCaloriesGoal(int caloriesGoal) {
        this.caloriesGoal = caloriesGoal;
    }

    public int getActiveTimeGoal() {
        return activeTimeGoal;
    }

    public void setActiveTimeGoal(int activeTimeGoal) {
        this.activeTimeGoal = activeTimeGoal;
    }

    public byte getGoalReached() {
        return goalReached;
    }

    public void setGoalReached(byte goalReached) {
        this.goalReached = goalReached;
    }

    @Override
    public int compareTo(Steps another) {
        if (getDate() < another.getDate()) {
            return -1;
        } else if (getDate() > another.getDate()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Steps{" +
                "id=" + id +
                ", cloudRecordID='" + cloudRecordID + '\'' +
                ", nevoUserID='" + nevoUserID + '\'' +
                ", createdDate=" + createdDate +
                ", date=" + date +
                ", steps=" + steps +
                ", walkSteps=" + walkSteps +
                ", runSteps=" + runSteps +
                ", distance=" + distance +
                ", walkDistance=" + walkDistance +
                ", runDistance=" + runDistance +
                ", walkDuration=" + walkDuration +
                ", runDuration=" + runDuration +
                ", calories=" + calories +
                ", hourlySteps='" + hourlySteps + '\'' +
                ", hourlyDistance='" + hourlyDistance + '\'' +
                ", hourlyCalories='" + hourlyCalories + '\'' +
                ", inZoneTime=" + inZoneTime +
                ", outZoneTime=" + outZoneTime +
                ", noActivityTime=" + noActivityTime +
                ", goal=" + goal +
                ", distanceGoal=" + distanceGoal +
                ", caloriesGoal=" + caloriesGoal +
                ", activeTimeGoal=" + activeTimeGoal +
                ", goalReached=" + goalReached +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
