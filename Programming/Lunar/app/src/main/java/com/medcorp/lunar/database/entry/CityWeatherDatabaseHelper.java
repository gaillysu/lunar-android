package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.google.gson.Gson;
import com.medcorp.lunar.R;
import com.medcorp.lunar.model.CityWeather;
import com.medcorp.lunar.network.model.response.weather.Forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;

/**
 * Created by med on 17/6/2.
 */

public class CityWeatherDatabaseHelper {
    private Realm realm;
    private boolean isNull;
    private Context context;

    public CityWeatherDatabaseHelper(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
    }

    public Observable<Boolean> update(final CityWeather object)
    {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        CityWeather cityWeather = realm.where(CityWeather.class).equalTo(context.getString(R.string.cityName), object.getCityName()).findFirst();
                        if(cityWeather==null) {
                            cityWeather = realm.createObject(CityWeather.class);
                        }
                        cityWeather.setCityName(object.getCityName());
                        cityWeather.setWeatherData(object.getWeatherData());
                        e.onNext(true);
                        e.onComplete();
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public void remove(final String cityName) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CityWeather cityWeather = realm.where(CityWeather.class).equalTo(context.getString(R.string.cityName), cityName).findFirst();
                if(cityWeather!=null) {
                    cityWeather.deleteFromRealm();
                }
            }
        });
    }

    public Observable<List<Forecast>> get(final String cityName) {
        return Observable.create(new ObservableOnSubscribe<List<Forecast>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Forecast>> e) throws Exception {
                List<Forecast> forecastList = new ArrayList<>();
                CityWeather cityWeather = realm.where(CityWeather.class).equalTo(context.getString(R.string.cityName), cityName).findFirst();
                if(cityWeather!=null) {
                    String string = cityWeather.getWeatherData();
                    try {
                        JSONArray jsonArray = new JSONArray(string);
                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Forecast forecast = new Gson().fromJson(jsonObject.toString(),Forecast.class);
                            forecastList.add(forecast);
                        }
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
                e.onNext(forecastList);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

}
