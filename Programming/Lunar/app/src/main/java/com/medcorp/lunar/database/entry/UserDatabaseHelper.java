package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.database.dao.UserDAO;
import com.medcorp.lunar.model.User;

import net.medcorp.library.worldclock.util.WorldClockLibraryModule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper {

    private Realm mRealm;
    private final String REALM_NAME = "med_lunar.realm";

    public UserDatabaseHelper() {
        RealmConfiguration lunarConfig = new RealmConfiguration.Builder()
                .name(REALM_NAME)
                .modules(new WorldClockLibraryModule(), Realm.getDefaultModule())
                .build();
        mRealm = Realm.getInstance(lunarConfig);
    }

    public User add(User object) {
        mRealm.beginTransaction();
        UserDAO userDAO = mRealm.copyToRealm(convertToDao(object));
        mRealm.commitTransaction();
        return convertToNormal(userDAO);
    }

    public boolean update(User object) {
        mRealm.beginTransaction();
        UserDAO userDAO = mRealm.copyToRealmOrUpdate(convertToDao(object));
        mRealm.commitTransaction();
        return userDAO != null;
    }

    public void remove(String userId, Date date) {
        mRealm.beginTransaction();
        mRealm.where(UserDAO.class).equalTo("nevoUserID", userId).equalTo("createdDate", date).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public List<User> get(String userId) {
        mRealm.beginTransaction();
        RealmResults<UserDAO> nevoUser = mRealm.where(UserDAO.class).equalTo("nevoUserID", userId).findAll();
        mRealm.commitTransaction();
        return convertToNormalList(nevoUser);
    }

    public User get(String userId, Date date) {
        mRealm.beginTransaction();
        UserDAO user = mRealm.where(UserDAO.class).equalTo("nevoUserID", userId).equalTo("createdDate", date).findFirst();
        return user == null ? new User(System.currentTimeMillis()) : convertToNormal(user);
    }

    public List<User> getAll(String userId) {
        return get(userId);
    }

    public User getLoginUser() {
        RealmResults<UserDAO> allUser = mRealm.where(UserDAO.class).findAll();
        UserDAO userDAO = null;
        for (UserDAO user : allUser) {
            if (user.isNevoUserIsLogin()) {
                userDAO = user;
                break;
            }
        }
        return convertToNormal(userDAO);
    }

    private UserDAO convertToDao(User user) {
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
        userDAO.setConnectValidic(user.isConnectValidic());
        userDAO.setWechat(user.getWechat());
        return userDAO;
    }

    private User convertToNormal(UserDAO userDAO) {
        User user = new User(userDAO.getCreatedDate());
        user.setId(userDAO.getId());
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
        user.setIsConnectValidic(userDAO.isConnectValidic());
        user.setWechat(userDAO.getWechat());
        return user;
    }

    public List<User> convertToNormalList(List<UserDAO> optionals) {
        List<User> userList = new ArrayList<>();
        for (UserDAO userOptional : optionals) {
            if (userOptional != null) {
                userList.add(convertToNormal(userOptional));
            }
        }
        return userList;
    }
}