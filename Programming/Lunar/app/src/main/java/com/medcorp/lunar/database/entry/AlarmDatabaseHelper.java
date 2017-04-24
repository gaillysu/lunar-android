package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.Alarm;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;

/**
 * Created by karl-john on 17/11/15.
 */
public class AlarmDatabaseHelper {

    private Realm mRealm;
    private Context mContext;
    public AlarmDatabaseHelper(Context context) {
        mRealm = Realm.getDefaultInstance();
        mContext = context;
    }

    public Observable<Boolean> add(final Alarm object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Alarm alarm = realm.createObject(Alarm.class);
                        alarm.setId(object.getId());
                        alarm.setWeekDay(object.getWeekDay());
                        alarm.setMinute(object.getMinute());
                        alarm.setLabel(object.getLabel());
                        alarm.setAlarmNumber(object.getAlarmNumber());
                        alarm.setAlarmType(object.getAlarmType());
                        alarm.setHour(object.getHour());
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
                        Alarm alarm = mRealm.where(Alarm.class).equalTo(mContext.getString(R.string.alarm_number),
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

    public Observable<Boolean> remove(final int alarmId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final Alarm alarm = mRealm.where(Alarm.class).equalTo(mContext.getString(R.string.database_id)
                        , alarmId).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (alarm != null) {
                            alarm.deleteFromRealm();
                        }
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Alarm> get(final int alarmId) {
        return Observable.create(new ObservableOnSubscribe<Alarm>() {
            @Override
            public void subscribe(ObservableEmitter<Alarm> e) throws Exception {
                Alarm obtainAlarm = null;
                Alarm alarm = mRealm.where(Alarm.class).equalTo(mContext.getString(R.string.database_id), alarmId).findFirst();
                if (alarm != null) {
                    obtainAlarm = mRealm.copyFromRealm(alarm);
                } else {
                    obtainAlarm = new Alarm();
                }
                e.onNext(obtainAlarm);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Alarm>> getAll() {
        return Observable.create(new ObservableOnSubscribe<List<Alarm>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Alarm>> e) throws Exception {
                List<Alarm> allAlarms = mRealm.where(Alarm.class).findAll();
                if (allAlarms != null) {
                    e.onNext(mRealm.copyFromRealm(allAlarms));
                    e.onComplete();
                } else {
                    e.onNext(new ArrayList<Alarm>());
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}