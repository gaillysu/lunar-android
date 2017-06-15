package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.Solar;

import java.util.ArrayList;
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
    private Context mContext;

    public SolarDatabaseHelper(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public Observable<Boolean> add(final Solar object) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Solar solar = realm.createObject(Solar.class);
                        solar.setId(object.getId());
                        solar.setTotalHarvestingTime(object.getTotalHarvestingTime());
                        solar.setHourlyHarvestingTime(object.getHourlyHarvestingTime());
                        solar.setCreatedDate(object.getCreatedDate());
                        solar.setDate(object.getDate());
                        solar.setUserId(object.getUserId());
                        solar.setGoal(object.getGoal());
                        e.onNext(true);
                        e.onComplete();
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
                        Solar solar = mRealm.where(Solar.class).equalTo(mContext.getString(R.string.database_id)
                                , object.getId())
                                .equalTo(mContext.getString(R.string.date), object.getDate()).findFirst();
                        if (solar != null) {
                            solar.setId(object.getId());
                            solar.setTotalHarvestingTime(object.getTotalHarvestingTime());
                            solar.setHourlyHarvestingTime(object.getHourlyHarvestingTime());
                            solar.setCreatedDate(object.getCreatedDate());
                            solar.setDate(object.getDate());
                            solar.setUserId(object.getUserId());
                            solar.setGoal(object.getGoal());
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
                final Solar solar = mRealm.where(Solar.class).equalTo(mContext.getString(R.string.userId), userId)
                        .equalTo(mContext.getString(R.string.create_date), date).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        solar.deleteFromRealm();
                        e.onNext(true);
                        e.onComplete();
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
                Solar solar = mRealm.where(Solar.class).equalTo(mContext.getString(R.string.database_id), userId)
                        .equalTo(mContext.getString(R.string.date), date.getTime()).findFirst();
                if (solar != null) {
                    e.onNext(mRealm.copyFromRealm(solar));
                    e.onComplete();
                } else {
                    e.onNext(new Solar(new Date()));
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Solar>> getAll(final int userId) {
        return Observable.create(new ObservableOnSubscribe<List<Solar>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Solar>> e) throws Exception {
                List<Solar> solarList = mRealm.where(Solar.class)
                        .equalTo(mContext.getString(R.string.database_id), userId).findAll();
                if (solarList != null) {
                    e.onNext(mRealm.copyFromRealm(solarList));
                    e.onComplete();
                } else {
                    e.onNext(new ArrayList<Solar>());
                    e.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Solar>> getSolarDatas(final int userId, final List<Date> dates) {
        return Observable.create(new ObservableOnSubscribe<List<Solar>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Solar>> e) throws Exception {
                List<Solar> solars = new ArrayList<>();
                for(Date date:dates){
                    Solar solar = mRealm.where(Solar.class).equalTo(mContext.getString(R.string.database_id), userId)
                            .equalTo(mContext.getString(R.string.date), date.getTime()).findFirst();
                    if(solar!=null){
                        solars.add(mRealm.copyFromRealm(solar));
                    }else{
                        Solar daySolar = new Solar(date);
                        daySolar.setUserId(userId);
                        daySolar.setDate(date.getTime());
                        solars.add(daySolar);
                    }
                }
                e.onNext(solars);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }


}
