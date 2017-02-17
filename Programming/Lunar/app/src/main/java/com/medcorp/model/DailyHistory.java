package com.medcorp.model;

        import java.util.Date;
        import java.util.List;

/**
 * Created by Hugo on 8/4/15.
 */
public class DailyHistory {

    private Date mDate;
    private List<Integer> mHourlySteps;
    private int mTotalSteps;

    //add new from v1.2.2, FW v18/v31, sleep tracker
    //unit:cm->meter
    int TotalDist;
    List<Integer> HourlyDist;
    //unit: cal->kcal
    int TotalCalories;
    List<Integer> HourlyCalories;

    int InactivityTime;
    int TotalInZoneTime;
    int TotalOutZoneTime;
    //unit: minute
    int TotalSleepTime;
    List<Integer> HourlySleepTime;
    int TotalWakeTime;
    List<Integer> HourlyWakeTime;
    int TotalLightTime;
    List<Integer> HourlyLightTime;
    int TotalDeepTime;
    List<Integer> HourlDeepTime;
    //end added

    public DailyHistory(Date date) {
        mDate = date;
    }



    public int getTotalSteps() {
        return mTotalSteps;
    }
    public void setTotalSteps(int TotalSteps) {
        mTotalSteps = TotalSteps;
    }

    public List<Integer> getHourlySteps() {
        return mHourlySteps;
    }
    public void setHourlySteps(List<Integer> HourlySteps) {
        mHourlySteps = HourlySteps;
    }

    public int getTotalDist() {
        return TotalDist;
    }

    public void setTotalDist(int totalDist) {
        TotalDist = totalDist;
    }

    public List<Integer> getHourlyDist() {
        return HourlyDist;
    }

    public void setHourlyDist(List<Integer> hourlyDist) {
        HourlyDist = hourlyDist;
    }

    public int getTotalCalories() {
        return TotalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        TotalCalories = totalCalories;
    }

    public List<Integer> getHourlyCalories() {
        return HourlyCalories;
    }

    public void setHourlyCalories(List<Integer> hourlyCalories) {
        HourlyCalories = hourlyCalories;
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

    public int getTotalSleepTime() {
        return TotalSleepTime;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        TotalSleepTime = totalSleepTime;
    }

    public List<Integer> getHourlySleepTime() {
        return HourlySleepTime;
    }

    public void setHourlySleepTime(List<Integer> hourlySleepTime) {
        HourlySleepTime = hourlySleepTime;
    }

    public int getTotalWakeTime() {
        return TotalWakeTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        TotalWakeTime = totalWakeTime;
    }

    public List<Integer> getHourlyWakeTime() {
        return HourlyWakeTime;
    }

    public void setHourlyWakeTime(List<Integer> hourlyWakeTime) {
        HourlyWakeTime = hourlyWakeTime;
    }

    public int getTotalLightTime() {
        return TotalLightTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        TotalLightTime = totalLightTime;
    }

    public List<Integer> getHourlyLightTime() {
        return HourlyLightTime;
    }

    public void setHourlyLightTime(List<Integer> hourlyLightTime) {
        HourlyLightTime = hourlyLightTime;
    }

    public int getTotalDeepTime() {
        return TotalDeepTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        TotalDeepTime = totalDeepTime;
    }

    public List<Integer> getHourlDeepTime() {
        return HourlDeepTime;
    }

    public void setHourlDeepTime(List<Integer> hourlDeepTime) {
        HourlDeepTime = hourlDeepTime;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }
}