package com.medcorp.network.validic.request.routine;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.validic.model.RoutineModel;
import com.medcorp.network.validic.model.UpdateRoutineRecordRequestObject;
import com.medcorp.network.validic.model.ValidicRoutineRecordModel;
import com.medcorp.network.validic.retrofit.Validic;

/**
 * Created by gaillysu on 16/3/8.
 */
public class UpdateRoutineRecordRequest extends BaseRequest<ValidicRoutineRecordModel,Validic> implements BaseRequest.BaseRetroRequestBody<UpdateRoutineRecordRequestObject> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;
    private int steps;

    public UpdateRoutineRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId, int steps) {
        super(ValidicRoutineRecordModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
        this.steps = steps;
    }


    @Override
    public UpdateRoutineRecordRequestObject buildRequestBody() {
        UpdateRoutineRecordRequestObject object = new UpdateRoutineRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        RoutineModel stepsModel = new RoutineModel();
        stepsModel.setSteps(steps);
        object.setRoutine(stepsModel);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicRoutineRecordModel loadDataFromNetwork() throws Exception {
       return getService().updateRoutineRecordRequest(buildRequestBody(), organizationId, validicUserId, validicRecordId, "application/json");
    }
}
