package com.medcorp.lunar.network_new.modle.request;

import com.medcorp.lunar.network_new.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/5.
 */

public class DeleteUserAccountRequest implements BaseRequest {

    private UserBean user;

    public DeleteUserAccountRequest(int user_id) {
        this.user = new UserBean(user_id);
    }

    private class UserBean {
        private int id;

        public UserBean(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
