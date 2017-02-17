package com.medcorp.database.entry;

import android.content.Context;

import com.medcorp.database.DatabaseHelper;
import com.medcorp.database.dao.GoalDAO;
import com.medcorp.model.Goal;

import net.medcorp.library.ble.util.Optional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class GoalDatabaseHelper implements iSettingDatabaseHelper<Goal> {

    private DatabaseHelper databaseHelper;

    public GoalDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Optional<Goal> add(Goal object) {
        Optional<Goal> presetOptional = new Optional<>();
        try {
            GoalDAO res  = databaseHelper.getGoalDao().createIfNotExists(convertToDao(object));
            if(res != null)
            {
                    presetOptional.set(convertToNormal(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetOptional;
    }

    @Override
    public boolean update(Goal object) {
        int result = -1;
        try {
            List<GoalDAO> goalDAOList = databaseHelper.getGoalDao().queryBuilder().where().eq(GoalDAO.iDString, object.getId()).query();
            if(goalDAOList.isEmpty()) return add(object)!=null;
            GoalDAO goalDAO = convertToDao(object);
            goalDAO.setID(goalDAOList.get(0).getID());
            result = databaseHelper.getGoalDao().update(goalDAO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int presetId) {
        try {
            List<GoalDAO> goalDAOList = databaseHelper.getGoalDao().queryBuilder().where().eq(GoalDAO.iDString, presetId).query();
            if(!goalDAOList.isEmpty())
            {
              return  databaseHelper.getGoalDao().delete(goalDAOList)>=0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<Goal>> get(int presetId) {
        List<Optional<Goal>> presetList = new ArrayList<Optional<Goal>>();
        try {
            List<GoalDAO> goalDAOList = databaseHelper.getGoalDao().queryBuilder()
                    .where().eq(GoalDAO.iDString, presetId).query();

            for(GoalDAO goalDAO : goalDAOList) {
                Optional<Goal> presetOptional = new Optional<>();
                presetOptional.set(convertToNormal(goalDAO));
                presetList.add(presetOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList;
    }

    @Override
    public List<Optional<Goal>> getAll() {
        List<Optional<Goal>> presetList = new ArrayList<Optional<Goal>>();
        try {
            List<GoalDAO> goalDAOList = databaseHelper.getGoalDao().queryBuilder().query();
            for(GoalDAO goalDAO : goalDAOList) {
                Optional<Goal> presetOptional = new Optional<>();
                presetOptional.set(convertToNormal(goalDAO));
                presetList.add(presetOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList;
    }

    private GoalDAO convertToDao(Goal goal){
        GoalDAO goalDAO = new GoalDAO();
        goalDAO.setLabel(goal.getLabel());
        goalDAO.setSteps(goal.getSteps());
        goalDAO.setEnabled(goal.isStatus());
        return goalDAO;
    }

    private Goal convertToNormal(GoalDAO goalDAO){
        Goal goal =  new Goal(goalDAO.getLabel(), goalDAO.isEnabled(), goalDAO.getSteps());
        goal.setId(goalDAO.getID());
        return goal;
    }


    @Override
    public List<Goal> convertToNormalList(List<Optional<Goal>> optionals) {
        List<Goal> goalList = new ArrayList<>();
        for (Optional<Goal> presetOptional: optionals) {
            if (presetOptional.notEmpty()){
                goalList.add(presetOptional.get());
            }
        }
        return goalList;
    }
}