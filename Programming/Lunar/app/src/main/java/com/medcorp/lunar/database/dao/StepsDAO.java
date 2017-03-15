package com.medcorp.lunar.database.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmModule;

/**
 * Created by gaillysu on 15/11/17.
 */
public class StepsDAO extends RealmObject {
    /**
     * field name and initialize value, Primary field
     */
    @PrimaryKey
    private int id;

    /**
     * this is created by saving cloud record,such as validic/med cloud
     */
    private String cloudRecordID;

    /**
     * which user ID
     */
    private String nevoUserID;
    private String validicRecordID;

    /**
     * created date
     */
    private long createdDate;

    /**
     * date, one day which is Year/Month/Day
     */
    private long date;


    /**
     * one day's total steps, include walk and run
     */
    private int steps;

    /**
     * one day's total walk steps
     */
    private int walkSteps;

    /**
     * one day's total run steps
     */
    private int runSteps;

    /**
     * one day's total distance ,unit is meter.
     */
    private int distance;


    /**
     * one day's total walk distance ,unit is meter.
     */
    private int walkDistance;

    /**
     * one day's total run distance ,unit is meter.
     */
    private int runDistance;

    /**
     * one day's total walk duration ,unit is minute.
     */
    private int walkDuration;

    /**
     * one day's total run duration ,unit is minute.
     */
    private int runDuration;


    /**
     * one day's total distance ,unit is calorie
     */
    private int calories;


    /**
     * one day's hourly steps, such as: int HourlySteps[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    private String hourlySteps = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    /**
     * one day's hourly distance, such as: int HourlyDistance[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    private String hourlyDistance;

    /**
     * one day's hourly calories, such as: int HourlyCalories[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    private String hourlyCalories;


    /**
     * match Zone duration, unit is minute
     */
    private int inZoneTime;

    /**
     * out of Zone duration, unit is minute
     */
    private int outZoneTime;

    /**
     * no activity duration, unit is minute
     */
    private int noActivityTime;

    /**
     * goal value
     */
    private int goal;
    private int distanceGoal;
    private int caloriesGoal;
    private int activeTimeGoal;
    private byte goalReached;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCloudRecordID() {
        return cloudRecordID;
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

    public String getValidicRecordID() {
        return validicRecordID;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getWalkSteps() {
        return walkSteps;
    }

    public void setWalkSteps(int walkSteps) {
        this.walkSteps = walkSteps;
    }

    public int getRunSteps() {
        return runSteps;
    }

    public void setRunSteps(int runSteps) {
        this.runSteps = runSteps;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getHourlySteps() {
        return hourlySteps;
    }

    public void setHourlySteps(String hourlySteps) {
        this.hourlySteps = hourlySteps;
    }

    public String getHourlyDistance() {
        return hourlyDistance;
    }

    public void setHourlyDistance(String hourlyDistance) {
        this.hourlyDistance = hourlyDistance;
    }

    public String getHourlyCalories() {
        return hourlyCalories;
    }

    public void setHourlyCalories(String hourlyCalories) {
        this.hourlyCalories = hourlyCalories;
    }

    public int getInZoneTime() {
        return inZoneTime;
    }

    public void setInZoneTime(int inZoneTime) {
        this.inZoneTime = inZoneTime;
    }

    public int getOutZoneTime() {
        return outZoneTime;
    }

    public void setOutZoneTime(int outZoneTime) {
        this.outZoneTime = outZoneTime;
    }

    public int getNoActivityTime() {
        return noActivityTime;
    }

    public void setNoActivityTime(int noActivityTime) {
        this.noActivityTime = noActivityTime;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * remarks field, save extend  infomation
     * it is a Json string
     */


    private String remarks;

    public void setValidicRecordID(String validicRecordID) {
        this.validicRecordID = validicRecordID;
    }

}
