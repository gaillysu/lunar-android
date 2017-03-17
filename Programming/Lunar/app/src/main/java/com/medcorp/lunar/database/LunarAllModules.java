package com.medcorp.lunar.database;

import com.medcorp.lunar.database.dao.AlarmDAO;
import com.medcorp.lunar.database.dao.GoalDAO;
import com.medcorp.lunar.database.dao.IDailyHistory;
import com.medcorp.lunar.database.dao.LedLampDAO;
import com.medcorp.lunar.database.dao.SleepDAO;
import com.medcorp.lunar.database.dao.SolarDAO;
import com.medcorp.lunar.database.dao.StepsDAO;
import com.medcorp.lunar.database.dao.UserDAO;

import io.realm.annotations.RealmModule;

/**
 * Created by Jason on 2017/3/16.
 */
@RealmModule(classes = {AlarmDAO.class, GoalDAO.class, LedLampDAO.class,
        SleepDAO.class, SolarDAO.class, StepsDAO.class, UserDAO.class, IDailyHistory.class})
public class LunarAllModules {
}
