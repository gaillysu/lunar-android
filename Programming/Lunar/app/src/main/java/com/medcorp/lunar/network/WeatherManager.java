package com.medcorp.lunar.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.R;
import com.medcorp.lunar.event.CityForecastChangedEvent;
import com.medcorp.lunar.network.httpmanager.HttpManager;
import com.medcorp.lunar.network.model.response.weather.Forecast;
import com.medcorp.lunar.network.model.response.weather.GetForecastResponse;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by med on 17/5/31.
 */

public class WeatherManager {
    private HttpManager httpManager;
    private Context context;

    public WeatherManager(Context context)
    {
        this.context = context;
        httpManager = HttpManager.getInstance(context);
    }

    /**
     *
     * @param cityName
     * send CityForecastChangedEvent to subscriber
     */
    public void getForecast(final String cityName) {

        Observable<GetForecastResponse> getForecastResponseObservable = httpManager.getWeatherApi().getForecast(
                cityName, context.getString(R.string.weather_api_key));

        httpManager.getWeatherForecast(getForecastResponseObservable,new Subscriber<GetForecastResponse>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("","onError: " + e.getLocalizedMessage());
                    }
                    @Override
                    public void onNext(GetForecastResponse getForecastResponse) {
                        //TODO save weather data to local file
                        //send weather data to subscriber
                        int currentHour = new DateTime().getHourOfDay();
                        int today = new DateTime().getDayOfMonth();
                        int index = 0;
                        int totalDataOfToday =0;
                        Set<String> todayData = new LinkedHashSet<>();
                        Calendar calendar = new GregorianCalendar();
                        long offset = calendar.getTimeZone().getRawOffset();
                        for(Forecast forecast:getForecastResponse.getList())
                        {
                            int day  = new DateTime(forecast.getDt()*1000-offset).getDayOfMonth();
                            if(day == today) {
                                totalDataOfToday++;
                                todayData.add(new Gson().toJson(forecast));
                            }
                        }
                        int forecastStartTime = new DateTime(getForecastResponse.getList()[0].getDt()*1000-offset).getHourOfDay();
                        index = ((currentHour - forecastStartTime)/3) % totalDataOfToday;
                        float temp = getForecastResponse.getList()[index].getMain().getTemp();
                        int id = getForecastResponse.getList()[index].getWeather()[0].getId();
                        String main = getForecastResponse.getList()[index].getWeather()[0].getMain();
                        EventBus.getDefault().post(new CityForecastChangedEvent(cityName, temp, id, main));
                    }
                }
            );
    }
}
