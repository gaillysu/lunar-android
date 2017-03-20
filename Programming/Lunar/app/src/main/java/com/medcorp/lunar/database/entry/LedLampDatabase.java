package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.ble.model.color.LedLamp;
import com.medcorp.lunar.database.dao.LedLampDAO;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jason on 2016/12/12.
 */

public class LedLampDatabase {

    private Realm mRealm;
    private boolean isSuccess;

    public LedLampDatabase() {
        mRealm = Realm.getDefaultInstance();
    }

    public boolean add(final LedLamp object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(convertToDao(object));
                isSuccess = true;
            }
        });
        return isSuccess;
    }


    public boolean update(final LedLamp object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                LedLampDAO led = mRealm.where(LedLampDAO.class).equalTo("id", object.getId()).findFirst();
                led.setColor(object.getColor());
                led.setName(object.getName());
                isSuccess = true;
            }
        });
        return isSuccess;
    }

    public boolean remove(final String name, final int color) {
        final LedLampDAO ledLamp = mRealm.where(LedLampDAO.class)
                .equalTo("name", name).equalTo("color", color).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (ledLamp != null) {
                    ledLamp.deleteFromRealm();
                    isSuccess = true;
                }
            }
        });
        return isSuccess;
    }


    public LedLamp get(String name, int color) {
        LedLamp led = convertToNormal(mRealm.where(LedLampDAO.class)
                .equalTo("name", name).equalTo("color", color).findFirst());
        return led;
    }

    public List<LedLamp> getAll() {
        RealmResults<LedLampDAO> all = mRealm.where(LedLampDAO.class).findAll();
        return convertToNormalList(all);
    }

    public List<LedLamp> convertToNormalList(List<LedLampDAO> optionals) {
        List<LedLamp> ledList = new ArrayList<>();
        for (LedLampDAO presetOptional : optionals) {
            if (presetOptional != null) {
                ledList.add(convertToNormal(presetOptional));
            }
        }
        return ledList;
    }


    private LedLamp convertToNormal(LedLampDAO res) {
        if (res != null) {
            LedLamp led = new LedLamp();
            led.setName(res.getName());
            led.setColor(res.getColor());
            led.setId(res.getId());
            return led;
        }
        return null;
    }

    private LedLampDAO convertToDao(LedLamp object) {
        if (object != null) {
            LedLampDAO dao = new LedLampDAO();
            dao.setColor(object.getColor());
            dao.setName(object.getName());
            return dao;
        }
        return null;
    }
}
