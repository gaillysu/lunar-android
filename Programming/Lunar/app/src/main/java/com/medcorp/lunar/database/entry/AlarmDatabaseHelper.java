package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Alarm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class AlarmDatabaseHelper {

    private Realm mRealm;
    private boolean isSuccess;

    public AlarmDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public boolean add(final Alarm object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
                isSuccess = true;
            }
        });
        return isSuccess;
    }

    public boolean update(final Alarm object) {
       mRealm.executeTransaction(new Realm.Transaction() {
           @Override
           public void execute(Realm realm) {
               Alarm alarm = mRealm.where(Alarm.class).equalTo("alarmNumber", object.getAlarmNumber()).findFirst();
               alarm.setId(object.getId());
               alarm.setWeekDay(object.getWeekDay());
               alarm.setMinute(object.getMinute());
               alarm.setLabel(object.getLabel());
               alarm.setAlarmNumber(object.getAlarmNumber());
               alarm.setAlarmType(object.getAlarmType());
               alarm.setHour(object.getHour());
               isSuccess = true;
           }
       });
        return isSuccess;
    }

    public void remove(int alarmId) {
        final Alarm alarm = mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                alarm.deleteFromRealm();
            }
        });
    }

    public Alarm get(int alarmId) {
        Alarm alarm = mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst();
        return alarm;
    }

    public List<Alarm> getAll() {
        RealmResults<Alarm> allAlarms = mRealm.where(Alarm.class).findAll();
        return allAlarms;
    }
}