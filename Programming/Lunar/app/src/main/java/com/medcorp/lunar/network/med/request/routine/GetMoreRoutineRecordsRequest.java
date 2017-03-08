package com.medcorp.lunar.network.med.request.routine;

import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.lunar.network.med.retrofit.MedCorp;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetMoreRoutineRecordsRequest extends BaseRequest<MedReadMoreRoutineRecordsModel,MedCorp> {

    private String   organizationTokenKey;
    private String   userID;
    private long   start_timestamp;
    private long   end_timestamp;

    public GetMoreRoutineRecordsRequest(String organizationTokenKey, String userID,long start_timestamp,long end_timestamp) {
        super(MedReadMoreRoutineRecordsModel.class,MedCorp.class);
        this.organizationTokenKey = organizationTokenKey;
        this.userID = userID;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
    }

    @Override
    public MedReadMoreRoutineRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreRoutineRecords(buildAuthorization(),"application/json",userID,organizationTokenKey,start_timestamp,end_timestamp);
    }
}
