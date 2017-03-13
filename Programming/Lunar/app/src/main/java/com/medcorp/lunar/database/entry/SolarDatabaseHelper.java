package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.database.dao.SolarDAO;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.util.Common;

import net.medcorp.library.ble.util.Optional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by med on 16/8/30.
 */
public class SolarDatabaseHelper {

    private Realm mRealm;

    public SolarDatabaseHelper(Context context) {
        Realm.init(context);
        mRealm = Realm.getDefaultInstance();
    }

    public Solar add(Solar object) {
        mRealm.beginTransaction();
        SolarDAO solarDAO = mRealm.copyToRealm(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(solarDAO);
    }

    public boolean update(Solar object) {
        mRealm.beginTransaction();
        SolarDAO solarDAO = mRealm.copyToRealmOrUpdate(convertToDao(object));
        mRealm.commitTransaction();
        return solarDAO == null ? false : true;
    }

    public void remove(String userId, Date date) {
        mRealm.where(SolarDAO.class).equalTo("ID", userId).equalTo("CreatedDate", date).findFirst().deleteFromRealm();

    }

    public List<Solar> get(String userId) {
        return getAll(userId);
    }

    public Optional<Solar> get(String userId, Date date) {
        List<Optional<Solar>> stepsList = new ArrayList<>();
        List<SolarDAO> solarDAOList = mRealm.where(SolarDAO.class).equalTo("ID", userId).equalTo("CreatedDate", date).findAll();
        for (SolarDAO solarDAO : solarDAOList) {
            Optional<Solar> solarOptional = new Optional<>();
            solarOptional.set(convertToNormal(solarDAO));
            stepsList.add(solarOptional);
        }
        return stepsList.isEmpty() ? new Optional<Solar>() : stepsList.get(0);
    }

    public List<Solar> getAll(String userId) {
        List<Solar> stepsList = new ArrayList<>();
        List<SolarDAO> solarDAOList = mRealm.where(SolarDAO.class).equalTo("ID", userId).findAll();
        for (SolarDAO solarDAO : solarDAOList) {
            stepsList.add(convertToNormal(solarDAO));
        }
        return stepsList;
    }

    private SolarDAO convertToDao(Solar solar) {
        SolarDAO solarDAO = new SolarDAO();
        solarDAO.setUserId(solar.getUserId());
        solarDAO.setCreatedDate(solar.getCreatedDate());
        solarDAO.setDate(Common.removeTimeFromDate(solar.getDate()));
        solarDAO.setTotalHarvestingTime(solar.getTotalHarvestingTime());
        solarDAO.setHourlyHarvestingTime(solar.getHourlyHarvestingTime());
        return solarDAO;
    }

    private Solar convertToNormal(SolarDAO solarDAO) {
        Solar solar = new Solar(solarDAO.getCreatedDate());
        solar.setId(solarDAO.getID());
        solar.setDate(solarDAO.getDate());
        solar.setHourlyHarvestingTime(solarDAO.getHourlyHarvestingTime());
        solar.setTotalHarvestingTime(solarDAO.getTotalHarvestingTime());
        solar.setUserId(solarDAO.getUserId());
        return solar;
    }

    public List<Solar> convertToNormalList(List<Optional<Solar>> optionals) {
        List<Solar> solarList = new ArrayList<>();
        for (Optional<Solar> solarOptional : optionals) {
            if (solarOptional.notEmpty()) {
                solarList.add(solarOptional.get());
            }
        }
        return solarList;
    }
}
