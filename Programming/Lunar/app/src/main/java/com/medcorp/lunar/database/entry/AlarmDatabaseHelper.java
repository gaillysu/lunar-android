package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Alarm;

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
public class AlarmDatabaseHelper {

    private Realm mRealm;
    private boolean isSuccess;

    public AlarmDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public Observable<Boolean> add(final Alarm object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Alarm alarm = realm.copyToRealm(object);
                        if (alarm != null) {
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

    public Observable<Boolean> update(final Alarm object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Alarm alarm = mRealm.where(Alarm.class).equalTo("alarmNumber",
                                object.getAlarmNumber()).findFirst();
                        if (alarm != null) {
                            alarm.setId(object.getId());
                            alarm.setWeekDay(object.getWeekDay());
                            alarm.setMinute(object.getMinute());
                            alarm.setLabel(object.getLabel());
                            alarm.setAlarmNumber(object.getAlarmNumber());
                            alarm.setAlarmType(object.getAlarmType());
                            alarm.setHour(object.getHour());
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

    public void remove(final int alarmId) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                final Alarm alarm = mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alarm.deleteFromRealm();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Alarm> get(final int alarmId) {
        return Observable.create(new ObservableOnSubscribe<Alarm>() {
            @Override
            public void subscribe(ObservableEmitter<Alarm> e) throws Exception {
                Alarm alarm = mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst();
                if (alarm != null) {
                    e.onNext(alarm);
                    e.onComplete();
                } else {
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Alarm>> getAll() {
        return Observable.create(new ObservableOnSubscribe<List<Alarm>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Alarm>> e) throws Exception {
                RealmResults<Alarm> allAlarms = mRealm.where(Alarm.class).findAll();
                if (allAlarms != null) {
                    e.onNext(allAlarms);
                    e.onComplete();
                } else {
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}