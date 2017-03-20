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
    private boolean isSuccess;

    public SolarDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public void add(final Solar object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
            }
        });
    }

    public boolean update(final Solar object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Solar solar = mRealm.where(Solar.class).equalTo("id", object.getId())
                        .equalTo("date", object.getDate()).findFirst();
                solar.setId(object.getId());
                solar.setTotalHarvestingTime(object.getTotalHarvestingTime());
                solar.setHourlyHarvestingTime(object.getHourlyHarvestingTime());
                solar.setCreatedDate(object.getCreatedDate());
                solar.setDate(object.getDate());
                solar.setUserId(object.getUserId());
                isSuccess = true;
            }
        });
        return isSuccess;
    }

    public void remove(String userId, Date date) {
        final Solar solar = mRealm.where(Solar.class).equalTo("userId", userId).equalTo("createdDate", date).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                solar.deleteFromRealm();
            }
        });
    }

    public List<Solar> get(int userId) {
        return getAll(userId);
    }

    public Solar get(int userId, Date date) {
        Solar solar = mRealm.where(Solar.class).equalTo("id", userId).equalTo("createdDate", date).findFirst();
        return solar == null ? new Solar() : solar;
    }

    public List<Solar> getAll(int userId) {
        List<Solar> solarList = mRealm.where(Solar.class).equalTo("id", userId).findAll();
        return solarList;
    }

}
