package com.medcorp.network.med.request.user;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.med.model.LoginUser;
import com.medcorp.network.med.model.LoginUserModel;
import com.medcorp.network.med.model.LoginUserObject;
import com.medcorp.network.med.model.LoginUserParameters;
import com.medcorp.network.med.retrofit.MedCorp;

/**
 * Created by gaillysu on 16/3/14.
 */
public class LoginUserRequest extends BaseRequest<LoginUserModel,MedCorp> implements BaseRequest.BaseRetroRequestBody<LoginUserObject> {

    private String token;
    private LoginUser user;

    public LoginUserRequest(LoginUser user, String token) {
        super(LoginUserModel.class, MedCorp.class);
        this.user  = user;
        this.token = token;
    }

    @Override
    public LoginUserModel loadDataFromNetwork() throws Exception {
        return getService().userLogin(buildRequestBody(), buildAuthorization(),CONTENT_TYPE);
    }

    @Override
    public LoginUserObject buildRequestBody() {
        LoginUserObject object = new LoginUserObject();
        object.setToken(token);
        LoginUserParameters parameters = new LoginUserParameters();
        parameters.setUser(user);
        object.setParams(parameters);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }
}

