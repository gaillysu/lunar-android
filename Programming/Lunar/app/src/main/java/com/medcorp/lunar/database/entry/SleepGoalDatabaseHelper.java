package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.SleepGoal;

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

public class SleepGoalDatabaseHelper {

    private Realm mRealm;
    private Context mContext;

    public SleepGoalDatabaseHelper(Context context) {
        mRealm = Realm.getDefaultInstance();
        mContext = context;
    }

    public Observable<Boolean> add(final SleepGoal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        SleepGoal goal = realm.createObject(SleepGoal.class);
                        goal.setGoalDuration(object.getGoalDuration());
                        goal.setGoalName(object.getGoalName());
                        goal.setStatus(object.isStatus());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final SleepGoal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        SleepGoal goal = mRealm.where(SleepGoal.class).equalTo(mContext.getString(R.string.sleep_goal_id), object.getSleepGoalId()).findFirst();
                        if (goal != null) {
                            goal.setStatus(object.isStatus());
                            goal.setGoalName(object.getGoalName());
                            goal.setGoalDuration(object.getGoalDuration());
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
                final SleepGoal goal = mRealm.where(SleepGoal.class).equalTo(mContext.getString(R.string.sleep_goal_id)
                        , presetId).findFirst();
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

    public Observable<SleepGoal> get(final int presetId) {
        return Observable.create(new ObservableOnSubscribe<SleepGoal>() {
            @Override
            public void subscribe(ObservableEmitter<SleepGoal> e) throws Exception {
                SleepGoal goal = mRealm.where(SleepGoal.class).equalTo(mContext.getString(R.string.sleep_goal_id)
                        , presetId).findFirst();
                if (goal != null) {
                    e.onNext(mRealm.copyFromRealm(goal));
                    e.onComplete();
                }
            }
        });
    }

    public Observable<List<SleepGoal>> getAll() {
        return Observable.create(new ObservableOnSubscribe<List<SleepGoal>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SleepGoal>> e) throws Exception {
                RealmResults<SleepGoal> allGoal = mRealm.where(SleepGoal.class).findAll();
                if (allGoal != null) {
                    e.onNext(mRealm.copyFromRealm(allGoal));
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SleepGoal> getSelectedGoal() {
        return Observable.create(new ObservableOnSubscribe<SleepGoal>() {
            @Override
            public void subscribe(ObservableEmitter<SleepGoal> e) throws Exception {
                RealmResults<SleepGoal> allGoal = mRealm.where(SleepGoal.class).findAll();
                boolean found = false;
                for(SleepGoal sleepGoal:allGoal) {
                    if(sleepGoal.isStatus()) {
                        found = true;
                        e.onNext(mRealm.copyFromRealm(sleepGoal));
                        e.onComplete();
                        break;
                    }
                }
                if(!found) {
                    e.onNext(new SleepGoal("unknown",480,false));
                    e.onComplete();
                }
            }
        });
    }
}
