package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Steps;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class StepsDatabaseHelper {

    private Realm mRealm;

    public StepsDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public Steps add(Steps object) {
        mRealm.beginTransaction();
        Steps steps = mRealm.copyToRealm(object);
        mRealm.commitTransaction();
        return steps;
    }

    public boolean update(Steps object) {
        mRealm.beginTransaction();
        Steps steps = mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
        return steps != null;
    }

    public void remove(String userId, Date date) {
        mRealm.beginTransaction();
        mRealm.where(Steps.class).equalTo("nevoUserID", userId).equalTo("date", date.getTime()).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public List<Steps> get(String userId) {
        return getAll(userId);
    }


    public Steps get(String userId, Date date) {
        mRealm.beginTransaction();
        Steps steps = mRealm.where(Steps.class).equalTo("nevoUserID", userId).equalTo("date", date.getTime()).findFirst();
        mRealm.commitTransaction();
        return steps == null ? new Steps(System.currentTimeMillis()) : steps;
    }


    public List<Steps> getAll(String userId) {
        mRealm.beginTransaction();
        RealmResults<Steps> allSteps = mRealm.where(Steps.class).equalTo("nevoUserID", userId).findAll();
        mRealm.commitTransaction();
        return allSteps;
    }


    public List<Steps> getNeedSyncSteps(String userId) {
        return getAll(userId);
    }

    public boolean isFoundInLocalSteps(int activity_id) {
        mRealm.beginTransaction();
        RealmResults<Steps> steps = mRealm.where(Steps.class).equalTo("id", activity_id).findAll();
        mRealm.commitTransaction();
        return steps != null;
    }

    public boolean isFoundInLocalSteps(Date date, String userId) {
        return get(userId, date) != null;
    }

}
