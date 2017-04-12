package com.medcorp.lunar.network.modle.request;

import com.medcorp.lunar.network.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class VerifyEmailRequest implements BaseRequest {
    private UserBean user;

    public VerifyEmailRequest(String user_id) {
        this.user = new UserBean(user_id);
    }

    private class UserBean {

        private String id;

        public UserBean(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
