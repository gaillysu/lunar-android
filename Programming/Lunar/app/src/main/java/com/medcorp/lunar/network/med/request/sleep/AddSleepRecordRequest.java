package com.medcorp.lunar.network.med.request.sleep;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.med.model.MedSleepRecord;
import com.medcorp.lunar.network.med.model.MedSleepRecordModel;
import com.medcorp.lunar.network.med.model.MedSleepRecordObject;
import com.medcorp.lunar.network.med.model.MedSleepRecordParameters;
import com.medcorp.lunar.network.med.retrofit.MedCorp;


/**
 * Created by med on 16/3/23.
 */
public class AddSleepRecordRequest extends BaseRequest<MedSleepRecordModel,MedCorp> implements BaseRequest.BaseRetroRequestBody<MedSleepRecordObject> {

    private MedSleepRecord record;
    private String   organizationTokenKey;

    public AddSleepRecordRequest(MedSleepRecord record,String organizationTokenKey) {
        super(MedSleepRecordModel.class, MedCorp.class);
        this.record = record;
        this.organizationTokenKey = organizationTokenKey;
    }

    @Override
    public MedSleepRecordObject buildRequestBody() {
        MedSleepRecordObject object = new MedSleepRecordObject();
        object.setToken(organizationTokenKey);
        MedSleepRecordParameters parameters = new MedSleepRecordParameters();
        parameters.setSleep(record);
        object.setParams(parameters);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public MedSleepRecordModel loadDataFromNetwork() throws Exception {
        return getService().sleepCreate(buildRequestBody(),buildAuthorization(),"application/json");
    }
}
