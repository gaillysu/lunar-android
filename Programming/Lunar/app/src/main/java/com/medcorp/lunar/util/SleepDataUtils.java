package com.medcorp.lunar.util;

import android.util.Log;

import com.medcorp.lunar.model.SleepData;

import org.joda.time.DateTime;
import org.json.JSONArray;

public class SleepDataUtils{
    public static SleepData mergeYesterdayToday(SleepData yesterday,SleepData today){

        DateTime todayTime = new DateTime(today.getDate());
        DateTime yesterdayTime = new DateTime(yesterday.getDate());
        DateTime todayMinusOne = todayTime.minusDays(1);
        if (todayMinusOne.getMillis() != yesterdayTime.getMillis()){
            Log.w("Karl","Hey, something went wrong here!");
            return new SleepData(0,0,0,0);
        }
        SleepData sleepData = new SleepData(today.getDeepSleep() + yesterday.getDeepSleep(),today.getLightSleep() + yesterday.getLightSleep(),today.getAwake() + yesterday.getAwake(),today.getDate());
        sleepData.setSleepStart(yesterday.getSleepStart());
        sleepData.setSleepEnd(today.getSleepEnd());
        sleepData.setHourlyWake(mergeSleepData(yesterday.getHourlyWakeInt(),today.getHourlyWakeInt()));
        sleepData.setHourlyLight(mergeSleepData(yesterday.getHourlyLightInt(),today.getHourlyLightInt()));
        sleepData.setHourlyDeep(mergeSleepData(yesterday.getHourlyDeepInt(),today.getHourlyDeepInt()));
        return sleepData;
    }

    private static String mergeSleepData (int[] yesterdaySleepArray, int[] todaySleepArray){
        JSONArray mergedSleepData = new JSONArray();
        for (int aSleepArray1 : yesterdaySleepArray) {
            mergedSleepData.put(aSleepArray1);
        }

        for (int aSleepArray2 : todaySleepArray) {
            mergedSleepData.put(aSleepArray2);
        }
        return mergedSleepData.toString();
    }
}
