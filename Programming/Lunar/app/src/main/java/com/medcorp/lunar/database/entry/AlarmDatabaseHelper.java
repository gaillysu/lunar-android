package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.database.DatabaseHelper;
import com.medcorp.lunar.database.dao.AlarmDAO;
import com.medcorp.lunar.model.Alarm;

import net.medcorp.library.ble.util.Optional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class AlarmDatabaseHelper implements iSettingDatabaseHelper<Alarm> {

    private DatabaseHelper databaseHelper;

    public AlarmDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Optional<Alarm> add(Alarm object) {
        Optional<Alarm> alarm = new Optional<>();
        try {
            AlarmDAO res = databaseHelper.getAlarmDao().createIfNotExists(convertToDao(object));
            if(res != null)
            {
                alarm.set(convertToNormal(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarm;
    }

    @Override
    public boolean update(Alarm object) {
        int result = -1;
        try {
            List<AlarmDAO> alarmDAOList = databaseHelper.getAlarmDao().queryBuilder().where().eq(AlarmDAO.iDString, object.getId()).query();
            if(alarmDAOList.isEmpty()) {
                return add(object)!=null;
            }
            AlarmDAO alarmDAO = convertToDao(object);
            alarmDAO.setID(alarmDAOList.get(0).getID());
            result = databaseHelper.getAlarmDao().update(alarmDAO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int alarmId) {
        try {
            List<AlarmDAO> alarmDAOList = databaseHelper.getAlarmDao().queryBuilder().where().eq(AlarmDAO.iDString, alarmId).query();
            if(!alarmDAOList.isEmpty())
            {
                return databaseHelper.getAlarmDao().delete(alarmDAOList)>=0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<Alarm>> get(int alarmId) {
        List<Optional<Alarm>> alarmList = new ArrayList<>();
        try {
            List<AlarmDAO> alarmDAOList = databaseHelper.getAlarmDao().queryBuilder().where().eq(AlarmDAO.iDString, alarmId).query();
            for(AlarmDAO alarmDAO: alarmDAOList) {
                Alarm alarm = convertToNormal(alarmDAO);;
                Optional<Alarm> alarmOptional = new Optional<>();
                alarmOptional.set(alarm);
                alarmList.add(alarmOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmList;
    }

    @Override
    public List<Optional<Alarm>> getAll() {
        List<Optional<Alarm>> alarmList = new ArrayList<>();
        try {
            List<AlarmDAO> alarmDAOList  = databaseHelper.getAlarmDao().queryBuilder().query();
            for(AlarmDAO alarmDAO: alarmDAOList) {
                Optional alarm = new Optional<Alarm>();
                alarm.set(convertToNormal(alarmDAO));
                alarmList.add(alarm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmList;
    }

    @Override
    public List<Alarm> convertToNormalList(List<Optional<Alarm>> optionals) {
        List<Alarm> alarmList = new ArrayList<>();
        for (Optional<Alarm> alarm: optionals) {
            if (alarm.notEmpty()){
                alarmList.add(alarm.get());
            }
        }
        return alarmList;
    }

    private AlarmDAO convertToDao(Alarm alarm){
        AlarmDAO alarmDAO = new AlarmDAO();
        alarmDAO.setAlarm(alarm.getHour() + ":" + alarm.getMinute());
        alarmDAO.setLabel(alarm.getLabel());
        alarmDAO.setWeekDay(alarm.getWeekDay());
        alarmDAO.setAlarmNumber(alarm.getAlarmNumber());
        alarmDAO.setAlarmType(alarm.getAlarmType());
        return alarmDAO;
    }

    private Alarm convertToNormal(AlarmDAO alarmDAO){
        String[] splittedAlarmStrings = alarmDAO.getAlarm().split(":");
        int hour = Integer.parseInt(splittedAlarmStrings[0]);
        int minutes = Integer.parseInt(splittedAlarmStrings[1]);
        Alarm alarm =new Alarm(hour,minutes,alarmDAO.getWeekDay(), alarmDAO.getLabel(),alarmDAO.getAlarmType(),alarmDAO.getAlarmNumber());
        alarm.setId(alarmDAO.getID());

        return  alarm;
    }


}