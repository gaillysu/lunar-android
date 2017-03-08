package com.medcorp.lunar.network.validic.service;

import com.medcorp.lunar.network.validic.retrofit.Validic;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RestAdapter;


/**
 * Created by Karl on 3/15/16.
 */
public class BaseValidicRetroService extends RetrofitGsonSpiceService {

    private final static String BASE_URL = "https://api.validic.com/v1";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(Validic.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setConverter(getConverter())
                .setEndpoint(getServerUrl());
        return builder;
    }
}
