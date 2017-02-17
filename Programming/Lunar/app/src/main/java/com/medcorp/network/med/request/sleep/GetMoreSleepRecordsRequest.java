package com.medcorp.network.med.request.sleep;

import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.med.model.MedReadMoreSleepRecordsModel;
import com.medcorp.network.med.retrofit.MedCorp;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetMoreSleepRecordsRequest extends BaseRequest<MedReadMoreSleepRecordsModel,MedCorp> {
    private String   organizationTokenKey;
    private String userID;
    private long   start_timestamp;
    private long   end_timestamp;

    public GetMoreSleepRecordsRequest(String organizationTokenKey, String userID,long start_timestamp,long end_timestamp) {
        super(MedReadMoreSleepRecordsModel.class,MedCorp.class);
        this.organizationTokenKey = organizationTokenKey;
        this.userID = userID;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
    }

    @Override
    public MedReadMoreSleepRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getMoreSleepRecords (organizationTokenKey,"application/json", userID,organizationTokenKey,start_timestamp,end_timestamp);
    }
}
