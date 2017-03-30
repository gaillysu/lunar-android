package com.medcorp.lunar.network.med.request.user;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.med.model.CheckWeChatObject;
import com.medcorp.lunar.network.med.model.CheckWeChatParams;
import com.medcorp.lunar.network.med.model.CreateWeChatUserModel;
import com.medcorp.lunar.network.med.model.UserWeChatInfo;
import com.medcorp.lunar.network.med.retrofit.MedCorp;


/**
 * Created by Jason on 2017/3/6.
 */

public class CreateWeChatRequest extends BaseRequest<CreateWeChatUserModel, MedCorp>
        implements BaseRequest.BaseRetroRequestBody<CheckWeChatObject> {

    private String  token;
    private UserWeChatInfo info;


    public CreateWeChatRequest(UserWeChatInfo info,String token) {
        super(CreateWeChatUserModel.class, MedCorp.class);
        this.token = token;
        this.info = info;
    }

    @Override
    public CheckWeChatObject buildRequestBody() {
        CheckWeChatObject obj = new CheckWeChatObject();
        obj.setToken(token);
        CheckWeChatParams params = new CheckWeChatParams();
        params.setUser(info);
        obj.setParams(params);
        Log.i("jason",new Gson().toJson(obj));
        return obj;
    }

    @Override
    public CreateWeChatUserModel loadDataFromNetwork() throws Exception {
        return getService().createWeChatUser(buildRequestBody(),buildAuthorization(),CONTENT_TYPE);
    }
}
