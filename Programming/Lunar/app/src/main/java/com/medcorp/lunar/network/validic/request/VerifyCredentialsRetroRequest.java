package com.medcorp.lunar.network.validic.request;


import com.medcorp.lunar.network.base.BaseRequest;
import com.medcorp.lunar.network.validic.model.VerifyCredentialModel;
import com.medcorp.lunar.network.validic.retrofit.Validic;

/**
 * Created by Karl on 3/16/16.
 */
public class VerifyCredentialsRetroRequest extends BaseRequest<VerifyCredentialModel, Validic> {

    private String organization;
    private String token;

    public VerifyCredentialsRetroRequest(String organization, String token) {
        super(VerifyCredentialModel.class, Validic.class);
        this.organization = organization;
        this.token = token;
    }

    @Override
    public VerifyCredentialModel loadDataFromNetwork() throws Exception {
        return getService().verifyCredential(organization,token);
    }
}
