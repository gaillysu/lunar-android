package com.medcorp.lunar.network_new.httpmanage;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.R;
import com.medcorp.lunar.network_new.modle.base.BaseModel;
import com.medcorp.lunar.network_new.modle.base.BaseRequest;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by DengGang on 2017/1/19.
 *
 */

public class HttpManager {

    private static volatile HttpManager httpManager = null;
    private static Retrofit apiRetrofit;
    private Context mContext;

    private HttpManager(Context mContext) {
        this.mContext = mContext;
        initRetrofit();
    }


    public static HttpManager getInstance(Context context) {
        if (httpManager == null) {
            synchronized (HttpManager.class) {
                if (httpManager == null) {
                    httpManager = new HttpManager(context);
                }
            }
        }
        return httpManager;
    }

    private void initRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .authenticator(authenticator)
                .addInterceptor(mHeaderInterceptor)
                .addInterceptor(mLogInterceptor)
                .build();

        apiRetrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.network_base_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

    }

    public HttpApi createApiService() {
        return apiRetrofit.create(HttpApi.class);
    }

    public <T> void toSubscribe(Context context,Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(s);
        SubscriberManager.getInstance().addSubscription(context, s);
    }

    Authenticator authenticator = new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            String credential = Credentials.basic(mContext.getString(R.string.network_authenticator_name)
                    , mContext.getString(R.string.network_authenticator_password));
            return response.request().newBuilder().header(mContext.getString(R.string.network_header_authenticator_name)
                    , credential).build();
        }
    };

    Interceptor mLogInterceptor = new HttpLoggingInterceptor(
            new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("jason", "http-message-->>" + message);
                }
            }).setLevel(HttpLoggingInterceptor.Level.BODY);

    Interceptor mHeaderInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            request = request.newBuilder().addHeader(mContext.getString(R.string.network_header_authenticator_name)
                    , mContext.getString(R.string.network_header_authenticator_value)).build();
            return chain.proceed(request);

        }
    };

    public static <T extends BaseRequest> RequestBody createRequestBody(String token, BaseRequest params){
        BaseModel<BaseRequest> baseRequestModel = new BaseModel<>();
        baseRequestModel.setToken(token);
        baseRequestModel.setParams(params);
        return  RequestBody.create(
                MediaType.parse("application/json"), new Gson().toJson(baseRequestModel));
    }
}