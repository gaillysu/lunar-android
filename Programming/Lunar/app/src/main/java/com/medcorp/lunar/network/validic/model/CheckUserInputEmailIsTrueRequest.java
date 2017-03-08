package com.medcorp.lunar.network.validic.model;


import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.med.model.RequestTokenBodyParameters;
import com.medcorp.lunar.network.med.retrofit.MedCorp;

/**
 * Created by Administrator on 2016/7/20.
 */
public class CheckUserInputEmailIsTrueRequest extends BaseRequest< RequestTokenResponse, MedCorp>
        implements BaseRequest.BaseRetroRequestBody< RequestTokenBody> {

    private String token;
    private String email;

    public CheckUserInputEmailIsTrueRequest(String token, String email) {
        super( RequestTokenResponse.class, MedCorp.class);
        this.token = token;
        this.email = email;
    }

    @Override
    public  RequestTokenResponse loadDataFromNetwork() throws Exception {
        return getService().requestToken(buildRequestBody(), buildAuthorization(), CONTENT_TYPE);
    }

    @Override
    public  RequestTokenBody buildRequestBody() {
        RequestTokenBodyParameters parameters = new RequestTokenBodyParameters(email);
        return new  RequestTokenBody(token, parameters);
    }
}
