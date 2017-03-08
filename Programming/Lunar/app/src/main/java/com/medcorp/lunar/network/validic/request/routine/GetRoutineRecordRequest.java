package com.medcorp.lunar.network.validic.request.routine;


import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.validic.model.ValidicReadRoutineRecordModel;
import com.medcorp.lunar.network.validic.retrofit.Validic;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetRoutineRecordRequest extends BaseRequest<ValidicReadRoutineRecordModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;
    private String   validicRecordId;

    public GetRoutineRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId) {
        super(ValidicReadRoutineRecordModel.class,Validic.class);

        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }

    @Override
    public ValidicReadRoutineRecordModel loadDataFromNetwork() throws Exception {
        return getService().getRoutineRecordRequest(organizationId, validicUserId, validicRecordId, organizationTokenKey);
    }
}
