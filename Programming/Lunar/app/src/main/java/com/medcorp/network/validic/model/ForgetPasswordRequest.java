package com.medcorp.network.validic.model;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.med.model.LoginUserModel;
import com.medcorp.network.med.retrofit.MedCorp;

/**
 * Created by Jason on 2016/10/12.
 */

public class ForgetPasswordRequest extends BaseRequest<LoginUserModel,MedCorp>
        implements BaseRequest.BaseRetroRequestBody<ChangePasswordObject>{

    private String token;
    private ForgetPasswordModel forgetModel;
    public ForgetPasswordRequest(String token, ForgetPasswordModel forgetModel) {
        super(LoginUserModel.class, MedCorp.class);
        this.token = token;
        this.forgetModel = forgetModel;
    }

    @Override
    public ChangePasswordObject buildRequestBody() {
        ChangePasswordObject object = new ChangePasswordObject();
        object.setToken(token);
        ChangePasswordUserParameters params = new ChangePasswordUserParameters();
        params.setUser(forgetModel);
        object.setParams(params);
        Log.i(this.getClass().getSimpleName(),"forgetPassword:" + new Gson().toJson(object));
        return object;
    }

    @Override
    public LoginUserModel loadDataFromNetwork() throws Exception {
        return getService().forgetPassword(buildRequestBody(),buildAuthorization(),CONTENT_TYPE);
    }
}
