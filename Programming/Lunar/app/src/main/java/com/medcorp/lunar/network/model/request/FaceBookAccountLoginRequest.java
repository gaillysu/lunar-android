package com.medcorp.lunar.network.model.request;

import com.medcorp.lunar.network.model.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class FaceBookAccountLoginRequest implements BaseRequest {

    private UserBean user;

    public FaceBookAccountLoginRequest(String email, String facebook_id) {
        this.user = new UserBean(email, facebook_id);
    }

    private class UserBean {
        private String email;
        private String facebook_id;

        public UserBean(String email, String facebook_id) {
            this.email = email;
            this.facebook_id = facebook_id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFacebook_id() {
            return facebook_id;
        }

        public void setFacebook_id(String facebook_id) {
            this.facebook_id = facebook_id;
        }
    }
}
