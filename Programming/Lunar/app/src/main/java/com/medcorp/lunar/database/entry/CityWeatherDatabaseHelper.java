package com.medcorp.lunar.database.entry;

import android.content.Context;

import com.google.gson.Gson;
import com.medcorp.lunar.R;
import com.medcorp.lunar.model.CityWeather;
import com.medcorp.lunar.model.HourlyForecast;
import com.medcorp.lunar.network.model.response.weather.Forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public Observable<Boolean> update(final String cityName, final Forecast[] forecasts)
    {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        CityWeather cityWeather = realm.where(CityWeather.class).equalTo(context.getString(R.string.cityName), cityName).findFirst();
                        if(cityWeather==null) {
                            cityWeather = realm.createObject(CityWeather.class);
                        }
                        else {
                            cityWeather.getWeatherData().clear();
                        }
                        cityWeather.setCityName(cityName);
                        for(Forecast forecast:forecasts)
                        {
                            HourlyForecast hourlyForecast = new HourlyForecast();
                            hourlyForecast.setDt(forecast.getDt());
                            hourlyForecast.setTemp(forecast.getMain().getTemp());
                            hourlyForecast.setId(forecast.getWeather()[0].getId());
                            hourlyForecast.setMain(forecast.getWeather()[0].getMain());
                            cityWeather.getWeatherData().add(realm.copyToRealm(hourlyForecast));
                        }
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

    public Observable<List<HourlyForecast>> get(final String cityName) {
        return Observable.create(new ObservableOnSubscribe<List<HourlyForecast>>() {
            @Override
            public void subscribe(ObservableEmitter<List<HourlyForecast>> e) throws Exception {
                List<HourlyForecast> forecastList = new ArrayList<>();
                CityWeather cityWeather = realm.where(CityWeather.class).equalTo(context.getString(R.string.cityName), cityName).findFirst();
                if(cityWeather!=null) {
                    for(int i=0;i<cityWeather.getWeatherData().size();i++){
                        forecastList.add(cityWeather.getWeatherData().get(i));
                    }
                }
                e.onNext(forecastList);
                e.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

}
