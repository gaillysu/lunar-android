package com.medcorp.network.med.retrofit;


import com.medcorp.network.med.model.CreateUserModel;
import com.medcorp.network.med.model.CreateUserObject;
import com.medcorp.network.med.model.LoginUserModel;
import com.medcorp.network.med.model.LoginUserObject;
import com.medcorp.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.network.med.model.MedReadMoreSleepRecordsModel;
import com.medcorp.network.med.model.MedRoutineRecordModel;
import com.medcorp.network.med.model.MedRoutineRecordObject;
import com.medcorp.network.med.model.MedSleepRecordModel;
import com.medcorp.network.med.model.MedSleepRecordObject;
import com.medcorp.network.validic.model.ChangePasswordObject;
import com.medcorp.network.validic.model.RequestTokenBody;
import com.medcorp.network.validic.model.RequestTokenResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by med on 16/3/21.
 */
public interface MedCorp {
    @POST("/user/login")
    LoginUserModel userLogin(@Body LoginUserObject object, @Header("Authorization") String auth, @Header("Content-Type") String type);

    @POST("/user/create")
    CreateUserModel userCreate(@Body CreateUserObject object, @Header("Authorization") String auth, @Header("Content-Type") String type);

    @POST("/user/request_password_token")
    RequestTokenResponse requestToken(@Body RequestTokenBody body, @Header("Authorization") String auth, @Header("Content-Type") String type);

    @POST("/steps/create")
    MedRoutineRecordModel stepsCreate(@Body MedRoutineRecordObject object,@Header("Authorization") String auth, @Header("Content-Type") String type);

    @GET("/steps/user/{USER_ID}")
    MedReadMoreRoutineRecordsModel getMoreRoutineRecords(@Header("Authorization") String auth, @Header("Content-Type") String type, @Path("USER_ID") String userID, @Query("token") String token, @Query("start_date") long start_date, @Query("end_date") long end_date);

    @POST("/sleep/create")
    MedSleepRecordModel sleepCreate(@Body MedSleepRecordObject object, @Header("Authorization") String auth, @Header("Content-Type") String type);

    @GET("/sleep/user/{USER_ID}")
    MedReadMoreSleepRecordsModel getMoreSleepRecords(@Header("Authorization") String auth, @Header("Content-Type") String type, @Path("USER_ID") String userID, @Query("token") String token, @Query("start_date") long start_date, @Query("end_date") long end_date);

    @POST("/user/forget_password")
    LoginUserModel forgetPassword(@Body ChangePasswordObject object,@Header("Authorization") String auth,@Header("Content-Type") String type);



}
