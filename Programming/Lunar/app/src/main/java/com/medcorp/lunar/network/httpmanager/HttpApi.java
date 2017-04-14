package com.medcorp.lunar.network.httpmanager;

import com.medcorp.lunar.network.model.response.ChangePasswordResponse;
import com.medcorp.lunar.network.model.response.CheckEmailResponse;
import com.medcorp.lunar.network.model.response.CheckWeChatAccountResponse;
import com.medcorp.lunar.network.model.response.CreateFacebookAccountResponse;
import com.medcorp.lunar.network.model.response.CreateMultiStepsRespnse;
import com.medcorp.lunar.network.model.response.CreateStepsResponse;
import com.medcorp.lunar.network.model.response.CreateWeChatAccountResponse;
import com.medcorp.lunar.network.model.response.DeleteUserAccountResponse;
import com.medcorp.lunar.network.model.response.FacebookLoginResponse;
import com.medcorp.lunar.network.model.response.ObtainMoreSleepResponse;
import com.medcorp.lunar.network.model.response.ObtainMoreStepsResponse;
import com.medcorp.lunar.network.model.response.RegisterNewAccountResponse;
import com.medcorp.lunar.network.model.response.RequestForgotPasswordResponse;
import com.medcorp.lunar.network.model.response.SleepCreateResponse;
import com.medcorp.lunar.network.model.response.SleepUpdateResponse;
import com.medcorp.lunar.network.model.response.StepsUpdateResponse;
import com.medcorp.lunar.network.model.response.UpdateAccountInformationResponse;
import com.medcorp.lunar.network.model.response.UserLoginResponse;
import com.medcorp.lunar.network.model.response.VerifyEmailResponse;
import com.medcorp.lunar.network.model.response.WeChatLoginResponse;

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
    @GET("steps/user/{uid_example}")
    Observable<ObtainMoreStepsResponse> obtainMoreSteps(@Path("uid_example") String userID, @Query("key") String token,
                                                        @Query("timestamp_example") long start_date,
                                                        @Query("timestamp2_example") long end_date);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @GET("sleep/user/{uid_example}")
    Observable<ObtainMoreSleepResponse> obtainMoreSleep(@Path("uid_example") String userID, @Query("key") String token,
                                                        @Query("timestamp_example") long start_date,
                                                        @Query("timestamp2_example") long end_date);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/facebook")
    Observable<CreateFacebookAccountResponse> createFacebookAccount(@Body RequestBody body);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("user/facebook/login")
    Observable<FacebookLoginResponse> facebookLogin(@Body RequestBody body);

}
