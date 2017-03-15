package com.medcorp.lunar.database.entry;

import android.content.Context;

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

    public LedLampDatabase(Context context) {
        Realm.init(context);
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
        LedLampDAO led = mRealm.where(LedLampDAO.class).equalTo("ID", object.getId()).findFirst();
        LedLampDAO ledLampDAO = mRealm.copyToRealmOrUpdate(led);
        mRealm.commitTransaction();
        return ledLampDAO == null ? false : true;
    }

    public void remove(int rid) {
        mRealm.where(LedLampDAO.class).equalTo("ID", rid).findFirst().deleteFromRealm();
    }


    public LedLamp get(int rid) {
        return convertToNormal(mRealm.where(LedLampDAO.class).equalTo("ID", rid).findFirst());
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
        LedLamp led = new LedLamp();
        led.setName(res.getName());
        led.setColor(res.getColor());
        led.setId(res.getId());
        return led;
    }

    private LedLampDAO convertToDao(LedLamp object) {
        LedLampDAO dao = new LedLampDAO();
        dao.setColor(object.getColor());
        dao.setName(object.getName());
        return dao;
    }
}
