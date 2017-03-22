package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Solar;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;

/**
 * Created by med on 16/8/30.
 */
public class SolarDatabaseHelper {

    private Realm mRealm;

    public SolarDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public Observable<Boolean> add(final Solar object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Solar solar = realm.copyToRealm(object);
                        if (solar != null) {
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

    public Observable<Boolean> update(final Solar object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Solar solar = mRealm.where(Solar.class).equalTo("id", object.getId())
                                .equalTo("date", object.getDate()).findFirst();
                        if (solar != null) {
                            solar.setId(object.getId());
                            solar.setTotalHarvestingTime(object.getTotalHarvestingTime());
                            solar.setHourlyHarvestingTime(object.getHourlyHarvestingTime());
                            solar.setCreatedDate(object.getCreatedDate());
                            solar.setDate(object.getDate());
                            solar.setUserId(object.getUserId());
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

    public void remove(final String userId, final Date date) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                final Solar solar = mRealm.where(Solar.class).equalTo("userId", userId)
                        .equalTo("createdDate", date).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        solar.deleteFromRealm();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Solar>> get(int userId) {
        return getAll(userId);
    }

    public Observable<Solar> get(final int userId, final Date date) {
        return Observable.create(new ObservableOnSubscribe<Solar>() {
            @Override
            public void subscribe(ObservableEmitter<Solar> e) throws Exception {
                Solar solar = mRealm.where(Solar.class).equalTo("id", userId)
                        .equalTo("createdDate", date).findFirst();
                if (solar != null) {
                    e.onNext(solar);
                    e.onComplete();
                } else {
                    e.onNext(new Solar());
                    e.onComplete();
                }

            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Solar>> getAll(final int userId) {
        return Observable.create(new ObservableOnSubscribe<List<Solar>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Solar>> e) throws Exception {
                List<Solar> solarList = mRealm.where(Solar.class).equalTo("id", userId).findAll();
                if (solarList != null) {
                    e.onNext(solarList);
                    e.onComplete();
                } else {
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

}
