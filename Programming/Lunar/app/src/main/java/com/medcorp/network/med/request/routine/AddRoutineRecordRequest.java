package com.medcorp.network.med.request.routine;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.med.model.MedRoutineRecord;
import com.medcorp.network.med.model.MedRoutineRecordModel;
import com.medcorp.network.med.model.MedRoutineRecordObject;
import com.medcorp.network.med.model.MedRoutineRecordParameters;
import com.medcorp.network.med.retrofit.MedCorp;
import com.medcorp.network.validic.model.AddRoutineRecordRequestObject;
import com.medcorp.network.validic.model.ValidicRoutineRecord;
import com.medcorp.network.validic.model.ValidicRoutineRecordModel;
import com.medcorp.network.validic.retrofit.Validic;

/**
 * Created by gaillysu on 16/3/8.
 */
public class AddRoutineRecordRequest extends BaseRequest<MedRoutineRecordModel,MedCorp> implements BaseRequest.BaseRetroRequestBody<MedRoutineRecordObject> {

    private MedRoutineRecord record;
    private String   organizationTokenKey;

    public AddRoutineRecordRequest(MedRoutineRecord record, String organizationTokenKey) {
        super(MedRoutineRecordModel.class,MedCorp.class);
        this.record = record;
        this.organizationTokenKey = organizationTokenKey;
    }
    @Override
    public MedRoutineRecordObject buildRequestBody() {
        MedRoutineRecordObject object = new MedRoutineRecordObject();
        object.setToken(organizationTokenKey);
        MedRoutineRecordParameters parameters = new MedRoutineRecordParameters();
        parameters.setSteps(record);
        object.setParams(parameters);
        Log.i(this.getClass().getSimpleName(),"object: "+new Gson().toJson(object));
        return object;
    }

    @Override
    public MedRoutineRecordModel loadDataFromNetwork() throws Exception {
        return getService().stepsCreate(buildRequestBody(),buildAuthorization(),"application/json");
    }
}
