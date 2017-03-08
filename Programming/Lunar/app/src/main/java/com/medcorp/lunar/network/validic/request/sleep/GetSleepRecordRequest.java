package com.medcorp.lunar.network.validic.request.sleep;


import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.validic.model.ValidicReadSleepRecordModel;
import com.medcorp.lunar.network.validic.retrofit.Validic;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetSleepRecordRequest extends BaseRequest<ValidicReadSleepRecordModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;
    private String   validicRecordId;

    public GetSleepRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId) {
        super(ValidicReadSleepRecordModel.class,Validic.class);

        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }

    @Override
    public ValidicReadSleepRecordModel loadDataFromNetwork() throws Exception {
        return getService().getSleepRecordRequest(organizationId, validicUserId, validicRecordId, organizationTokenKey);
    }
}
