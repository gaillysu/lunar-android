package com.medcorp.lunar.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.medcorp.lunar.network.model.response.weather.Forecast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by med on 17/6/1.
 */

public class WeatherUtils {
    final static String CITYLIST = "CITYLIST";
    final static String SP_Name = "LUNAR_SP_NAME";

    //when user select/unselect a city, update the city list

    public static void addWeatherCity(Context context, String name)
    {
        if(name.contains(",")) {
            name = name.split(",")[0];
        }
        List<String> cities = getWeatherCities(context);
        if(!cities.contains(name)){
            SharedPreferences sp = context.getSharedPreferences(SP_Name,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =  sp.edit();
            ArrayList<String> cityList = new ArrayList<>(cities);
            cityList.add(name);
            editor.putString(CITYLIST,cityList.toString().replace("[","").replace("]",""));
            editor.apply();
        }
    }

    public static List<String> getWeatherCities(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(SP_Name,Context.MODE_PRIVATE);
        String cites = sp.getString(CITYLIST, new String());
        if(cites.isEmpty()) {
            return new ArrayList<String>();
        }
        String [] cityArray = cites.split(",");
        return Arrays.asList(cityArray);
    }

    public static void removeAllCities(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(SP_Name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sp.edit();
        editor.putString(CITYLIST,new String());
        editor.apply();
    }



    /*
    Set<String> allForecast, see@ GetForecastModel.Forecast to Json string
    every 3 hours, will has a Forecast record, so total of record for 5 days is less than 40
     */
    public static void saveCityWeather(Context context, String name, Set<String> allForecast)
    {
        SharedPreferences sp = context.getSharedPreferences(SP_Name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sp.edit();
        editor.putStringSet(name,allForecast);
        editor.apply();
    }

    public static List<Forecast> getCityWeather(Context context, String name)
    {
        SharedPreferences sp = context.getSharedPreferences(SP_Name,Context.MODE_PRIVATE);
        Set<String> records =  sp.getStringSet(name, new HashSet<String>());
        List<Forecast> weather = new ArrayList<>();
        for(String record:records)
        {
            Forecast forecast = new Gson().fromJson(record, Forecast.class);
            weather.add(forecast);
        }
        Comparator<Forecast> comparator = new Comparator<Forecast>() {
            public int compare(Forecast s1, Forecast s2) {
                if (s1.getDt() == s2.getDt()) {
                    return 0;
                } else if (s1.getDt() > s2.getDt()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };
        Collections.sort(weather,comparator);

        return weather;
    }

}
