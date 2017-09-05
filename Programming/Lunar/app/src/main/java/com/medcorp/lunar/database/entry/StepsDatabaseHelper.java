package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.Steps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by karl-john on 17/11/15.
 */
public class StepsDatabaseHelper {

    private Realm mRealm;
    private boolean isNull;
    private Context mContext;

    public StepsDatabaseHelper(Context context) {
        mRealm = Realm.getDefaultInstance();
        mContext = context;
    }

    public Observable<Boolean> addSteps(final Steps object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Steps steps = realm.createObject(Steps.class);
                        steps.setId(object.getId());
                        steps.setSteps(object.getSteps());
                        steps.setRemarks(object.getRemarks());
                        steps.setActiveTimeGoal(object.getActiveTimeGoal());
                        steps.setCalories(object.getCalories());
                        steps.setCloudRecordID(object.getCloudRecordID());
                        steps.setActiveTimeGoal(object.getActiveTimeGoal());
                        steps.setCreatedDate(object.getCreatedDate());
                        steps.setCaloriesGoal(object.getCaloriesGoal());
                        steps.setDistance(object.getDistance());
                        steps.setDistanceGoal(object.getDistanceGoal());
                        steps.setGoal(object.getGoal());
                        steps.setGoalReached(object.getGoalReached());
                        steps.setHourlyCalories(object.getHourlyCalories());
                        steps.setHourlySteps(object.getHourlySteps());
                        steps.setInZoneTime(object.getInZoneTime());
                        steps.setUserID(object.getUserID());
                        steps.setNoActivityTime(object.getNoActivityTime());
                        steps.setHourlyDistance(object.getHourlyDistance());
                        steps.setDate(object.getDate());
                        steps.setRunDistance(object.getRunDistance());
                        steps.setOutZoneTime(object.getOutZoneTime());
                        steps.setWalkDistance(object.getWalkDistance());
                        steps.setRunSteps(object.getRunSteps());
                        steps.setWalkSteps(object.getWalkSteps());
                        steps.setWalkDuration(object.getWalkDuration());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final Steps object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> observable) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Steps steps = mRealm.where(Steps.class).equalTo(mContext.getString(R.string.date), object.getDate())
                                .equalTo(mContext.getString(R.string.database_id), object.getId()).findFirst();
                        if (steps != null) {
                            steps.setId(object.getId());
                            steps.setSteps(object.getSteps());
                            steps.setRemarks(object.getRemarks());
                            steps.setActiveTimeGoal(object.getActiveTimeGoal());
                            steps.setCalories(object.getCalories());
                            steps.setCloudRecordID(object.getCloudRecordID());
                            steps.setActiveTimeGoal(object.getActiveTimeGoal());
                            steps.setCreatedDate(object.getCreatedDate());
                            steps.setCaloriesGoal(object.getCaloriesGoal());
                            steps.setDistance(object.getDistance());
                            steps.setDistanceGoal(object.getDistanceGoal());
                            steps.setGoal(object.getGoal());
                            steps.setGoalReached(object.getGoalReached());
                            steps.setHourlyCalories(object.getHourlyCalories());
                            steps.setHourlySteps(object.getHourlySteps());
                            steps.setInZoneTime(object.getInZoneTime());
                            steps.setUserID(object.getUserID());
                            steps.setNoActivityTime(object.getNoActivityTime());
                            steps.setHourlyDistance(object.getHourlyDistance());
                            steps.setDate(object.getDate());
                            steps.setRunDistance(object.getRunDistance());
                            steps.setOutZoneTime(object.getOutZoneTime());
                            steps.setWalkDistance(object.getWalkDistance());
                            steps.setRunSteps(object.getRunSteps());
                            steps.setWalkSteps(object.getWalkSteps());
                            steps.setWalkDuration(object.getWalkDuration());
                            observable.onNext(true);
                            observable.onComplete();
                        } else {
                            observable.onNext(false);
                            observable.onComplete();
                        }
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public void remove(String userId, Date date) {
        final Steps steps = mRealm.where(Steps.class).equalTo(mContext.getString(R.string.database_user_id), userId)
                .equalTo(mContext.getString(R.string.date), date.getTime()).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                steps.deleteFromRealm();
            }
        });
    }

    public Observable<List<Steps>> get(String userId) {
        return getAll(userId);
    }


    public Observable<Steps> get(final String userId, final Date date) {
        return Observable.create(new ObservableOnSubscribe<Steps>() {
            @Override
            public void subscribe(ObservableEmitter<Steps> e) throws Exception {
                Steps steps = mRealm.where(Steps.class).equalTo(mContext.getString(R.string.database_user_id), userId)
                        .equalTo(mContext.getString(R.string.date), date.getTime()).findFirst();
                if (steps != null) {
                    e.onNext(mRealm.copyFromRealm(steps));
                    e.onComplete();
                } else {
                    e.onNext(new Steps());
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }



    public Observable<List<Steps>> getDailySteps(final String userId, final List<Date> dates) {
        return Observable.create(new ObservableOnSubscribe<List<Steps>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Steps>> e) throws Exception {
                List<Steps> stepLists = new ArrayList<>();
                for (Date date : dates) {
                    Steps steps = mRealm.where(Steps.class).equalTo(mContext.getString(R.string.database_user_id), userId)
                            .equalTo(mContext.getString(R.string.date), date.getTime()).findFirst();
                    if (null != steps) {
                        stepLists.add(mRealm.copyFromRealm(steps));
                    } else {
                        Steps temp = new Steps();
                        temp.setCreatedDate(date.getTime());
                        temp.setDate(date.getTime());
                        stepLists.add(temp);
                    }
                }
                if (stepLists.size() > 0) {
                    e.onNext(stepLists);
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }


    public Observable<List<Steps>> getAll(final String userId) {
        return Observable.create(new ObservableOnSubscribe<List<Steps>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Steps>> e) throws Exception {
                List<Steps> allSteps = new ArrayList<Steps>();
                List<Steps> stepses = mRealm.where(Steps.class).equalTo(mContext.getString(R.string.database_user_id), userId).findAll();
                if(stepses!=null){
                    allSteps = mRealm.copyFromRealm(stepses);
                }
                e.onNext(allSteps);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }


    public Observable<List<Steps>> getNeedSyncSteps(String userId) {
        return getAll(userId);
    }

    public boolean isFoundInLocalSteps(final int activity_id) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                RealmResults<Steps> steps = Realm.getDefaultInstance().where(Steps.class)
                        .equalTo(mContext.getString(R.string.database_id), activity_id).findAll();
                if (steps != null) {
                    e.onNext(true);
                    e.onComplete();
                } else {
                    e.onNext(false);
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                isNull = aBoolean;
            }
        });
        return isNull;
    }

    public boolean isFoundInLocalSteps(Date date, String userId) {
        return get(userId, date) != null;
    }

}
