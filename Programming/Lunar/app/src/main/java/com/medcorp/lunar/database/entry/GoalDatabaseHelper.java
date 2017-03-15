package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.database.dao.GoalDAO;
import com.medcorp.lunar.model.Goal;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

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
        GoalDAO goalDAO = mRealm.copyToRealm(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(goalDAO);
    }

    public boolean update(Goal object) {
        mRealm.beginTransaction();
        GoalDAO goalDAO = mRealm.copyToRealmOrUpdate(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(goalDAO) == null ? false : true;
    }

    public void remove(int presetId) {
        mRealm.where(GoalDAO.class).equalTo("ID", presetId).findFirst().deleteFromRealm();
    }

    public Goal get(int presetId) {
        return convertToNormal(mRealm.where(GoalDAO.class).equalTo("ID", presetId).findFirst());
    }

    public List<Goal> getAll() {

        return convertToNormalList(mRealm.where(GoalDAO.class).findAll());
    }

    private GoalDAO convertToDao(Goal goal) {
        GoalDAO goalDAO = new GoalDAO();
        goalDAO.setLabel(goal.getLabel());
        goalDAO.setSteps(goal.getSteps());
        goalDAO.setEnabled(goal.isStatus());
        return goalDAO;
    }

    private Goal convertToNormal(GoalDAO goalDAO) {
        Goal goal = new Goal(goalDAO.getLabel(), goalDAO.isEnabled(), goalDAO.getSteps());
        goal.setId(goalDAO.getId());
        return goal;
    }


    public List<Goal> convertToNormalList(List<GoalDAO> optionals) {
        List<Goal> goalList = new ArrayList<>();
        for (GoalDAO presetOptional : optionals) {
            if (presetOptional != null) {
                goalList.add(convertToNormal(presetOptional));
            }
        }
        return goalList;
    }
}