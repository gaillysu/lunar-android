package com.medcorp.lunar.network.model.response.weather;

/**
 * Created by med on 17/4/24.
 */

public class Forecast {
    long dt;
    Main main;
    Weather[] weather;
    Clouds clouds;
    Wind wind;
    Sys sys;
    String dt_txt;

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }


    public long getDt() {
        return dt;
    }

}
