package com.medcorp.lunar.network.med.request.routine;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.med.model.MedRoutineRecord;
import com.medcorp.lunar.network.med.model.MedRoutineRecordModel;
import com.medcorp.lunar.network.med.model.MedRoutineRecordObject;
import com.medcorp.lunar.network.med.model.MedRoutineRecordParameters;
import com.medcorp.lunar.network.med.retrofit.MedCorp;


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
