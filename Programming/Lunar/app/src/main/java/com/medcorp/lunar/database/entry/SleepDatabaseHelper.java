package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.database.LunarAllModules;
import com.medcorp.lunar.database.dao.SleepDAO;
import com.medcorp.lunar.model.Sleep;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class SleepDatabaseHelper {

    private Realm mRealm;

    public SleepDatabaseHelper() {
        RealmConfiguration lunarConfig = new RealmConfiguration.Builder()
                .name("med_lunar.realm")
                .modules(new LunarAllModules())
                .build();
        mRealm = Realm.getInstance(lunarConfig);
//        mRealm = Realm.getDefaultInstance();
    }

    public Sleep add(Sleep object) {
        mRealm.beginTransaction();
        SleepDAO sleepDAO = mRealm.copyToRealm(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(sleepDAO);
    }

    public boolean update(Sleep object) {
        mRealm.beginTransaction();
        SleepDAO sleepDAO = mRealm.copyToRealmOrUpdate(convertToDao(object));
        mRealm.commitTransaction();
        return sleepDAO == null ? false : true;
    }

    public void remove(String userId, Date date) {
        mRealm.where(SleepDAO.class).equalTo("nevoUserID", userId).equalTo("CreatedDate", date).findFirst().deleteFromRealm();
    }

    public Sleep get(String userId) {
        return convertToNormal(mRealm.where(SleepDAO.class).equalTo("nevoUserID", userId).findFirst());
    }

    public Sleep get(String userId, Date date) {
        SleepDAO sleep = mRealm.where(SleepDAO.class).equalTo("nevoUserID", userId).equalTo("CreatedDate", date).findFirst();
        return convertToNormal(sleep) == null ? new Sleep(System.currentTimeMillis()) : convertToNormal(sleep);
    }


    public List<Sleep> getAll(String userId) {
        RealmResults<SleepDAO> nevoUserID = mRealm.where(SleepDAO.class).equalTo("nevoUserID", userId).findAll();
        return convertToNormalList(nevoUserID);
    }

    public List<Sleep> getNeedSyncSleep(String userId) {
        List<SleepDAO> sleepDAOList = mRealm.where(SleepDAO.class).equalTo("nevoUserID", userId).findAll();
        return convertToNormalList(sleepDAOList);
    }

    public boolean isFoundInLocalSleep(int activity_id) {
        List<SleepDAO> sleepDAOList = mRealm.where(SleepDAO.class).equalTo("ID", activity_id).findAll();
        return !sleepDAOList.isEmpty();
    }

    public boolean isFoundInLocalSleep(Date date, String userId) {
        return get(userId, date) == null;
    }

    private SleepDAO convertToDao(Sleep sleep) {
        SleepDAO sleepDAO = new SleepDAO();
        sleepDAO.setID(sleep.getiD());
        sleepDAO.setNevoUserID(sleep.getNevoUserID());
        sleepDAO.setCreatedDate(sleep.getCreatedDate());
        sleepDAO.setDate(sleep.getDate());
        sleepDAO.setEnd(sleep.getEnd());
        sleepDAO.setHourlyDeep(sleep.getHourlyDeep());
        sleepDAO.setHourlyLight(sleep.getHourlyLight());
        sleepDAO.setHourlySleep(sleep.getHourlySleep());
        sleepDAO.setHourlyWake(sleep.getHourlyWake());
        sleepDAO.setRemarks(sleep.getRemarks());
        sleepDAO.setSleepQuality(sleep.getSleepQuality());
        sleepDAO.setStart(sleep.getStart());
        sleepDAO.setTotalDeepTime(sleep.getTotalDeepTime());
        sleepDAO.setTotalLightTime(sleep.getTotalLightTime());
        sleepDAO.setTotalSleepTime(sleep.getTotalSleepTime());
        sleepDAO.setTotalWakeTime(sleep.getTotalWakeTime());
        sleepDAO.setCloudRecordID(sleep.getCloudRecordID());
        return sleepDAO;
    }

    private Sleep convertToNormal(SleepDAO sleepDAO) {
        Sleep sleep = new Sleep(sleepDAO.getCreatedDate());
        sleep.setNevoUserID(sleepDAO.getNevoUserID());
        sleep.setiD(sleepDAO.getId());
        sleep.setDate(sleepDAO.getDate());
        sleep.setEnd(sleepDAO.getEnd());
        sleep.setHourlyDeep(sleepDAO.getHourlyDeep());
        sleep.setHourlyLight(sleepDAO.getHourlyLight());
        sleep.setHourlySleep(sleepDAO.getHourlySleep());
        sleep.setHourlyWake(sleepDAO.getHourlyWake());
        sleep.setRemarks(sleepDAO.getRemarks());
        sleep.setSleepQuality(sleepDAO.getSleepQuality());
        sleep.setStart(sleepDAO.getStart());
        sleep.setTotalDeepTime(sleepDAO.getTotalDeepTime());
        sleep.setTotalLightTime(sleepDAO.getTotalLightTime());
        sleep.setTotalSleepTime(sleepDAO.getTotalSleepTime());
        sleep.setTotalWakeTime(sleepDAO.getTotalWakeTime());
        sleep.setCloudRecordID(sleepDAO.getCloudRecordID());
        return sleep;
    }

    public List<Sleep> convertToNormalList(List<SleepDAO> optionals) {
        List<Sleep> sleepList = new ArrayList<>();
        for (SleepDAO sleepOptional : optionals) {
            if (sleepOptional != null) {
                sleepList.add(convertToNormal(sleepOptional));
            }
        }
        return sleepList;
    }
}
