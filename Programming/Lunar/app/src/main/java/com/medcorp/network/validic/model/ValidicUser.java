package com.medcorp.network.validic.model;


/**
 * Created by gaillysu on 16/3/8.
 */
public class ValidicUser{
        private String code;
        private String message;
        private ValidicUserBase user;
        private String application;

        public String getCode() {
                return code;
        }

        public void setCode(String code) {
                this.code = code;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public ValidicUserBase getUser() {
                return user;
        }

        public void setUser(ValidicUserBase user) {
                this.user = user;
        }

        public String getApplication() {
                return application;
        }

        public void setApplication(String application) {
                this.application = application;
        }
}
