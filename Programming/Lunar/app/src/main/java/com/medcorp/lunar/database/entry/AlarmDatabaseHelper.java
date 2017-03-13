package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.database.dao.AlarmDAO;
import com.medcorp.lunar.model.Alarm;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class AlarmDatabaseHelper {

    private Realm mRealm;

    public AlarmDatabaseHelper(Context context) {
        Realm.init(context);
        mRealm = Realm.getDefaultInstance();
    }

    public Alarm add(Alarm object) {
        mRealm.beginTransaction();
        AlarmDAO alarmDAO = mRealm.copyToRealm(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(alarmDAO);
    }

    public boolean update(Alarm object) {
        mRealm.beginTransaction();
        AlarmDAO alarmDAO = mRealm.copyToRealmOrUpdate(convertToDao(object));
        mRealm.commitTransaction();
        return alarmDAO == null ? false : true;
    }

    public void remove(int alarmId) {
        mRealm.where(AlarmDAO.class).equalTo("ID", alarmId).findFirst().deleteFromRealm();
    }

    public Alarm get(int alarmId) {
        return convertToNormal(mRealm.where(AlarmDAO.class).equalTo("ID", alarmId).findFirst());
    }

    public List<Alarm> getAll() {
        RealmResults<AlarmDAO> allAlarmsDAO = mRealm.where(AlarmDAO.class).findAll();
        return convertToNormalList(allAlarmsDAO);
    }

    private AlarmDAO convertToDao(Alarm alarm) {
        AlarmDAO alarmDAO = new AlarmDAO();
        alarmDAO.setAlarm(alarm.getHour() + ":" + alarm.getMinute());
        alarmDAO.setLabel(alarm.getLabel());
        alarmDAO.setWeekDay(alarm.getWeekDay());
        alarmDAO.setAlarmNumber(alarm.getAlarmNumber());
        alarmDAO.setAlarmType(alarm.getAlarmType());
        return alarmDAO;
    }

    private Alarm convertToNormal(AlarmDAO alarmDAO) {
        String[] splittedAlarmStrings = alarmDAO.getAlarm().split(":");
        int hour = Integer.parseInt(splittedAlarmStrings[0]);
        int minutes = Integer.parseInt(splittedAlarmStrings[1]);
        Alarm alarm = new Alarm(hour, minutes, alarmDAO.getWeekDay(), alarmDAO.getLabel(), alarmDAO.getAlarmType(), alarmDAO.getAlarmNumber());
        alarm.setId(alarmDAO.getID());
        return alarm;
    }

    public List<Alarm> convertToNormalList(List<AlarmDAO> optionals) {
        List<Alarm> goalList = new ArrayList<>();
        for (AlarmDAO presetOptional : optionals) {
            if (presetOptional != null) {
                goalList.add(convertToNormal(presetOptional));
            }
        }
        return goalList;
    }
}