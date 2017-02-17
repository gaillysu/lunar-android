package com.medcorp.network.validic.request.routine;

import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.validic.model.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.network.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetMoreRoutineRecordsRequest extends BaseRequest<ValidicReadMoreRoutineRecordsModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;
    private String   start_timestamp;
    private String   end_timestamp;
    private int      page;

    public GetMoreRoutineRecordsRequest(String organizationId, String organizationTokenKey, String validicUserId,String start_timestamp,String end_timestamp,int page) {
        super(ValidicReadMoreRoutineRecordsModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
        this.page = page;
    }

    @Override
    public ValidicReadMoreRoutineRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreRoutineRecordsRequest(organizationId, validicUserId, organizationTokenKey,start_timestamp,end_timestamp,1,page);
    }
}
