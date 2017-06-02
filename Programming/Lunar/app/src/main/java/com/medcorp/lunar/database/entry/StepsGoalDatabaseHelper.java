package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.StepsGoal;

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
public class StepsGoalDatabaseHelper {

    private Realm mRealm;
    private Context mContext;

    public StepsGoalDatabaseHelper(Context context) {
        mRealm = Realm.getDefaultInstance();
        mContext = context;
    }

    public Observable<Boolean> add(final StepsGoal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        StepsGoal stepsGoal = realm.createObject(StepsGoal.class);
                        stepsGoal.setSteps(object.getSteps());
                        stepsGoal.setId(object.getId());
                        stepsGoal.setSteps(object.getSteps());
                        stepsGoal.setLabel(object.getLabel());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final StepsGoal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        StepsGoal stepsGoal = mRealm.where(StepsGoal.class).equalTo(mContext.getString(R.string.database_id)
                                , object.getId())
                                .equalTo(mContext.getString(R.string.database_goal_label), object.getLabel()).findFirst();
                        if (stepsGoal != null) {
                            stepsGoal.setSteps(object.getSteps());
                            stepsGoal.setId(object.getId());
                            stepsGoal.setStatus(object.isStatus());
                            stepsGoal.setLabel(object.getLabel());
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
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                final StepsGoal stepsGoal = mRealm.where(StepsGoal.class).equalTo(mContext.getString(R.string.database_id)
                        , presetId).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        stepsGoal.deleteFromRealm();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }

    public Observable<StepsGoal> get(final int presetId) {
        return Observable.create(new ObservableOnSubscribe<StepsGoal>() {
            @Override
            public void subscribe(ObservableEmitter<StepsGoal> e) throws Exception {
                StepsGoal stepsGoal = mRealm.where(StepsGoal.class).equalTo(mContext.getString(R.string.database_id)
                        , presetId).findFirst();
                if (stepsGoal != null) {
                    e.onNext(mRealm.copyFromRealm(stepsGoal));
                    e.onComplete();
                }
            }
        });
    }

    public Observable<List<StepsGoal>> getAll() {
        return Observable.create(new ObservableOnSubscribe<List<StepsGoal>>() {
            @Override
            public void subscribe(ObservableEmitter<List<StepsGoal>> e) throws Exception {
                RealmResults<StepsGoal> allStepsGoal = mRealm.where(StepsGoal.class).findAll();
                if (allStepsGoal != null) {
                    e.onNext(mRealm.copyFromRealm(allStepsGoal));
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}