package com.medcorp.lunar.database;

import com.medcorp.lunar.database.dao.LedLampDAO;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.Goal;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;

import io.realm.annotations.RealmModule;

/**
 * Created by Jason on 2017/3/16.
 */
@RealmModule(classes = {Alarm.class, Goal.class, LedLampDAO.class,
        Sleep.class, Solar.class, Steps.class, User.class})
public class LunarAllModules {
}
