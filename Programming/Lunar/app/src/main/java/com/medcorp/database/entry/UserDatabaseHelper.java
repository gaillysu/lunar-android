package com.medcorp.database.entry;

import android.content.Context;

import com.medcorp.database.dao.UserDAO;
import com.medcorp.database.DatabaseHelper;
import com.medcorp.model.User;

import net.medcorp.library.ble.util.Optional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper implements iEntryDatabaseHelper<User> {

    private DatabaseHelper databaseHelper;

    public UserDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Optional<User> add(User object) {
        Optional<User> userOptional = new Optional<>();
        try {
            int res = databaseHelper.getUserDao().create(convertToDao(object));
            if(res>0)
            {
                userOptional.set(object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userOptional;
    }

    @Override
    public boolean update(User object) {
        int result = -1;
        try {
            List<UserDAO> userDAOList = databaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fNevoUserID, object.getNevoUserID()).query();
            if(userDAOList.isEmpty()){
                return add(object)!=null;
            }
            UserDAO daoObject = convertToDao(object);
            daoObject.setID(userDAOList.get(0).getID());
            result = databaseHelper.getUserDao().update(daoObject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(String userId,Date date) {
        try {
            List<UserDAO> userDAOList = databaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fNevoUserID, userId).query();
            if(!userDAOList.isEmpty())
            {
                return databaseHelper.getUserDao().delete(userDAOList)>=0;                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<User>> get(String userId) {
        List<Optional<User>> userList = new ArrayList<Optional<User>>();
        try {
            List<UserDAO> userDAOList = databaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fNevoUserID, userId).query();
            for(UserDAO userDAO: userDAOList) {
                Optional<User> userOptional = new Optional<>();
                userOptional.set(convertToNormal(userDAO));
                userList.add(userOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public Optional<User> get(String userId,Date date) {
        List<Optional<User>> userList = get(userId);
        return userList.isEmpty()?new Optional<User>(): userList.get(0);
    }

    @Override
    public List<Optional<User>> getAll(String userId) {
        return get(userId);
    }

    public Optional<User> getLoginUser()
    {
        Optional<User> userOptional = new Optional<>();
        try {
            //logged in nevo
            List<UserDAO> userDAOList = databaseHelper.getUserDao().queryBuilder().orderBy(UserDAO.fCreatedDate,false).where().eq(UserDAO.fNevoUserIsLogin, true).query();
            for(UserDAO userDAO: userDAOList) {
                userOptional.set(convertToNormal(userDAO));
                return userOptional;
            }
            //logged in validic
            userDAOList = databaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fValidicUserIsConnected, true).query();
            for(UserDAO userDAO: userDAOList) {
                userOptional.set(convertToNormal(userDAO));
                return userOptional;
            }
            //logged out nevo, but register nevo successfully
            userDAOList = databaseHelper.getUserDao().queryBuilder().query();
            for(UserDAO userDAO: userDAOList) {
                if(userDAO.getNevoUserToken()!=null) {
                    userOptional.set(convertToNormal(userDAO));
                    return userOptional;
                }
            }
            //anonymous user
            if(!userDAOList.isEmpty()){
                userOptional.set(convertToNormal(userDAOList.get(0)));
                return userOptional;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userOptional;
    }

    private UserDAO convertToDao(User user){
        UserDAO userDAO = new UserDAO();
        userDAO.setCreatedDate(user.getCreatedDate());
        userDAO.setHeight(user.getHeight());
        userDAO.setAge(user.getAge());
        userDAO.setBirthday(user.getBirthday());
        userDAO.setWeight(user.getWeight());
        userDAO.setRemarks(user.getRemarks());
        userDAO.setFirstName(user.getFirstName());
        userDAO.setLastName(user.getLastName());
        userDAO.setSex(user.getSex());
        userDAO.setNevoUserEmail(user.getNevoUserEmail());
        userDAO.setNevoUserID(user.getNevoUserID());
        userDAO.setNevoUserToken(user.getNevoUserToken());
        userDAO.setValidicUserID(user.getValidicUserID());
        userDAO.setValidicUserToken(user.getValidicUserToken());
        userDAO.setNevoUserIsLogin(user.isLogin());
        userDAO.setValidicUserIsConnected(user.isConnectValidic());
        return userDAO;
    }

    private User convertToNormal(UserDAO userDAO){
        User user = new User(userDAO.getCreatedDate());
        user.setId(userDAO.getID());
        user.setAge(userDAO.getAge());
        user.setHeight(userDAO.getHeight());
        user.setBirthday(userDAO.getBirthday());
        user.setWeight(userDAO.getWeight());
        user.setRemarks(userDAO.getRemarks());
        user.setFirstName(userDAO.getFirstName());
        user.setLastName(userDAO.getLastName());
        user.setSex(userDAO.getSex());
        user.setNevoUserEmail(userDAO.getNevoUserEmail());
        user.setNevoUserID(userDAO.getNevoUserID());
        user.setNevoUserToken(userDAO.getNevoUserToken());
        user.setValidicUserID(userDAO.getValidicUserID());
        user.setValidicUserToken(userDAO.getValidicUserToken());
        user.setIsLogin(userDAO.isNevoUserIsLogin());
        user.setIsConnectValidic(userDAO.isValidicUserIsConnected());
        return user;
    }

    @Override
    public List<User> convertToNormalList(List<Optional<User>> optionals) {
        List<User> userList = new ArrayList<>();
        for (Optional<User> userOptional: optionals) {
            if (userOptional.notEmpty()){
                userList.add(userOptional.get());
            }
        }
        return userList;
    }
}