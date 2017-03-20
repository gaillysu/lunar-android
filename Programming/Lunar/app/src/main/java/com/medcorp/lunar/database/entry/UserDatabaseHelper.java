package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.User;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper {

    private Realm mRealm;
    private boolean isSuccess;

    public UserDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public void add(final User object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
            }
        });
    }

    public boolean update(final User object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
                isSuccess = true;
            }
        });
        return isSuccess;
    }

    public void remove(String userId, Date date) {
        final User user = mRealm.where(User.class).equalTo("nevoUserID", userId)
                .equalTo("createdDate", date).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user.deleteFromRealm();
            }
        });
    }

    public List<User> get(String userId) {
        RealmResults<User> nevoUser = mRealm.where(User.class).equalTo("nevoUserID", userId).findAll();
        return nevoUser;
    }

    public User get(String userId, Date date) {
        User user = mRealm.where(User.class).equalTo("nevoUserID", userId).equalTo("createdDate", date).findFirst();
        return user == null ? new User(System.currentTimeMillis()) : user;
    }

    public List<User> getAll(String userId) {
        return get(userId);
    }

    public User getLoginUser() {
        RealmResults<User> allUser = mRealm.where(User.class).findAll();
        for (User user : allUser) {
            if (user.isLogin()) {
                return user;
            }
        }
        return null;
    }
}