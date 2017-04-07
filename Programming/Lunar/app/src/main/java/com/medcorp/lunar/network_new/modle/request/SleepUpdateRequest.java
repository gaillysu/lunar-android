package com.medcorp.lunar.network_new.modle.request;

import com.medcorp.lunar.network_new.modle.base.BaseRequest;

/**
 * Created by Jason on 2017/4/7.
 */

public class SleepUpdateRequest implements BaseRequest {

    /**
     * sleep : {"id":"{{uid_example}}","deep_sleep":"{{sleep_example}}","light_sleep":"{{sleep_example}}","wake_time":"{{sleep_example}}","date":"{{date_example}}"}
     */

    private SleepBean sleep;

    public SleepUpdateRequest(String id, String deep_sleep, String light_sleep, String wake_time, String date) {
        this.sleep = new SleepBean(id,deep_sleep,light_sleep,wake_time,date);
    }

    private class SleepBean {
        /**
         * id : {{uid_example}}
         * deep_sleep : {{sleep_example}}
         * light_sleep : {{sleep_example}}
         * wake_time : {{sleep_example}}
         * date : {{date_example}}
         */

        private String id;
        private String deep_sleep;
        private String light_sleep;
        private String wake_time;
        private String date;

       public SleepBean(String id, String deep_sleep, String light_sleep, String wake_time, String date) {
           this.id = id;
           this.deep_sleep = deep_sleep;
           this.light_sleep = light_sleep;
           this.wake_time = wake_time;
           this.date = date;
       }

       public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
