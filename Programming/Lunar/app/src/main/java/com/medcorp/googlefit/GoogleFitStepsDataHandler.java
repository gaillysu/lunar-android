package com.medcorp.googlefit;

import android.content.Context;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.medcorp.model.Steps;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by karl-john on 19/2/16.
 */
public class GoogleFitStepsDataHandler {

    private List<Steps> stepsList;
    private Context context;

    public GoogleFitStepsDataHandler(List<Steps> stepsList, Context context) {
        this.stepsList = stepsList;
        this.context = context;
    }

    public DataSet getDistanceDataSet(){
        DataSet dataSet = getDataSetFromType(DataType.AGGREGATE_DISTANCE_DELTA);
        for (Steps steps : stepsList) {
            if (steps.getHourlyDistance() == null){
                continue;
            }
            JSONArray getHourlyDistance;
            try {
                getHourlyDistance = new JSONArray(steps.getHourlyDistance());
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            saveToDataSet(getHourlyDistance,new Date(steps.getDate()),Field.FIELD_DISTANCE,dataSet);
        }
        return dataSet;
    }

    public DataSet getCaloriesDataSet(){
        DataSet dataSet = getDataSetFromType(DataType.TYPE_CALORIES_EXPENDED);
        for (Steps steps : stepsList) {
            if (steps.getHourlyCalories() == null){
                continue;
            }
            JSONArray hourlyCaloriesArray;
            try {
                hourlyCaloriesArray = new JSONArray(steps.getHourlyCalories());
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            saveToDataSet(hourlyCaloriesArray,new Date(steps.getDate()),Field.FIELD_CALORIES,dataSet);
        }
        return dataSet;
    }

    public DataSet getStepsDataSet(){
        DataSet dataSet = getDataSetFromType(DataType.TYPE_STEP_COUNT_DELTA);
        for (Steps steps : stepsList) {
            if (steps.getHourlySteps() == null){
                continue;
            }
            JSONArray hourlyStepsArray;
            try {
                hourlyStepsArray = new JSONArray(steps.getHourlySteps());
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            saveToDataSet(hourlyStepsArray,new Date(steps.getDate()),Field.FIELD_STEPS,dataSet);
        }
        return dataSet;
    }

    private void saveToDataSet(JSONArray data, Date date ,Field field, DataSet dataSet){
        for (int i = 0; i<data.length(); i++){
            try {
                int dataItem =  data.getInt(i);
                if (dataItem > 0){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, i);
                    long startTime = calendar.getTimeInMillis();
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    long endTime = calendar.getTimeInMillis();
                    DataPoint dataPoint = dataSet.createDataPoint();
                    if (field == Field.FIELD_DISTANCE || field == Field.FIELD_CALORIES ) {
                        dataPoint.getValue(field).setFloat(dataItem);
                    }else{
                        dataPoint.getValue(field).setInt(dataItem);
                    }
                    dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                    dataSet.add(dataPoint);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private DataSet getDataSetFromType(DataType dataType){
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(dataType)
                .setType(DataSource.TYPE_RAW)
                .build();
        return DataSet.create(dataSource);
    }

}
