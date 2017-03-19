package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.User;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper {

    private Realm mRealm;

    public UserDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public User add(User object) {
        mRealm.beginTransaction();
        User user = mRealm.copyToRealm(object);
        mRealm.commitTransaction();
        return user;
    }

    public boolean update(User object) {
        mRealm.beginTransaction();
        User userDAO = mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
        return userDAO != null;
    }

    public void remove(String userId, Date date) {
        mRealm.beginTransaction();
        mRealm.where(User.class).equalTo("nevoUserID", userId).equalTo("createdDate", date).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public List<User> get(String userId) {
        mRealm.beginTransaction();
        RealmResults<User> nevoUser = mRealm.where(User.class).equalTo("nevoUserID", userId).findAll();
        mRealm.commitTransaction();
        return nevoUser;
    }

    public User get(String userId, Date date) {
        mRealm.beginTransaction();
//        RealmQuery<User> realmQuery = mRealm.where(User.class);
//        realmQuery.equalTo("nevoUserID", userId);
//        realmQuery.equalTo("createdDate", date);
//        User user = realmQuery.findFirst();
        User user = mRealm.where(User.class).equalTo("nevoUserID", userId).equalTo("createdDate", date).findFirst();
        mRealm.commitTransaction();
        return user == null ? new User(System.currentTimeMillis()) : user;
    }

    public List<User> getAll(String userId) {
        return get(userId);
    }

    public User getLoginUser() {
        mRealm.beginTransaction();
        RealmQuery<User> realmQuery = mRealm.where(User.class);
        RealmResults<User> allUser = realmQuery.findAll();
//        RealmResults<User> allUser = mRealm.where(User.class).findAll();
        mRealm.commitTransaction();
        for (User user : allUser) {
            if (user.isLogin()) {
               return user;
            }
        }
        return null;
    }
}