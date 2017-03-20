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
    private boolean isSuccess;

    public StepsDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public void add(final Steps object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
            }
        });
    }

    public boolean update(final Steps object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
                isSuccess = true;
            }
        });
        return isSuccess;
    }

    public void remove(String userId, Date date) {
        final Steps steps = mRealm.where(Steps.class).equalTo("nevoUserID", userId)
                .equalTo("date", date.getTime()).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                steps.deleteFromRealm();
            }
        });
    }

    public List<Steps> get(String userId) {
        return getAll(userId);
    }


    public Steps get(final String userId, final Date date) {
        Steps steps = mRealm.where(Steps.class).equalTo("nevoUserID", userId).equalTo("date", date.getTime()).findFirst();
        return steps == null ? new Steps(System.currentTimeMillis()) : steps;
    }


    public List<Steps> getAll(String userId) {
        RealmResults<Steps> allSteps = mRealm.where(Steps.class).equalTo("nevoUserID", userId).findAll();
        return allSteps;
    }


    public List<Steps> getNeedSyncSteps(String userId) {
        return getAll(userId);
    }

    public boolean isFoundInLocalSteps(int activity_id) {
        RealmResults<Steps> steps = mRealm.where(Steps.class).equalTo("id", activity_id).findAll();
        return steps != null;
    }

    public boolean isFoundInLocalSteps(Date date, String userId) {
        return get(userId, date) != null;
    }

}
