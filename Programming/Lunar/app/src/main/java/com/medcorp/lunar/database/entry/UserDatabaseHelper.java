package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.User;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper {

    private Realm mRealm;
    private Context mContext;

    public UserDatabaseHelper(Context mContext) {
        this.mContext = mContext;
        mRealm = Realm.getDefaultInstance();
    }

    public Observable<Boolean> add(final User object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        User user = realm.createObject(User.class);
                        user.setId(object.getId());
                        user.setWechat(object.getWechat());
                        user.setAge(object.getAge());
                        user.setBirthday(object.getBirthday());
                        user.setCreatedDate(object.getCreatedDate());
                        user.setFirstName(object.getFirstName());
                        user.setHeight(object.getHeight());
                        user.setWeight(object.getWeight());
                        user.setIsConnectValidic(object.isConnectValidic());
                        user.setLastName(object.getLastName());
                        user.setIsLogin(object.isLogin());
                        user.setUserEmail(object.getUserEmail());
                        user.setUserID(object.getUserID());
                        user.setSex(object.getSex());
                        user.setValidicUserToken(object.getValidicUserToken());
                        user.setRemarks(object.getRemarks());
                        user.setValidicUserID(object.getValidicUserID());
                        user.setUserToken(object.getUserToken());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final User object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        User user = mRealm.where(User.class).equalTo(mContext.getString(R.string.database_user_id),
                                object.getUserID()).equalTo(mContext.getString(R.string.create_date), object.getCreatedDate()).findFirst();
                        if (user != null) {
                            user.setId(object.getId());
                            user.setWechat(object.getWechat());
                            user.setAge(object.getAge());
                            user.setBirthday(object.getBirthday());
                            user.setCreatedDate(object.getCreatedDate());
                            user.setFirstName(object.getFirstName());
                            user.setHeight(object.getHeight());
                            user.setWeight(object.getWeight());
                            user.setIsConnectValidic(object.isConnectValidic());
                            user.setLastName(object.getLastName());
                            user.setIsLogin(object.isLogin());
                            user.setUserEmail(object.getUserEmail());
                            user.setUserID(object.getUserID());
                            user.setSex(object.getSex());
                            user.setValidicUserToken(object.getValidicUserToken());
                            user.setRemarks(object.getRemarks());
                            user.setValidicUserID(object.getValidicUserID());
                            user.setUserToken(object.getUserToken());
                            e.onNext(true);
                            e.onComplete();
                        } else {
                            e.onNext(false);
                            e.onComplete();
                        }
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }

    public void remove(String userId, Date date) {
        final User user = mRealm.where(User.class).equalTo(mContext.getString(R.string.database_user_id), userId)
                .equalTo(mContext.getString(R.string.create_date), date.getTime()).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user.deleteFromRealm();
            }
        });
    }

    public List<User> get(String userId) {
        RealmResults<User> nevoUser = mRealm.where(User.class).equalTo(mContext.getString(R.string.database_user_id), userId).findAll();
        return nevoUser;
    }

    public User get(String userId, Date date) {
        User user = mRealm.where(User.class).equalTo(mContext.getString(R.string.database_user_id)
                , userId).equalTo(mContext.getString(R.string.create_date), date).findFirst();
        return user == null ? new User(System.currentTimeMillis()) : user;
    }

    public List<User> getAll(String userId) {
        return get(userId);
    }

    public Observable<User> getLoginUser() {
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> e) throws Exception {
                User loginUser = null;
                User user = mRealm.where(User.class).equalTo(mContext.getString(R.string.user_is_login), true).findFirst();
                if (user == null) {
                    loginUser = new User(0);
                    loginUser.setUserID("0");
                } else {
                    loginUser = mRealm.copyFromRealm(user);
                }
                e.onNext(loginUser);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}