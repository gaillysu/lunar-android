package com.medcorp.lunar.database.dao;

import com.j256.ormlite.field.DatabaseField;
import com.medcorp.lunar.model.DailyHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gaillysu on 15/8/11.
 * this Model save Daily record from nevo
 * @link: DailyHistory class
 */
public class IDailyHistory {

    static String TRAININGID_KEY = "trainingID";
    @DatabaseField(generatedId = true)
    private int trainingID = 0;
    /**
     * Created  time in milliseconds since January 1, 1970, 00:00:00 GMT
     * for sample: Aug 12,2015,00:00:00
     * it has a unique value, you can think it as a mainly key field
     */
    static String CREATED_KEY = "created";
    @DatabaseField
    private long created = 0;

    static String STEPS_KEY = "steps";
    @DatabaseField
    private int steps = 0;

    static String HOURLYSTEPS_KEY = "hourlysteps";
    @DatabaseField
    private String hourlysteps ="";

    static String DISTANCE_KEY = "distance";
    @DatabaseField
    private double distance = 0;

    static String HOURLYDISTANCE_KEY = "hourlydistance";
    @DatabaseField
    private String hourlydistance ="";


    static String CALORIES_KEY = "calories";
    @DatabaseField
    private double calories = 0;

    static String HOURLYCALORIES_KEY = "hourlycalories";
    @DatabaseField
    private String hourlycalories ="";

    static String InactivityTime_KEY = "InactivityTime";
    @DatabaseField
    private int InactivityTime = 0;

    static String TotalInZoneTime_KEY = "TotalInZoneTime";
    @DatabaseField
    private int TotalInZoneTime = 0;

    static String TotalOutZoneTime_KEY = "TotalOutZoneTime";
    @DatabaseField
    private int TotalOutZoneTime = 0;

    static String AVGHRM_KEY = "avghrm";
    @DatabaseField
    private int avghrm = 0;

    static String MAXHRM_KEY = "maxhrm";
    @DatabaseField
    private int maxhrm = 0;

    static String GOALREACH_KEY = "goalreach";
    @DatabaseField
    private double goalreach = 0;

    static String TotalSleepTime_KEY = "TotalSleepTime";
    @DatabaseField
    private int TotalSleepTime = 0;

    static String HourlySleepTime_KEY = "HourlySleepTime";
    @DatabaseField
    private String HourlySleepTime="";

    static String TotalWakeTime_KEY = "TotalWakeTime";
    @DatabaseField
    private int TotalWakeTime =0;

    static String HourlyWakeTime_KEY = "HourlyWakeTime";
    @DatabaseField
    private String HourlyWakeTime="";

    static String TotalLightTime_KEY = "TotalLightTime";
    @DatabaseField
    private int TotalLightTime = 0;

    static String HourlyLightTime_KEY = "HourlyLightTime";
    @DatabaseField
    private String HourlyLightTime="";

    static String TotalDeepTime_KEY = "TotalDeepTime";
    @DatabaseField
    private int TotalDeepTime =0;

    static String HourlDeepTime_KEY = "HourlDeepTime";
    @DatabaseField
    private String HourlDeepTime="";

    /**
     * Start date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep start time
     * this is the night sleep start
     */
    static String STARTDATETIME_KEY = "startDateTime";
    @DatabaseField
    private long startDateTime = 0;

    /**
     * End date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep end time
     * this is the night sleep end
     */
    static String ENDDATETIME_KEY = "endDateTime";
    @DatabaseField
    private long endDateTime = 0;

    /**
     * Start date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep start time
     * this is the day sleep start
     * if I like to have a short sleep after lunch, It is a good idea for showing the second graph.
     */
    static String RESTSTARTDATETIME_KEY = "reststartDateTime";
    @DatabaseField
    private long reststartDateTime = 0;

    /**
     * End date in milliseconds since January 1, 1970, 00:00:00 GMT, means sleep end time
     * this is the day sleep end
     */
    static String RESTENDDATETIME_KEY = "restendDateTime";
    @DatabaseField
    private long restendDateTime = 0;

    //this field save other values with Json string
    static String DESCRIPTION_KEY = "remarks";
    @DatabaseField
    private String remarks = "";

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
        return InactivityTime;
    }

    public void setInactivityTime(int inactivityTime) {
        InactivityTime = inactivityTime;
    }

    public int getTotalInZoneTime() {
        return TotalInZoneTime;
    }

    public void setTotalInZoneTime(int totalInZoneTime) {
        TotalInZoneTime = totalInZoneTime;
    }

    public int getTotalOutZoneTime() {
        return TotalOutZoneTime;
    }

    public void setTotalOutZoneTime(int totalOutZoneTime) {
        TotalOutZoneTime = totalOutZoneTime;
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
        return TotalSleepTime;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        TotalSleepTime = totalSleepTime;
    }

    public String getHourlySleepTime() {
        return HourlySleepTime;
    }

    public void setHourlySleepTime(String hourlySleepTime) {
        HourlySleepTime = hourlySleepTime;
    }

    public int getTotalWakeTime() {
        return TotalWakeTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        TotalWakeTime = totalWakeTime;
    }

    public String getHourlyWakeTime() {
        return HourlyWakeTime;
    }

    public void setHourlyWakeTime(String hourlyWakeTime) {
        HourlyWakeTime = hourlyWakeTime;
    }

    public int getTotalLightTime() {
        return TotalLightTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        TotalLightTime = totalLightTime;
    }

    public String getHourlyLightTime() {
        return HourlyLightTime;
    }

    public void setHourlyLightTime(String hourlyLightTime) {
        HourlyLightTime = hourlyLightTime;
    }

    public int getTotalDeepTime() {
        return TotalDeepTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        TotalDeepTime = totalDeepTime;
    }

    public String getHourlDeepTime() {
        return HourlDeepTime;
    }

    public void setHourlDeepTime(String hourlDeepTime) {
        HourlDeepTime = hourlDeepTime;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public DailyHistory getDailyHistory()
    {
        return dailyHistory;
    }
}