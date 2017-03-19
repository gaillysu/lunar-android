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

    public SleepDatabaseHelper() {
       mRealm = Realm.getDefaultInstance();
    }

    public Sleep add(Sleep object) {
        mRealm.beginTransaction();
        Sleep sleep = mRealm.copyToRealm(object);
        mRealm.commitTransaction();
        return sleep;
    }

    public boolean update(Sleep object) {
        mRealm.beginTransaction();
        Sleep sleep = mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
        return sleep != null;
    }

    public void remove(String userId, Date date) {
        mRealm.beginTransaction();
        mRealm.where(Sleep.class).equalTo("nevoUserID", userId)
                .equalTo("createdDate", date.getTime()).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public Sleep get(String userId) {
        mRealm.beginTransaction();
        Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId).findFirst();
        mRealm.commitTransaction();
        return sleep;
    }

    public Sleep get(String userId, Date date) {
        mRealm.beginTransaction();
        Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId)
                .equalTo("createdDate", date.getTime()).findFirst();
        mRealm.commitTransaction();
        return sleep == null ? new Sleep(System.currentTimeMillis()) : sleep;
    }


    public List<Sleep> getAll(String userId) {
        mRealm.beginTransaction();
        RealmResults<Sleep> allSleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId).findAll();
        mRealm.commitTransaction();
        return allSleep;
    }

    public List<Sleep> getNeedSyncSleep(String userId) {
        mRealm.beginTransaction();
        List<Sleep> sleepDAOList = mRealm.where(Sleep.class).equalTo("nevoUserID", userId).findAll();
        mRealm.commitTransaction();
        return sleepDAOList;
    }

    public boolean isFoundInLocalSleep(int activity_id) {
        mRealm.beginTransaction();
        List<Sleep> sleepList = mRealm.where(Sleep.class).equalTo("id", activity_id).findAll();
        mRealm.commitTransaction();
        return !sleepList.isEmpty();
    }

    public boolean isFoundInLocalSleep(Date date, String userId) {
        return get(userId, date) == null;
    }
}
