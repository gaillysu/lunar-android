package com.medcorp.lunar.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class StepsDAO {
    /**
     * field name and initialize value, Primary field
     */
    public static final String fID = "ID";
    @DatabaseField(id = true)
    private int ID = (int) Math.floor(Math.random()*Integer.MAX_VALUE);

    /**
     * this is created by saving cloud record,such as validic/med cloud
     */
    public static final String fCloudRecordID = "cloudRecordID";
    @DatabaseField
    private String cloudRecordID;

    /**
     * which user ID
     */
    public static final String fNevoUserID = "nevoUserID";
    @DatabaseField
    private String nevoUserID;


    public static final String fValidicRecordID = "validicRecordID";
    @DatabaseField
    private String validicRecordID;

    /**
     * created date
     */
    public static final String fCreatedDate = "CreatedDate";
    @DatabaseField
    private long CreatedDate;

    /**
     *  date, one day which is Year/Month/Day
     */
    public static final String fDate = "Date";
    @DatabaseField
    private long Date;


    /**
     * one day's total steps, include walk and run
     */
    public static final String fSteps = "Steps";
    @DatabaseField
    private int Steps;

    /**
     * one day's total walk steps
     */
    public static final String fWalkSteps = "WalkSteps";
    @DatabaseField
    private int WalkSteps;

    /**
     * one day's total run steps
     */
    public static final String fRunSteps = "RunSteps";
    @DatabaseField
    private int RunSteps;

    /**
     * one day's total distance ,unit is meter.
     */
    public static final String fDistance = "Distance";
    @DatabaseField
    private int Distance;


    /**
     * one day's total walk distance ,unit is meter.
     */
    public static final String fWalkDistance = "WalkDistance";
    @DatabaseField
    private int WalkDistance;

    /**
     * one day's total run distance ,unit is meter.
     */
    public static final String fRunDistance = "RunDistance";
    @DatabaseField
    private int RunDistance;

    /**
     * one day's total walk duration ,unit is minute.
     */
    public static final String fWalkDuration = "WalkDuration";
    @DatabaseField
    private int WalkDuration;

    /**
     * one day's total run duration ,unit is minute.
     */
    public static final String fRunDuration = "RunDuration";
    @DatabaseField
    private int RunDuration;


    /**
     * one day's total distance ,unit is calorie
     */
    public static final String fCalories = "Calories";
    @DatabaseField
    private int Calories;


    /**
     * one day's hourly steps, such as: int HourlySteps[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    public static final String fHourlySteps = "HourlySteps";
    @DatabaseField
    private String HourlySteps = "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]";

    /**
     * one day's hourly distance, such as: int HourlyDistance[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    public static final String fHourlyDistance = "HourlyDistance";
    @DatabaseField
    private String HourlyDistance;

    /**
     * one day's hourly calories, such as: int HourlyCalories[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    public static final String fHourlyCalories = "HourlyCalories";
    @DatabaseField
    private String HourlyCalories;


    /**
     * match Zone duration, unit is minute
     */
    public static final String fInZoneTime = "InZoneTime";
    @DatabaseField
    private int InZoneTime;

    /**
     * out of Zone duration, unit is minute
     */
    public static final String fOutZoneTime = "OutZoneTime";
    @DatabaseField
    private int OutZoneTime;

    /**
     * no activity duration, unit is minute
     */
    public static final String fNoActivityTime = "NoActivityTime";
    @DatabaseField
    private int NoActivityTime;

    /**
     * goal value
     */
    public static final String fGoal = "Goal";
    @DatabaseField
    private int Goal;

    public static final String fDistanceGoal = "distanceGoal";
    @DatabaseField
    private int distanceGoal;

    public static final String fCaloriesGoal = "caloriesGoal";
    @DatabaseField
    private int caloriesGoal;

    public static final String fActiveTimeGoal = "activeTimeGoal";
    @DatabaseField
    private int activeTimeGoal;

    public static final String fGoalReached = "goalReached";
    @DatabaseField
    private byte goalReached;

    /**
     * remarks field, save extend  infomation
     * it is a Json string
     */

    public static final String fRemarks = "Remarks";
    @DatabaseField
    private String Remarks;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNevoUserID() {
        return nevoUserID;
    }

    public void setNevoUserID(String nevoUserID) {
        this.nevoUserID = nevoUserID;
    }

    public long getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        CreatedDate = createdDate;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }

    public int getWalkSteps() {
        return WalkSteps;
    }

    public void setWalkSteps(int walkSteps) {
        WalkSteps = walkSteps;
    }

    public int getRunSteps() {
        return RunSteps;
    }

    public void setRunSteps(int runSteps) {
        RunSteps = runSteps;
    }

    public int getDistance() {
        return Distance;
    }

    public void setDistance(int distance) {
        Distance = distance;
    }

    public int getCalories() {
        return Calories;
    }

    public void setCalories(int calories) {
        Calories = calories;
    }

    public String getHourlySteps() {
        return HourlySteps;
    }

    public void setHourlySteps(String hourlySteps) {
        HourlySteps = hourlySteps;
    }

    public String getHourlyDistance() {
        return HourlyDistance;
    }

    public void setHourlyDistance(String hourlyDistance) {
        HourlyDistance = hourlyDistance;
    }

    public String getHourlyCalories() {
        return HourlyCalories;
    }

    public void setHourlyCalories(String hourlyCalories) {
        HourlyCalories = hourlyCalories;
    }

    public int getInZoneTime() {
        return InZoneTime;
    }

    public void setInZoneTime(int inZoneTime) {
        InZoneTime = inZoneTime;
    }

    public int getOutZoneTime() {
        return OutZoneTime;
    }

    public void setOutZoneTime(int outZoneTime) {
        OutZoneTime = outZoneTime;
    }

    public int getNoActivityTime() {
        return NoActivityTime;
    }

    public void setNoActivityTime(int noActivityTime) {
        NoActivityTime = noActivityTime;
    }

    public int getGoal() {
        return Goal;
    }

    public void setGoal(int goal) {
        Goal = goal;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public int getWalkDistance() {
        return WalkDistance;
    }

    public void setWalkDistance(int walkDistance) {
        WalkDistance = walkDistance;
    }

    public int getRunDistance() {
        return RunDistance;
    }

    public void setRunDistance(int runDistance) {
        RunDistance = runDistance;
    }

    public int getWalkDuration() {
        return WalkDuration;
    }

    public void setWalkDuration(int walkDuration) {
        WalkDuration = walkDuration;
    }

    public int getRunDuration() {
        return RunDuration;
    }

    public void setRunDuration(int runDuration) {
        RunDuration = runDuration;
    }

    public String getCloudRecordID() {
        return cloudRecordID;
    }

    public void setCloudRecordID(String cloudRecordID) {
        this.cloudRecordID = cloudRecordID;
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


    public String getValidicRecordID() {
        return validicRecordID;
    }

    public void setValidicRecordID(String validicRecordID) {
        this.validicRecordID = validicRecordID;
    }

}
