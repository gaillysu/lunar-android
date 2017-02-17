package com.medcorp.network.validic.request.user;

import com.medcorp.network.validic.model.CreateUserRequestObject;
import com.medcorp.network.validic.model.ValidicUser;
import com.medcorp.network.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Karl on 3/16/16.
 */
public class CreateUserRetroRequest extends RetrofitSpiceRequest<ValidicUser, Validic> {

    private String organization;
    private CreateUserRequestObject object;

    public CreateUserRetroRequest(String organization, CreateUserRequestObject object) {
        super(ValidicUser.class, Validic.class);
        this.organization = organization;
        this.object = object;
    }

    @Override
    public ValidicUser loadDataFromNetwork() throws Exception {
        return getService().createUserRequest(object,organization,"application/json");
    }
}
