package com.medcorp.network.validic.request.sleep;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.validic.model.AddSleepRecordRequestObject;
import com.medcorp.network.validic.model.ValidicSleepRecord;
import com.medcorp.network.validic.model.ValidicSleepRecordModel;
import com.medcorp.network.validic.retrofit.Validic;

/**
 * Created by med on 16/3/23.
 */
public class AddSleepRecordRequest extends BaseRequest<ValidicSleepRecordModel,Validic> implements BaseRequest.BaseRetroRequestBody<AddSleepRecordRequestObject> {

    private ValidicSleepRecord record;
    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public AddSleepRecordRequest(ValidicSleepRecord record,String organizationId,String organizationTokenKey,String validicUserId) {
        super(ValidicSleepRecordModel.class, Validic.class);
        this.record = record;
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public AddSleepRecordRequestObject buildRequestBody() {
        AddSleepRecordRequestObject object = new AddSleepRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        object.setSleep(record);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicSleepRecordModel loadDataFromNetwork() throws Exception {
        return getService().addSleepRecordRequest(buildRequestBody(), organizationId, validicUserId,"application/json");
    }
}
