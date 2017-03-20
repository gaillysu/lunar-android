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
    private boolean isSuccess;

    public GoalDatabaseHelper() {
       mRealm = Realm.getDefaultInstance();
    }

    public void add(final Goal object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
            }
        });
    }

    public boolean update(final Goal object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Goal goal = mRealm.where(Goal.class).equalTo("is", object.getId()).equalTo("label", object.getLabel()).findFirst();
                goal.setSteps(object.getSteps());
                goal.setId(object.getId());
                goal.setSteps(object.getSteps());
                goal.setLabel(object.getLabel());
                isSuccess = true;
            }
        });
        return isSuccess;
    }

    public void remove(int presetId) {
        final Goal goal = mRealm.where(Goal.class).equalTo("id", presetId).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                goal.deleteFromRealm();
            }
        });
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