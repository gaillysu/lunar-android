package com.medcorp.network.validic.model;


/**
 * Created by gaillysu on 16/3/8.
 */
public class ValidicUserBase{
        private String _id;
        private String uid;
        private String access_token;

        public String getUid() {
                return uid;
        }

        public void setUid(String uid) {
                this.uid = uid;
        }

        public String get_id() {
                return _id;
        }

        public void set_id(String _id) {
                this._id = _id;
        }

        public String getAccess_token() {
                return access_token;
        }

        public void setAccess_token(String access_token) {
                this.access_token = access_token;
        }
}
