package com.medcorp.lunar.model;


import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by med on 17/6/2.
 */

public class CityWeather extends RealmObject {
    private String cityName;
    private RealmList<HourlyForecast> weatherData;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public RealmList<HourlyForecast> getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(RealmList<HourlyForecast> weatherData) {
        this.weatherData = weatherData;
    }
}
