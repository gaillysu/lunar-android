package com.medcorp.network.base;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by Administrator on 2016/7/20.
 *
 */
public abstract class BaseRequest<T, R> extends RetrofitSpiceRequest<T, R> {
    public static final String CONTENT_TYPE = "application/json";

    public BaseRequest(Class<T> clazz, Class<R> retrofitInterfaceClass) {
        super(clazz, retrofitInterfaceClass);
    }

    //for HTTP "get"/"header", hasn't body, so needn't implement this function, others need do it.
    public interface BaseRetroRequestBody<B> {
        B buildRequestBody();
    }

    public String buildAuthorization() {
        // TODO add to strings/config
        String authorization = "Basic ";
        String username = "apps";
        String password = "med_app_development";
        return authorization + new String(new Base64().encode(new String(username + ":" + password).getBytes()));
    }
}
