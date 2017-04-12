package com.medcorp.lunar.network.modle.response;

import com.medcorp.lunar.network.modle.base.BaseResponse;

/**
 * Created by Jason on 2017/4/7.
 */

public class SleepCreateResponse extends BaseResponse {

    private SleepBean sleep;

    public SleepBean getSleep() {
        return sleep;
    }

    public void setSleep(SleepBean sleep) {
        this.sleep = sleep;
    }

    public static class SleepBean {
        /**
         * id : 297
         * uid : 53
         * wake_time : [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
         * light_sleep : [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
         * deep_sleep : [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
         * date : {"date":"2000-01-01 00:00:00.000000","timezone_type":3,"timezone":"Europe/Amsterdam"}
         */

        private int id;
        private String uid;
        private String wake_time;
        private String light_sleep;
        private String deep_sleep;
        private DateBean date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getWake_time() {
            return wake_time;
        }

        public void setWake_time(String wake_time) {
            this.wake_time = wake_time;
        }

        public String getLight_sleep() {
            return light_sleep;
        }

        public void setLight_sleep(String light_sleep) {
            this.light_sleep = light_sleep;
        }

        public String getDeep_sleep() {
            return deep_sleep;
        }

        public void setDeep_sleep(String deep_sleep) {
            this.deep_sleep = deep_sleep;
        }

        public DateBean getDate() {
            return date;
        }

        public void setDate(DateBean date) {
            this.date = date;
        }

        public static class DateBean {
            /**
             * date : 2000-01-01 00:00:00.000000
             * timezone_type : 3
             * timezone : Europe/Amsterdam
             */

            private String date;
            private int timezone_type;
            private String timezone;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public int getTimezone_type() {
                return timezone_type;
            }

            public void setTimezone_type(int timezone_type) {
                this.timezone_type = timezone_type;
            }

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }
        }
    }
}
