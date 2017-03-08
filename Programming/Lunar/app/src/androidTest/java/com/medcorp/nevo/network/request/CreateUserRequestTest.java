package com.medcorp.network.request;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.lunar.network.validic.manager.ValidicManager;
import com.medcorp.lunar.network.validic.model.CreateUserRequestObject;
import com.medcorp.lunar.network.validic.model.CreateUserRequestObjectUser;
import com.medcorp.lunar.network.validic.model.ValidicUser;
import com.medcorp.lunar.network.validic.request.user.CreateUserRetroRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by gaillysu on 16/3/8.
 */
public class CreateUserRequestTest extends AndroidTestCase {
    private static final String TAG = CreateUserRequestTest.class.getSimpleName();
    ValidicManager validicManager;
    CreateUserRetroRequest createUserRequest;
    //it comes from user 's input
    String pincode = "2364367";
    String uid = pincode;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validicManager = new ValidicManager(getContext());
        validicManager.startSpiceManager();
        CreateUserRequestObject object = new CreateUserRequestObject();
        object.setPin(pincode);
        object.setAccess_token(validicManager.getOrganizationToken());
        CreateUserRequestObjectUser user  = new CreateUserRequestObjectUser();
        user.setUid(uid);
        object.setUser(user);
        createUserRequest = new CreateUserRetroRequest(validicManager.getOrganizationID(),object);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        validicManager.stopSpiceManager();
    }
    public void testCreateUser()
    {
        validicManager.execute(createUserRequest, new RequestListener<ValidicUser>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(ValidicUser validicUser) {
                String result = new Gson().toJson(validicUser);
                Log.i(TAG, result);
            }
        });
    }
}
