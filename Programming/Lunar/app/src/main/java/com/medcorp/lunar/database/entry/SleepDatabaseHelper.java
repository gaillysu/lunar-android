package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.SleepData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;

/**
 * Created by karl-john on 17/11/15.
 */
public class SleepDatabaseHelper {

    private Realm mRealm;

    public SleepDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public Observable<Boolean> add(final Sleep object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Sleep sleep = realm.createObject(Sleep.class);
                        sleep.setId(object.getId());
                        sleep.setTotalDeepTime(object.getTotalDeepTime());
                        sleep.setTotalLightTime(object.getTotalLightTime());
                        sleep.setTotalSleepTime(object.getTotalSleepTime());
                        sleep.setCloudRecordID(object.getCloudRecordID());
                        sleep.setDate(object.getDate());
                        sleep.setEnd(object.getEnd());
                        sleep.setHourlyDeep(object.getHourlyDeep());
                        sleep.setHourlyLight(object.getHourlyLight());
                        sleep.setHourlySleep(object.getHourlySleep());
                        sleep.setNevoUserID(object.getNevoUserID());
                        sleep.setHourlyWake(object.getHourlyWake());
                        sleep.setRemarks(object.getRemarks());
                        sleep.setTotalDeepTime(object.getTotalDeepTime());
                        sleep.setTotalWakeTime(object.getTotalWakeTime());
                        sleep.setStart(object.getStart());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final Sleep object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Sleep sleep = mRealm.where(Sleep.class).equalTo("createdDate", object.getCreatedDate())
                                .equalTo("start", object.getStart()).findFirst();
                        if (sleep != null) {
                            sleep.setId(object.getId());
                            sleep.setTotalDeepTime(object.getTotalDeepTime());
                            sleep.setTotalLightTime(object.getTotalLightTime());
                            sleep.setTotalSleepTime(object.getTotalSleepTime());
                            sleep.setCloudRecordID(object.getCloudRecordID());
                            sleep.setDate(object.getDate());
                            sleep.setEnd(object.getEnd());
                            sleep.setHourlyDeep(object.getHourlyDeep());
                            sleep.setHourlyLight(object.getHourlyLight());
                            sleep.setHourlySleep(object.getHourlySleep());
                            sleep.setNevoUserID(object.getNevoUserID());
                            sleep.setHourlyWake(object.getHourlyWake());
                            sleep.setRemarks(object.getRemarks());
                            sleep.setTotalDeepTime(object.getTotalDeepTime());
                            sleep.setTotalWakeTime(object.getTotalWakeTime());
                            sleep.setStart(object.getStart());
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

    public Observable<Boolean> remove(final String userId, final Date date) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId)
                        .equalTo("date", date.getTime()).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        sleep.deleteFromRealm();
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Sleep> get(final String userId) {
        return Observable.create(new ObservableOnSubscribe<Sleep>() {
            @Override
            public void subscribe(ObservableEmitter<Sleep> e) throws Exception {
                Sleep dailySteps = null;
                Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId).findFirst();
                if (sleep != null) {
                    dailySteps = mRealm.copyFromRealm(sleep);
                } else {
                    dailySteps = new Sleep(new Date().getTime());
                }
                e.onNext(dailySteps);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Sleep> get(final String userId, final Date date) {
        return Observable.create(new ObservableOnSubscribe<Sleep>() {
            @Override
            public void subscribe(ObservableEmitter<Sleep> e) throws Exception {
                Sleep dailySteps = null;
                Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId)
                        .equalTo("date", date.getTime()).findFirst();
                if (sleep != null) {
                    dailySteps = mRealm.copyFromRealm(sleep);
                } else {
                    dailySteps = new Sleep(new Date().getTime());
                }
                e.onNext(dailySteps);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<SleepData>> getWeekSleep(final String userId, final List<Date> dateList) {
        return Observable.create(new ObservableOnSubscribe<List<SleepData>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SleepData>> e) throws Exception {
                List<SleepData> sleepLists = new ArrayList<>();
                SleepData sleepData = null;
                for (Date date : dateList) {
                    Sleep sleep = mRealm.where(Sleep.class).equalTo("nevoUserID", userId)
                            .equalTo("date", date.getTime()).findFirst();
                    if (null != sleep) {
                        Sleep dailySleep = mRealm.copyFromRealm(sleep);
                        sleepData = new SleepData(dailySleep.getTotalDeepTime()
                                , dailySleep.getTotalLightTime(), dailySleep.getTotalWakeTime(),
                                date.getTime(), dailySleep.getStart(), dailySleep.getEnd());
                    } else {
                        sleepData = new SleepData(0, 0, 0, date.getTime());
                    }
                    sleepLists.add(sleepData);
                }
                if (sleepLists.size() > 0) {
                    e.onNext(sleepLists);
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Sleep>> getAll(final String userId) {
        return Observable.create(new ObservableOnSubscribe<List<Sleep>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Sleep>> e) throws Exception {
                List<Sleep> allSleeps = null;
                List<Sleep> allSleep = mRealm.where(Sleep.class).
                        equalTo("nevoUserID", userId).findAll();
                if (allSleep != null) {
                    allSleeps = mRealm.copyFromRealm(allSleep);
                } else {
                    allSleeps = new ArrayList<Sleep>();
                }
                e.onNext(allSleeps);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Sleep>> getNeedSyncSleep(final String userId) {
        return Observable.create(new ObservableOnSubscribe<List<Sleep>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Sleep>> e) throws Exception {
                List<Sleep> sleepDAOList = mRealm.where(Sleep.class)
                        .equalTo("nevoUserID", userId).findAll();
                if (sleepDAOList != null) {
                    e.onNext(mRealm.copyFromRealm(sleepDAOList));
                    e.onComplete();
                } else {
                    e.onNext(new ArrayList<Sleep>());
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> isFoundInLocalSleep(final int activity_id) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                List<Sleep> sleepList = mRealm.where(Sleep.class)
                        .equalTo("id", activity_id).findAll();
                if (sleepList != null) {
                    e.onNext(true);
                    e.onComplete();
                } else {
                    e.onNext(false);
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public boolean isFoundInLocalSleep(Date date, String userId) {
        return get(userId, date) == null;
    }
}
