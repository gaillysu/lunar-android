package com.medcorp.lunar.database.entry;

import com.medcorp.lunar.model.Goal;

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
public class GoalDatabaseHelper {

    private Realm mRealm;

    public GoalDatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public Observable<Boolean> add(final Goal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Goal goal = realm.createObject(Goal.class);
                        goal.setSteps(object.getSteps());
                        goal.setId(object.getId());
                        goal.setSteps(object.getSteps());
                        goal.setLabel(object.getLabel());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final Goal object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Goal goal = mRealm.where(Goal.class).equalTo("is", object.getId())
                                .equalTo("label", object.getLabel()).findFirst();
                        if (goal != null) {
                            goal.setSteps(object.getSteps());
                            goal.setId(object.getId());
                            goal.setSteps(object.getSteps());
                            goal.setLabel(object.getLabel());
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

    public void remove(final int presetId) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                final Goal goal = mRealm.where(Goal.class).equalTo("id", presetId).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        goal.deleteFromRealm();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }

    public Observable<Goal> get(final int presetId) {
        return Observable.create(new ObservableOnSubscribe<Goal>() {
            @Override
            public void subscribe(ObservableEmitter<Goal> e) throws Exception {
                Goal goal = mRealm.where(Goal.class).equalTo("id", presetId).findFirst();
                if (goal != null) {
                    e.onNext(mRealm.copyFromRealm(goal));
                    e.onComplete();
                }
            }
        });
    }

    public Observable<List<Goal>> getAll() {
        return Observable.create(new ObservableOnSubscribe<List<Goal>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Goal>> e) throws Exception {
                RealmResults<Goal> allGoal = mRealm.where(Goal.class).findAll();
                if (allGoal != null) {
                    e.onNext(mRealm.copyFromRealm(allGoal));
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}