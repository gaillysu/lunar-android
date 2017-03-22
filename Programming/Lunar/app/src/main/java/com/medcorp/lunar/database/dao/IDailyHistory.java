package com.medcorp.lunar.database.dao;

import com.medcorp.lunar.model.DailyHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.annotations.Ignore;

/**
 * Created by gaillysu on 15/8/11.
 * this Model save Daily record from nevo
 * @link: DailyHistory class
 */
public class IDailyHistory{

    private int trainingID = 0;
    private long created = 0;
    private int steps = 0;
    private String hourlysteps ="";
    private double distance = 0;
    private String hourlydistance ="";
    private double calories = 0;
    private String hourlycalories ="";
    private int inactivityTime = 0;
    private int totalInZoneTime = 0;
    private int totalOutZoneTime = 0;
    private int avghrm = 0;
    private int maxhrm = 0;
    private double goalreach = 0;
    private int totalSleepTime = 0;
    private String hourlySleepTime="";
    private int totalWakeTime =0;
    private String hourlyWakeTime="";
    private int totalLightTime = 0;
    private String hourlyLightTime="";
    private int totalDeepTime =0;
    private String hourlDeepTime="";

    /**
     * Start date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep start time
     * this is the night sleep start
     */
    private long startDateTime = 0;

    /**
     * End date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep end time
     * this is the night sleep end
     */
    private long endDateTime = 0;

    /**
     * Start date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep start time
     * this is the day sleep start
     * if I like to have a short sleep after lunch, It is a good idea for showing the second graph.
     */
    private long reststartDateTime = 0;

    /**
     * End date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep end time
     * this is the day sleep end
     */
    private long restendDateTime = 0;

    //this field save other values with Json string
    private String remarks = "";

    @Ignore
    private DailyHistory dailyHistory;

    //must have no-arg construct function
    public IDailyHistory(){
        dailyHistory = new DailyHistory(new Date());
    }

    public IDailyHistory(DailyHistory history)
    {
        this.dailyHistory = history;
        setCreated(history.getDate().getTime());

        //step data
        setSteps(history.getTotalSteps());
        setHourlysteps(history.getHourlySteps()==null?new String():history.getHourlySteps().toString());
        setDistance(history.getTotalDist());
        setHourlydistance(history.getHourlyDist()==null?new String():history.getHourlyDist().toString());
        setCalories(history.getTotalCalories());
        setHourlycalories(history.getHourlyCalories()==null?new String():history.getHourlyCalories().toString());

        //sleep data
        setTotalSleepTime(history.getTotalSleepTime());
        setHourlySleepTime(history.getHourlySleepTime()==null?new String():history.getHourlySleepTime().toString());
        setTotalWakeTime(history.getTotalWakeTime());
        setHourlyWakeTime(history.getHourlyWakeTime()==null?new String():history.getHourlyWakeTime().toString());
        setTotalLightTime(history.getTotalLightTime());
        setHourlyLightTime(history.getHourlyLightTime()==null?new String():history.getHourlyLightTime().toString());
        setTotalDeepTime(history.getTotalDeepTime());
        setHourlDeepTime(history.getHourlDeepTime()==null?new String():history.getHourlDeepTime().toString());

        //in /out ZONE
        setInactivityTime(history.getInactivityTime());
        setTotalInZoneTime(history.getTotalInZoneTime());
        setTotalOutZoneTime(history.getTotalOutZoneTime());

        //result: extend fields for futrue
        setAvghrm(0);
        setMaxhrm(0);
        setGoalreach(0);
        //other fields,save to remarks, Json format string
        JSONObject json = new JSONObject();
        try {
            json.put("createDate",new SimpleDateFormat("yyyy-MM-dd").format(history.getDate()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setRemarks(json.toString());
    }

    public int getTrainingID() {
        return trainingID;
    }

    public void setTrainingID(int trainingID) {
        this.trainingID = trainingID;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getHourlysteps() {
        return hourlysteps;
    }

    public void setHourlysteps(String hourlysteps) {
        this.hourlysteps = hourlysteps;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getHourlydistance() {
        return hourlydistance;
    }

    public void setHourlydistance(String hourlydistance) {
        this.hourlydistance = hourlydistance;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getHourlycalories() {
        return hourlycalories;
    }

    public void setHourlycalories(String hourlycalories) {
        this.hourlycalories = hourlycalories;
    }

    public int getInactivityTime() {
        return inactivityTime;
    }

    public void setInactivityTime(int inactivityTime) {
        this.inactivityTime = inactivityTime;
    }

    public int getTotalInZoneTime() {
        return totalInZoneTime;
    }

    public void setTotalInZoneTime(int totalInZoneTime) {
        this.totalInZoneTime = totalInZoneTime;
    }

    public int getTotalOutZoneTime() {
        return totalOutZoneTime;
    }

    public void setTotalOutZoneTime(int totalOutZoneTime) {
        this.totalOutZoneTime = totalOutZoneTime;
    }

    public int getAvghrm() {
        return avghrm;
    }

    public void setAvghrm(int avghrm) {
        this.avghrm = avghrm;
    }

    public int getMaxhrm() {
        return maxhrm;
    }

    public void setMaxhrm(int maxhrm) {
        this.maxhrm = maxhrm;
    }

    public double getGoalreach() {
        return goalreach;
    }

    public void setGoalreach(double goalreach) {
        this.goalreach = goalreach;
    }

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        this.totalSleepTime = totalSleepTime;
    }

    public String getHourlySleepTime() {
        return hourlySleepTime;
    }

    public void setHourlySleepTime(String hourlySleepTime) {
        this.hourlySleepTime = hourlySleepTime;
    }

    public int getTotalWakeTime() {
        return totalWakeTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        this.totalWakeTime = totalWakeTime;
    }

    public String getHourlyWakeTime() {
        return hourlyWakeTime;
    }

    public void setHourlyWakeTime(String hourlyWakeTime) {
        this.hourlyWakeTime = hourlyWakeTime;
    }

    public int getTotalLightTime() {
        return totalLightTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        this.totalLightTime = totalLightTime;
    }

    public String getHourlyLightTime() {
        return hourlyLightTime;
    }

    public void setHourlyLightTime(String hourlyLightTime) {
        this.hourlyLightTime = hourlyLightTime;
    }

    public int getTotalDeepTime() {
        return totalDeepTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        this.totalDeepTime = totalDeepTime;
    }

    public String getHourlDeepTime() {
        return hourlDeepTime;
    }

    public void setHourlDeepTime(String hourlDeepTime) {
        this.hourlDeepTime = hourlDeepTime;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(long endDateTime) {
        this.endDateTime = endDateTime;
    }

    public long getReststartDateTime() {
        return reststartDateTime;
    }

    public void setReststartDateTime(long reststartDateTime) {
        this.reststartDateTime = reststartDateTime;
    }

    public long getRestendDateTime() {
        return restendDateTime;
    }

    public void setRestendDateTime(long restendDateTime) {
        this.restendDateTime = restendDateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public DailyHistory getDailyHistory() {
        return dailyHistory;
    }

    public void setDailyHistory(DailyHistory dailyHistory) {
        this.dailyHistory = dailyHistory;
    }
}