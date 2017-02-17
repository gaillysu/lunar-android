package com.medcorp.network.validic.request.sleep;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.validic.model.DeleteSleepRecordRequestObject;
import com.medcorp.network.validic.model.ValidicDeleteSleepRecordModel;
import com.medcorp.network.validic.retrofit.Validic;

/**
 * Created by gaillysu on 16/3/8.
 */
public class DeleteSleepRecordRequest extends BaseRequest<ValidicDeleteSleepRecordModel,Validic> implements BaseRequest.BaseRetroRequestBody<DeleteSleepRecordRequestObject> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;

    public DeleteSleepRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId) {
        super(ValidicDeleteSleepRecordModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }


    @Override
    public DeleteSleepRecordRequestObject buildRequestBody() {
        DeleteSleepRecordRequestObject object = new DeleteSleepRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicDeleteSleepRecordModel loadDataFromNetwork() throws Exception {
       return getService().deleteSleepRecordRequest(buildRequestBody(), organizationId, validicUserId, validicRecordId, "application/json");
    }
}
