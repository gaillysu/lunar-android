package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Solar;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by med on 16/8/30.
 */
public class SolarDatabaseHelper {

    private Realm mRealm;

    public SolarDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public Solar add(Solar object) {
        mRealm.beginTransaction();
        Solar solar = mRealm.copyToRealm(object);
        mRealm.commitTransaction();
        return solar;
    }

    public boolean update(Solar object) {
        mRealm.beginTransaction();
        Solar solar = mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
        return solar == null ? false : true;
    }

    public void remove(String userId, Date date) {
        mRealm.beginTransaction();
        mRealm.where(Solar.class).equalTo("id", userId).equalTo("createdDate", date).findFirst().deleteFromRealm();
        mRealm.commitTransaction();

    }

    public List<Solar> get(int userId) {
        return getAll(userId);
    }

    public Solar get(int userId, Date date) {
        mRealm.beginTransaction();
        Solar solar = mRealm.where(Solar.class).equalTo("id", userId).equalTo("createdDate", date).findFirst();
        mRealm.commitTransaction();
        return solar == null ? new Solar() : solar;
    }

    public List<Solar> getAll(int userId) {
        mRealm.beginTransaction();
        List<Solar> solarList = mRealm.where(Solar.class).equalTo("id", userId).findAll();
        mRealm.commitTransaction();
        return solarList;
    }

}
