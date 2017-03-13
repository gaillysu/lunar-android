package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.database.dao.StepsDAO;
import com.medcorp.lunar.model.Steps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class StepsDatabaseHelper {

    private Realm mRealm;

    public StepsDatabaseHelper(Context context) {
        mRealm = Realm.getDefaultInstance();
    }

    public Steps add(Steps object) {
        mRealm.beginTransaction();
        StepsDAO stepsDAO = mRealm.copyToRealm(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(stepsDAO);
    }

    public boolean update(Steps object) {
        mRealm.beginTransaction();
        StepsDAO stepsDAO = mRealm.copyToRealmOrUpdate(convertToDao(object));
        mRealm.commitTransaction();
        return stepsDAO == null ? false : true;
    }

    public void remove(String userId, Date date) {
        mRealm.beginTransaction();
        mRealm.where(StepsDAO.class).equalTo("nevoUserID", userId).equalTo("date", date).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public List<Steps> get(String userId) {
        return getAll(userId);
    }


    public Steps get(String userId, Date date) {
        mRealm.beginTransaction();
        StepsDAO steps = mRealm.where(StepsDAO.class).equalTo("nevoUserID", userId).equalTo("date", date).findFirst();
        mRealm.commitTransaction();
        return steps == null ? new Steps(System.currentTimeMillis()) : convertToNormal(steps);
    }


    public List<Steps> getAll(String userId) {
        mRealm.beginTransaction();
        RealmResults<StepsDAO> allSteps = mRealm.where(StepsDAO.class).equalTo("nevoUserID", userId).findAll();
        mRealm.commitTransaction();
        return convertToNormalList(allSteps);
    }


    public List<Steps> getNeedSyncSteps(String userId) {
        return getAll(userId);
    }

    public boolean isFoundInLocalSteps(int activity_id) {
        RealmResults<StepsDAO> steps = mRealm.where(StepsDAO.class).equalTo("ID", activity_id).findAll();
        return steps == null ? false : true;
    }

    public boolean isFoundInLocalSteps(Date date, String userId) {
        return get(userId, date) == null ? false : true;
    }

    private StepsDAO convertToDao(Steps steps) {
        StepsDAO stepsDao = new StepsDAO();
        stepsDao.setID(steps.getiD());
        stepsDao.setNevoUserID(steps.getNevoUserID());
        stepsDao.setCreatedDate(steps.getCreatedDate());
        stepsDao.setDate(steps.getDate());
        stepsDao.setSteps(steps.getSteps());
        stepsDao.setWalkSteps(steps.getWalkSteps());
        stepsDao.setRunSteps(steps.getRunSteps());
        stepsDao.setDistance(steps.getDistance());
        stepsDao.setWalkDistance(steps.getWalkDistance());
        stepsDao.setRunDistance(steps.getRunDistance());
        stepsDao.setWalkDuration(steps.getWalkDuration());
        stepsDao.setRunDuration(steps.getRunDuration());
        stepsDao.setCalories(steps.getCalories());
        stepsDao.setHourlySteps(steps.getHourlySteps());
        stepsDao.setHourlyDistance(steps.getHourlyDistance());
        stepsDao.setHourlyCalories(steps.getHourlyCalories());
        stepsDao.setInZoneTime(steps.getInZoneTime());
        stepsDao.setOutZoneTime(steps.getOutZoneTime());
        stepsDao.setNoActivityTime(steps.getNoActivityTime());
        stepsDao.setGoal(steps.getGoal());
        stepsDao.setRemarks(steps.getRemarks());
        stepsDao.setCloudRecordID(steps.getCloudRecordID());
        stepsDao.setDistanceGoal(steps.getDistanceGoal());
        stepsDao.setCaloriesGoal(steps.getCaloriesGoal());
        stepsDao.setActiveTimeGoal(steps.getActiveTimeGoal());
        stepsDao.setGoalReached(steps.getGoalReached());
        return stepsDao;
    }

    private Steps convertToNormal(StepsDAO stepsDAO) {
        Steps steps = new Steps(stepsDAO.getCreatedDate());
        steps.setNevoUserID(stepsDAO.getNevoUserID());
        steps.setiD(stepsDAO.getID());
        steps.setDate(stepsDAO.getDate());
        steps.setSteps(stepsDAO.getSteps());
        steps.setWalkSteps(stepsDAO.getWalkSteps());
        steps.setRunSteps(stepsDAO.getRunSteps());
        steps.setDistance(stepsDAO.getDistance());
        steps.setWalkDistance(stepsDAO.getWalkDistance());
        steps.setRunDistance(stepsDAO.getRunDistance());
        steps.setWalkDuration(stepsDAO.getWalkDuration());
        steps.setRunDuration(stepsDAO.getRunDuration());
        steps.setCalories(stepsDAO.getCalories());
        steps.setHourlySteps(stepsDAO.getHourlySteps());
        steps.setHourlyDistance(stepsDAO.getHourlyDistance());
        steps.setHourlyCalories(stepsDAO.getHourlyCalories());
        steps.setInZoneTime(stepsDAO.getInZoneTime());
        steps.setOutZoneTime(stepsDAO.getOutZoneTime());
        steps.setNoActivityTime(stepsDAO.getNoActivityTime());
        steps.setGoal(stepsDAO.getGoal());
        steps.setRemarks(stepsDAO.getRemarks());
        steps.setCloudRecordID(stepsDAO.getCloudRecordID());
        steps.setDistanceGoal(stepsDAO.getDistanceGoal());
        steps.setCaloriesGoal(stepsDAO.getCaloriesGoal());
        steps.setActiveTimeGoal(stepsDAO.getActiveTimeGoal());
        steps.setGoalReached(stepsDAO.getGoalReached());
        return steps;
    }

    public List<Steps> convertToNormalList(List<StepsDAO> optionals) {
        List<Steps> stepsList = new ArrayList<>();
        for (StepsDAO stepsOptional : optionals) {
            if (stepsOptional != null) {
                stepsList.add(convertToNormal(stepsOptional));
            }
        }
        return stepsList;
    }

}
