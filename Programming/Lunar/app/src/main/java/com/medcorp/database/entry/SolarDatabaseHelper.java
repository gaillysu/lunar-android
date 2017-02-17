package com.medcorp.database.entry;

import android.content.Context;

import com.medcorp.database.DatabaseHelper;
import com.medcorp.database.dao.SolarDAO;
import com.medcorp.model.Solar;
import com.medcorp.util.Common;

import net.medcorp.library.ble.util.Optional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by med on 16/8/30.
 */
public class SolarDatabaseHelper implements iEntryDatabaseHelper<Solar> {

    private DatabaseHelper databaseHelper;

    public SolarDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Optional<Solar> add(Solar object) {
        Optional<Solar> solarOptional = new Optional<>();
        try {
            SolarDAO solarDAO = databaseHelper.getSolarDAO().createIfNotExists(convertToDao(object));
            if (solarDAO != null) {
                solarOptional.set(convertToNormal(solarDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return solarOptional;
    }

    @Override
    public boolean update(Solar object) {
        Optional<Solar> solarOptional = get(object.getUserId()+"",object.getDate());
        if(solarOptional.isEmpty()){
            return add(object).notEmpty();
        }
        SolarDAO solarDAO = convertToDao(object);
        solarDAO.setID(solarOptional.get().getId());
        try {
            return databaseHelper.getSolarDAO().update(solarDAO)>=0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean remove(String userId, Date date) {
        try {
            List<SolarDAO> solarDAOList = databaseHelper.getSolarDAO().queryBuilder().where().eq(SolarDAO.fUserId, userId).and().eq(SolarDAO.fDate, Common.removeTimeFromDate(date)).query();
            if (!solarDAOList.isEmpty()) {
                return databaseHelper.getSolarDAO().delete(solarDAOList) >= 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<Solar>> get(String userId) {
        return getAll(userId);
    }

    @Override
    public Optional<Solar> get(String userId, Date date) {
        List<Optional<Solar>> stepsList = new ArrayList<>();
        try {
            List<SolarDAO> solarDAOList = databaseHelper.getSolarDAO().queryBuilder().where().eq(SolarDAO.fUserId, userId).and().eq(SolarDAO.fDate, Common.removeTimeFromDate(date)).query();
            for (SolarDAO solarDAO : solarDAOList) {
                Optional<Solar> solarOptional = new Optional<>();
                solarOptional.set(convertToNormal(solarDAO));
                stepsList.add(solarOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList.isEmpty() ? new Optional<Solar>() : stepsList.get(0);
    }

    @Override
    public List<Optional<Solar>> getAll(String userId) {
        List<Optional<Solar>> stepsList = new ArrayList<>();
        try {
            List<SolarDAO> solarDAOList = databaseHelper.getSolarDAO().queryBuilder().where().eq(SolarDAO.fUserId, userId).query();
            for (SolarDAO solarDAO : solarDAOList) {
                Optional<Solar> solarOptional = new Optional<>();
                solarOptional.set(convertToNormal(solarDAO));
                stepsList.add(solarOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepsList;
    }

    private SolarDAO convertToDao(Solar solar)
    {
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
    @Override
    public List<Solar> convertToNormalList(List<Optional<Solar>> optionals) {
        List<Solar> solarList = new ArrayList<>();
        for(Optional<Solar> solarOptional:optionals)
        {
            if(solarOptional.notEmpty()){
                solarList.add(solarOptional.get());
            }
        }
        return solarList;
    }
}
