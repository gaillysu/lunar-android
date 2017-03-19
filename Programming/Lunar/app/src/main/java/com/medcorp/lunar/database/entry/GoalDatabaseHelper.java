package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Goal;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class GoalDatabaseHelper {

    private Realm mRealm;

    public GoalDatabaseHelper() {
       mRealm = Realm.getDefaultInstance();
    }

    public Goal add(Goal object) {
        mRealm.beginTransaction();
        Goal goal = mRealm.copyToRealm(object);
        mRealm.commitTransaction();
        return goal;
    }

    public boolean update(Goal object) {
        mRealm.beginTransaction();
        Goal goal = mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
        return goal != null;
    }

    public void remove(int presetId) {
        mRealm.beginTransaction();
        mRealm.where(Goal.class).equalTo("id", presetId).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public Goal get(int presetId) {
        mRealm.beginTransaction();
        Goal goal = mRealm.where(Goal.class).equalTo("id", presetId).findFirst();
        mRealm.commitTransaction();
        return goal;
    }

    public List<Goal> getAll() {
        mRealm.beginTransaction();
        RealmResults<Goal> allGoal = mRealm.where(Goal.class).findAll();
        mRealm.commitTransaction();
        return allGoal;
    }
}