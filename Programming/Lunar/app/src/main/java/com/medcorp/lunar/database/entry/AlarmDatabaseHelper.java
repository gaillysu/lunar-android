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

    public AlarmDatabaseHelper() {
       mRealm = Realm.getDefaultInstance();
    }

    public Alarm add(Alarm object) {
        mRealm.beginTransaction();
        Alarm alarm = mRealm.copyToRealm(object);
        mRealm.commitTransaction();
        return alarm;
    }

    public boolean update(Alarm object) {
        mRealm.beginTransaction();
        Alarm alarm = mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
        return alarm != null;
    }

    public void remove(int alarmId) {
        mRealm.beginTransaction();
        mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public Alarm get(int alarmId) {
        mRealm.beginTransaction();
        Alarm alarm = mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst();
        mRealm.commitTransaction();
        return alarm;
    }

    public List<Alarm> getAll() {
        mRealm.beginTransaction();
        RealmResults<Alarm> allAlarms = mRealm.where(Alarm.class).findAll();
        mRealm.commitTransaction();
        return allAlarms;
    }
}