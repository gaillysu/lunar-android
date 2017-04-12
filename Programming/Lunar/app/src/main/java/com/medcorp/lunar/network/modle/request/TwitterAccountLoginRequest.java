package com.medcorp.lunar.network.modle.request;

import com.medcorp.lunar.network.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class TwitterAccountLoginRequest implements BaseRequest {

    private UserBean user;

    public TwitterAccountLoginRequest(String email, String twitter_id) {
        this.user = new UserBean(email, twitter_id);
    }

    private class UserBean {
        private String email;
        private String twitter_id;

        public UserBean(String email, String twitter_id) {
            this.email = email;
            this.twitter_id = twitter_id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTwitter_id() {
            return twitter_id;
        }

        public void setTwitter_id(String twitter_id) {
            this.twitter_id = twitter_id;
        }
    }
}
