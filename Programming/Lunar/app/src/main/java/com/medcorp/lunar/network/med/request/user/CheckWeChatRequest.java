package com.medcorp.lunar.network.med.request.user;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.med.model.CheckWeChatModel;
import com.medcorp.lunar.network.med.model.CheckWeChatObject;
import com.medcorp.lunar.network.med.model.CheckWeChatParams;
import com.medcorp.lunar.network.med.model.UserWeChatInfo;
import com.medcorp.lunar.network.med.retrofit.MedCorp;


/**
 * Created by Jason on 2017/3/6.
 */

public class CheckWeChatRequest extends BaseRequest<CheckWeChatModel, MedCorp>
        implements BaseRequest.BaseRetroRequestBody<CheckWeChatObject> {

    private String token;
    private UserWeChatInfo info;

    public CheckWeChatRequest(UserWeChatInfo info, String token) {
        super(CheckWeChatModel.class, MedCorp.class);
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
    public CheckWeChatModel loadDataFromNetwork() throws Exception {
        return getService().checkWeChat(buildRequestBody(), buildAuthorization(),CONTENT_TYPE);
    }
}
