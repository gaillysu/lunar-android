package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.SolarGoal;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jason on 2017/5/27.
 */

public class SolarGoalDatabaseHelper {


    private Realm mRealm;
    private Context mContext;

    public SolarGoalDatabaseHelper(Context context) {
        mRealm = Realm.getDefaultInstance();
        mContext = context;
    }

    public Observable<Boolean> add(final SolarGoal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        SolarGoal goal = realm.createObject(SolarGoal.class);
                        goal.setStatus(object.isStatus());
                        goal.setName(object.getName());
                        goal.setTime(object.getTime());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final SolarGoal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        SolarGoal goal = mRealm.where(SolarGoal.class).equalTo(mContext.getString(R.string.solar_goal_id), object.getSolarGoalId()).findFirst();
                        if (goal != null) {
                            goal.setStatus(object.isStatus());
                            goal.setName(object.getName());
                            goal.setTime(object.getTime());
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

    public Observable<Boolean> remove(final int presetId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final SolarGoal goal = mRealm.where(SolarGoal.class).
                        equalTo(mContext.getString(R.string.solar_goal_id), presetId).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (goal != null) {
                            goal.deleteFromRealm();
                            e.onNext(true);
                        }else{
                            e.onNext(false);
                        }
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }

    public Observable<SolarGoal> get(final int presetId) {
        return Observable.create(new ObservableOnSubscribe<SolarGoal>() {
            @Override
            public void subscribe(ObservableEmitter<SolarGoal> e) throws Exception {
                SolarGoal goal = mRealm.where(SolarGoal.class).equalTo(mContext.getString(R.string.solar_goal_id)
                        , presetId).findFirst();
                if (goal != null) {
                    e.onNext(mRealm.copyFromRealm(goal));
                    e.onComplete();
                }
            }
        });
    }

    public Observable<List<SolarGoal>> getAll() {
        return Observable.create(new ObservableOnSubscribe<List<SolarGoal>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SolarGoal>> e) throws Exception {
                RealmResults<SolarGoal> allGoal = mRealm.where(SolarGoal.class).findAll();
                if (allGoal != null) {
                    e.onNext(mRealm.copyFromRealm(allGoal));
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SolarGoal> getSelectedGoal() {
        return Observable.create(new ObservableOnSubscribe<SolarGoal>() {
            @Override
            public void subscribe(ObservableEmitter<SolarGoal> e) throws Exception {
                RealmResults<SolarGoal> allGoal = mRealm.where(SolarGoal.class).findAll();
                boolean found = false;
                for(SolarGoal solarGoal:allGoal) {
                    if(solarGoal.isStatus()) {
                        found = true;
                        e.onNext(mRealm.copyFromRealm(solarGoal));
                        e.onComplete();
                        break;
                    }
                }
                if(!found) {
                    e.onNext(new SolarGoal("unknown",30,false));
                    e.onComplete();
                }
            }
        });
    }
}
