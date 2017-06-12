package com.medcorp.lunar.database;

import com.medcorp.lunar.database.dao.LedLampDAO;
import com.medcorp.lunar.model.Alarm;
<<<<<<< HEAD
import com.medcorp.lunar.model.BedtimeModel;
=======
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.model.CityWeather;
import com.medcorp.lunar.model.HourlyForecast;
>>>>>>> develop
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.model.User;

import io.realm.annotations.RealmModule;

/**
 * Created by Jason on 2017/3/16.
 */
<<<<<<< HEAD
@RealmModule(classes = {Alarm.class, StepsGoal.class, LedLampDAO.class,
        Sleep.class, Solar.class, Steps.class, User.class, SolarGoal.class, SleepGoal.class, BedtimeModel.class})
=======
@RealmModule(classes = {Alarm.class, StepsGoal.class, LedLampDAO.class, CityWeather.class, HourlyForecast.class,
        Sleep.class, Solar.class, Steps.class, User.class, SolarGoal.class, SleepGoal.class})
>>>>>>> develop
public class LunarAllModules {
}
