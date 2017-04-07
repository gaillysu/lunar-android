package com.medcorp.lunar.network.med.retrofit;


import com.medcorp.lunar.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.lunar.network.med.model.MedReadMoreSleepRecordsModel;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by med on 16/3/21.
 */
public interface MedCorp {

    @GET("/steps/user/{USER_ID}")
    MedReadMoreRoutineRecordsModel getMoreRoutineRecords(@Header("Authorization") String auth, @Header("Content-Type") String type, @Path("USER_ID") String userID, @Query("token") String token, @Query("start_date") long start_date, @Query("end_date") long end_date);
    @GET("/sleep/user/{USER_ID}")
    MedReadMoreSleepRecordsModel getMoreSleepRecords(@Header("Authorization") String auth, @Header("Content-Type") String type, @Path("USER_ID") String userID, @Query("token") String token, @Query("start_date") long start_date, @Query("end_date") long end_date);
}
