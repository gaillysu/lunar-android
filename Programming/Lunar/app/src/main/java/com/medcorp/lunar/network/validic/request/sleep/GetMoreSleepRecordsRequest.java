package com.medcorp.lunar.network.validic.request.sleep;


import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.validic.model.ValidicReadMoreSleepRecordsModel;
import com.medcorp.lunar.network.validic.retrofit.Validic;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetMoreSleepRecordsRequest extends BaseRequest<ValidicReadMoreSleepRecordsModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;
    private String   start_timestamp;
    private String   end_timestamp;
    private int      page;

    public GetMoreSleepRecordsRequest(String organizationId, String organizationTokenKey, String validicUserId,String   start_timestamp,String   end_timestamp,int page) {
        super(ValidicReadMoreSleepRecordsModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
        this.page = page;
    }

    @Override
    public ValidicReadMoreSleepRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreSleepRecordsRequest(organizationId, validicUserId, organizationTokenKey,start_timestamp,end_timestamp,1,page);
    }
}
