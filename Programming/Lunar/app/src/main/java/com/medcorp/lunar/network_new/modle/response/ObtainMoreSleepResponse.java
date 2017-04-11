package com.medcorp.lunar.network_new.modle.response;

import com.medcorp.lunar.network_new.modle.base.BaseResponse;

import java.util.List;

/**
 * Created by Jason on 2017/4/11.
 */

public class ObtainMoreSleepResponse extends BaseResponse {


    private List<SleepBean> sleep;

    public List<SleepBean> getSleep() {
        return sleep;
    }

    public void setSleep(List<SleepBean> sleep) {
        this.sleep = sleep;
    }

    public static class SleepBean {
        private int id;
        private int uid;
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

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
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
             * date : 2017-03-31 00:00:00.000000
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
