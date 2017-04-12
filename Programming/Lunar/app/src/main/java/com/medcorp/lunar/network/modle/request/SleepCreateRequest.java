package com.medcorp.lunar.network.modle.request;

import com.medcorp.lunar.network.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/7.
 */

public class SleepCreateRequest implements BaseRequest {

    private SleepBean sleep;

    public SleepCreateRequest(String uid, String deep_sleep, String light_sleep, String wake_time, String date) {
        this.sleep = new SleepBean(uid,deep_sleep,light_sleep,wake_time,date);
    }

    private class SleepBean {
        /**
         * uid : {{uid_example}}
         * deep_sleep : {{sleep_example}}
         * light_sleep : {{sleep_example}}
         * wake_time : {{sleep_example}}
         * date : {{date_example}}
         */

        private String uid;
        private String deep_sleep;
        private String light_sleep;
        private String wake_time;
        private String date;

        public SleepBean(String uid, String deep_sleep, String light_sleep, String wake_time, String date) {
            this.uid = uid;
            this.deep_sleep = deep_sleep;
            this.light_sleep = light_sleep;
            this.wake_time = wake_time;
            this.date = date;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getDeep_sleep() {
            return deep_sleep;
        }

        public void setDeep_sleep(String deep_sleep) {
            this.deep_sleep = deep_sleep;
        }

        public String getLight_sleep() {
            return light_sleep;
        }

        public void setLight_sleep(String light_sleep) {
            this.light_sleep = light_sleep;
        }

        public String getWake_time() {
            return wake_time;
        }

        public void setWake_time(String wake_time) {
            this.wake_time = wake_time;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
