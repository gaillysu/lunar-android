package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Sleep;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class SleepDatabaseHelper {

    private Realm mRealm;
    private boolean isSuccess;

    public SleepDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public void add(final Sleep object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
            }
        });
    }

    public boolean update(final Sleep object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Sleep sleep = mRealm.where(Sleep.class).equalTo("createdDate", object.getCreatedDate())
                        .equalTo("start", object.getStart()).findFirst();
                sleep.setId(object.getId());
                sleep.setTotalDeepTime(object.getTotalDeepTime());
                sleep.setTotalLightTime(object.getTotalLightTime());
                sleep.setTotalSleepTime(object.getTotalSleepTime());
                sleep.setCloudRecordID(object.getCloudRecordID());
                sleep.setDate(object.getDate());
                sleep.setEnd(object.getEnd());
                sleep.setHourlyDeep(object.getHourlyDeep());
                sleep.setHourlyLight(object.getHourlyLight());
                sleep.setHourlySleep(object.getHourlySleep());
                sleep.setNevoUserID(object.getNevoUserID());
                sleep.setHourlyWake(object.getHourlyWake());
                sleep.setRemarks(object.getRemarks());
                sleep.setTotalDeepTime(object.getTotalDeepTime());
                sleep.setTotalWakeTime(object.getTotalWakeTime());
                sleep.setStart(object.getStart());
                isSuccess = true;
            }
        });
        return isSuccess;
    }

    public void remove(String userId, Date date) {
        final Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId)
                .equalTo("createdDate", date.getTime()).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                sleep.deleteFromRealm();
            }
        });
    }

    public Sleep get(String userId) {
        Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId).findFirst();
        return sleep;
    }

    public Sleep get(String userId, Date date) {
        Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId)
                .equalTo("createdDate", date.getTime()).findFirst();
        return sleep == null ? new Sleep(System.currentTimeMillis()) : sleep;
    }


    public List<Sleep> getAll(String userId) {
        RealmResults<Sleep> allSleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId).findAll();
        return allSleep;
    }

    public List<Sleep> getNeedSyncSleep(String userId) {
        List<Sleep> sleepDAOList = mRealm.where(Sleep.class).equalTo("nevoUserID", userId).findAll();
        return sleepDAOList;
    }

    public boolean isFoundInLocalSleep(int activity_id) {
        List<Sleep> sleepList = mRealm.where(Sleep.class).equalTo("id", activity_id).findAll();
        return !sleepList.isEmpty();
    }

    public boolean isFoundInLocalSleep(Date date, String userId) {
        return get(userId, date) == null;
    }
}
