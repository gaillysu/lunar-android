package com.medcorp.lunar.model;

import io.realm.RealmObject;

/**
 * Created by med on 17/6/2.
 */

public class CityWeather extends RealmObject {

    private String cityName;
    private String weatherData;

    public CityWeather()
    {

    }

    public CityWeather(String cityName,String weatherData)
    {
        this.cityName = cityName;
        this.weatherData = weatherData;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(String weatherData) {
        this.weatherData = weatherData;
    }
}
