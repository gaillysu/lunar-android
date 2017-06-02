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
}
