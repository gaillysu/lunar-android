package com.medcorp.lunar.network.httpmanager;

import com.medcorp.lunar.network.model.response.weather.GetForecastResponse;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface WeatherApi {
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @GET("/data/2.5/forecast")
    Observable<GetForecastResponse> getForecast(@Query("q") String cityName,@Query("appid") String apiKey);
}
