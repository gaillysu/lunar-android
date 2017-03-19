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

    public LedLampDatabase() {
        mRealm = Realm.getDefaultInstance();
    }

    public LedLamp add(LedLamp object) {
        mRealm.beginTransaction();
        LedLampDAO ledLampDAO = mRealm.copyToRealm(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(ledLampDAO);
    }


    public boolean update(LedLamp object) {
        mRealm.beginTransaction();
        LedLampDAO led = mRealm.where(LedLampDAO.class).equalTo("id", object.getId()).findFirst();
        LedLampDAO ledLampDAO = mRealm.copyToRealmOrUpdate(led);
        mRealm.commitTransaction();
        return ledLampDAO != null;
    }

    public void remove(int rid) {
        mRealm.beginTransaction();
        mRealm.where(LedLampDAO.class).equalTo("id", rid).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }


    public LedLamp get(int rid) {
        mRealm.beginTransaction();
        LedLamp led = convertToNormal(mRealm.where(LedLampDAO.class).equalTo("id", rid).findFirst());
        mRealm.commitTransaction();
        return led;
    }

    public List<LedLamp> getAll() {
        mRealm.beginTransaction();
        RealmResults<LedLampDAO> all = mRealm.where(LedLampDAO.class).findAll();
        mRealm.commitTransaction();
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
        if(res != null) {
            LedLamp led = new LedLamp();
            led.setName(res.getName());
            led.setColor(res.getColor());
            led.setId(res.getId());
            return led;
        }
        return null;
    }

    private LedLampDAO convertToDao(LedLamp object) {
        if(object!=null) {
            LedLampDAO dao = new LedLampDAO();
            dao.setColor(object.getColor());
            dao.setName(object.getName());
            return dao;
        }
        return null;
    }
}
