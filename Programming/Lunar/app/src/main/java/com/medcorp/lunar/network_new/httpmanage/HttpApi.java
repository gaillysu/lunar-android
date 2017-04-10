package com.medcorp.lunar.network_new.httpmanage;

import com.medcorp.lunar.network_new.modle.response.ChangePasswordResponse;
import com.medcorp.lunar.network_new.modle.response.CheckEmailResponse;
import com.medcorp.lunar.network_new.modle.response.CheckWeChatAccountResponse;
import com.medcorp.lunar.network_new.modle.response.CreateMultiStepsRespnse;
import com.medcorp.lunar.network_new.modle.response.CreateStepsResponse;
import com.medcorp.lunar.network_new.modle.response.CreateWeChatAccountResponse;
import com.medcorp.lunar.network_new.modle.response.DeleteUserAccountResponse;
import com.medcorp.lunar.network_new.modle.response.RegisterNewAccountResponse;
import com.medcorp.lunar.network_new.modle.response.RequestForgotPasswordResponse;
import com.medcorp.lunar.network_new.modle.response.SleepCreateResponse;
import com.medcorp.lunar.network_new.modle.response.SleepUpdateResponse;
import com.medcorp.lunar.network_new.modle.response.StepsUpdateResponse;
import com.medcorp.lunar.network_new.modle.response.UpdateAccountInformationResponse;
import com.medcorp.lunar.network_new.modle.response.UserLoginResponse;
import com.medcorp.lunar.network_new.modle.response.VerifyEmailResponse;
import com.medcorp.lunar.network_new.modle.response.WeChatLoginResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by DengGang on 2017/1/19.
 */

public interface HttpApi {


    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/create")
    Observable<RegisterNewAccountResponse> registerNewAccount(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @PUT("user/update")
    Observable<UpdateAccountInformationResponse> updateInformation(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @retrofit2.http.HTTP(method = "DELETE", path = "user/delete", hasBody = true)
    Observable<DeleteUserAccountResponse> deleteAccount(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/check_email")
    Observable<CheckEmailResponse> checkAccount(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/request_password_token")
    Observable<RequestForgotPasswordResponse> obtainPasswordToken(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/forget_password")
    Observable<ChangePasswordResponse> changePassword(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/login")
    Observable<UserLoginResponse> userLogin(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/resend_email_token")
    Observable<VerifyEmailResponse> verfiyEmail(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/wechat/login")
    Observable<WeChatLoginResponse> weChatLogin(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/wechat")
    Observable<CreateWeChatAccountResponse> createWeChatAccount(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/wechat/check")
    Observable<CheckWeChatAccountResponse> checkWeChatAccount(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("steps/create")
    Observable<CreateStepsResponse> createSteps(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @PUT("steps/update")
    Observable<StepsUpdateResponse> updateSteps(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("sleep/create")
    Observable<SleepCreateResponse> createSleep(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @PUT("sleep/update")
    Observable<SleepUpdateResponse> updateSleep(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @PUT("steps/create")
    Observable<CreateMultiStepsRespnse> createMultiSteps(@Body RequestBody body);


    @Headers({"Content-type:application/json;charset=UTF-8"})
    @GET("sleep/user/{uid_example}?token={key}&start_date= {timestamp_example}&end_date= {timestamp2_example}")
    Observable<> obtainMoreSleep(@Path("uid_example") String id,@Path("key") String key,@Path("timestamp_example") long startTime, @Path("timestamp2_example") long endTime);



}
