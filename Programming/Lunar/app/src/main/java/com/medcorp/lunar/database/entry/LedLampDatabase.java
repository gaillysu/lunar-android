package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.ble.model.color.LedLamp;
import com.medcorp.lunar.database.DatabaseHelper;
import com.medcorp.lunar.database.dao.LedLampDAO;

import net.medcorp.library.ble.util.Optional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2016/12/12.
 */

public class LedLampDatabase implements iSettingDatabaseHelper<LedLamp> {

    private DatabaseHelper databaseHelper;

    public LedLampDatabase(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Optional<LedLamp> add(LedLamp object) {
        Optional<LedLamp> presetOptional = new Optional<>();
        try {
            LedLampDAO res = databaseHelper.getLedDao().createIfNotExists(convertToDao(object));
            if (res != null) {
                presetOptional.set(convertToNormal(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetOptional;
    }


    @Override
    public boolean update(LedLamp object) {
        int result = -1;
        try {
            List<LedLampDAO> ledDaoList = databaseHelper.getLedDao()
                    .queryBuilder().where().eq(LedLampDAO.IDString, object.getId()).query();

            if(ledDaoList.isEmpty())
                return add(object)!=null;

            LedLampDAO ledDao = convertToDao(object);
            ledDao.setID(ledDaoList.get(0).getID());
            result = databaseHelper.getLedDao().update(ledDao);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int rid) {
        try {
            List<LedLampDAO> ledDaoList = databaseHelper.getLedDao()
                    .queryBuilder().where().eq(LedLampDAO.IDString, rid).query();
            if(!ledDaoList.isEmpty())
            {
                return  databaseHelper.getLedDao().delete(ledDaoList)>=0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public List<Optional<LedLamp>> get(int rid) {
        List<Optional<LedLamp>> presetList = new ArrayList<>();
        try {
            List<LedLampDAO> ledDaoList = databaseHelper.getLedDao().queryBuilder()
                    .where().eq(LedLampDAO.IDString, rid).query();

            for(LedLampDAO ledDao : ledDaoList) {
                Optional<LedLamp> presetOptional = new Optional<>();
                presetOptional.set(convertToNormal(ledDao));
                presetList.add(presetOptional);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList;
    }

    @Override
    public List<Optional<LedLamp>> getAll() {
        List<Optional<LedLamp>> presetList = new ArrayList<>();
        try {
            List<LedLampDAO> ledDaoList = databaseHelper.getLedDao().queryBuilder().query();
            for(LedLampDAO dao : ledDaoList) {
                Optional<LedLamp> presetOptional = new Optional<>();
                presetOptional.set(convertToNormal(dao));
                presetList.add(presetOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presetList;
    }

    @Override
    public List<LedLamp> convertToNormalList(List<Optional<LedLamp>> optionals) {
        List<LedLamp> ledList = new ArrayList<>();
        for (Optional<LedLamp> presetOptional: optionals) {
            if (presetOptional.notEmpty()){
                ledList.add(presetOptional.get());
            }
        }
        return ledList;
    }


    private LedLamp convertToNormal(LedLampDAO res) {
        LedLamp led = new LedLamp();
        led.setName(res.getName());
        led.setColor(res.getColor());
        led.setId(res.getID());
        return led;
    }

    private LedLampDAO convertToDao(LedLamp object) {
        LedLampDAO dao = new LedLampDAO();
        dao.setColor(object.getColor());
        dao.setName(object.getName());
        return dao;
    }
}
