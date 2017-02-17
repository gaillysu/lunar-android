package com.medcorp.event;

import com.medcorp.network.med.model.CreateUserModel;

/**
 * Created by karl-john on 17/5/16.
 */
public class SignUpEvent {

    public enum status{
        SUCCESS,FAILED
    }

    final private status signUpStatus;
    final private CreateUserModel createUserModel;

    public SignUpEvent(status signUpStatus, CreateUserModel createUserModel) {
        this.signUpStatus = signUpStatus;
        this.createUserModel = createUserModel;
    }

    public status getSignUpStatus() {
        return signUpStatus;
    }

    public CreateUserModel getCreateUserModel() {
        return createUserModel;
    }
}
