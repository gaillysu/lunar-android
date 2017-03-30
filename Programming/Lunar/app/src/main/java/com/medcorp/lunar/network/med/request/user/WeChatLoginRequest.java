package com.medcorp.lunar.network.med.request.user;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.med.model.WeChatLogin;
import com.medcorp.lunar.network.med.model.WeChatLoginModel;
import com.medcorp.lunar.network.med.model.WeChatLoginObject;
import com.medcorp.lunar.network.med.model.WeChatLoginParams;
import com.medcorp.lunar.network.med.retrofit.MedCorp;


/**
 * Created by Jason on 2017/3/6.
 *
 */

public class WeChatLoginRequest extends BaseRequest<WeChatLoginModel, MedCorp>
        implements BaseRequest.BaseRetroRequestBody<WeChatLoginObject>{

    private WeChatLogin user;
    private String token;

    public WeChatLoginRequest(WeChatLogin user, String accessToken) {
        super(WeChatLoginModel.class,MedCorp.class);
        this.user = user;
        this.token = accessToken;
    }

    @Override
    public WeChatLoginObject buildRequestBody() {
        WeChatLoginObject obj = new WeChatLoginObject();
        obj.setToken(token);
        WeChatLoginParams params = new WeChatLoginParams();
        params.setUser(user);
        obj.setParams(params);
        Log.i("jason",new Gson().toJson(obj));
        return obj;
    }

    @Override
    public WeChatLoginModel loadDataFromNetwork() throws Exception {
        return getService().weChatLogin(buildRequestBody(),buildAuthorization(),CONTENT_TYPE);
    }
}
