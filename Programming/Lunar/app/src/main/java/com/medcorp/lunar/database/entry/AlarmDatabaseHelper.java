package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.model.Alarm;

import java.util.List;

import io.realm.Realm;

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
        Alarm alarm = mRealm.copyToRealm(object);
        mRealm.commitTransaction();
        return alarm;
    }

    public boolean update(Alarm object) {
        mRealm.beginTransaction();
        Alarm alarm = mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
        return alarm == null ? false : true;
    }

    public void remove(int alarmId) {
        mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst().deleteFromRealm();
    }

    public Alarm get(int alarmId) {
        return mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst();
    }

    public List<Alarm> getAll() {
        List<Alarm> list = mRealm.where(Alarm.class).findAll();
        return list;
    }
}