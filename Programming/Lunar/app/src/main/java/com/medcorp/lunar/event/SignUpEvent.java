package com.medcorp.lunar.event;


import com.medcorp.lunar.network_new.modle.response.RegisterNewAccountResponse;

/**
 * Created by karl-john on 17/5/16.
 */
public class SignUpEvent {

    public enum status{
        SUCCESS,FAILED
    }

    final private status signUpStatus;
    final private RegisterNewAccountResponse createUserModel;

    public SignUpEvent(status signUpStatus, RegisterNewAccountResponse createUserModel) {
        this.signUpStatus = signUpStatus;
        this.createUserModel = createUserModel;
    }

    public status getSignUpStatus() {
        return signUpStatus;
    }

    public RegisterNewAccountResponse getCreateUserModel() {
        return createUserModel;
    }
}
