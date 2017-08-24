package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.BedtimeModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import io.realm.RealmResults;

/***
 * Created by Jason on 2017/6/8.
 */

public class BedtimeDatabaseHelper {

    private Realm mRealm;
    private Context mContext;

    public BedtimeDatabaseHelper(Context context) {
        mRealm = Realm.getDefaultInstance();
        mContext = context;
    }

    public Observable<Boolean> add(final BedtimeModel object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(object);
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> update(final BedtimeModel object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mRealm.copyToRealmOrUpdate(object);
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).

                subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> remove(final int presetId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final BedtimeModel bedtimeModel = mRealm.where(BedtimeModel.class)
                        .equalTo(mContext.getString(R.string.database_id), presetId).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (bedtimeModel != null) {
                            bedtimeModel.deleteFromRealm();
                            e.onNext(true);
                        } else {
                            e.onNext(true);
                        }
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());

    }

    public Observable<BedtimeModel> get(final int presetId) {
        return Observable.create(new ObservableOnSubscribe<BedtimeModel>() {
            @Override
            public void subscribe(ObservableEmitter<BedtimeModel> e) throws Exception {
                BedtimeModel bedtimeModel = mRealm.where(BedtimeModel.class)
                        .equalTo(mContext.getString(R.string.database_id), presetId).findFirst();
                if (bedtimeModel != null) {
                    e.onNext(mRealm.copyFromRealm(bedtimeModel));
                    e.onComplete();
                }
            }
        });
    }

    public Observable<List<BedtimeModel>> getAll() {
        return Observable.create(new ObservableOnSubscribe<List<BedtimeModel>>() {
            @Override
            public void subscribe(ObservableEmitter<List<BedtimeModel>> e) throws Exception {
                RealmResults<BedtimeModel> allBedtime = mRealm.where(BedtimeModel.class).findAll();
                if (allBedtime != null) {
                    e.onNext(mRealm.copyFromRealm(allBedtime));
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
