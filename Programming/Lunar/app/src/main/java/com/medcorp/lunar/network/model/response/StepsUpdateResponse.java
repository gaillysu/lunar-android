package com.medcorp.lunar.network.model.response;

import com.medcorp.lunar.network.model.base.BaseResponse;

/**
 * Created by Jason on 2017/4/7.
 */

public class StepsUpdateResponse extends BaseResponse {

    private StepsBean steps;

    public StepsBean getSteps() {
        return steps;
    }

    public void setSteps(StepsBean steps) {
        this.steps = steps;
    }

    public static class StepsBean {
        /**
         * id : 317
         * uid : 52
         * steps : [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
         * date : {"date":"2000-01-01 00:00:00.000000","timezone_type":3,"timezone":"Europe/Amsterdam"}
         * active_time : 0
         * calories : 0
         * distance : 0
         */

        private int id;
        private String uid;
        private String steps;
        private DateBean date;
        private int active_time;
        private int calories;
        private int distance;

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

        public String getSteps() {
            return steps;
        }

        public void setSteps(String steps) {
            this.steps = steps;
        }

        public DateBean getDate() {
            return date;
        }

        public void setDate(DateBean date) {
            this.date = date;
        }

        public int getActive_time() {
            return active_time;
        }

        public void setActive_time(int active_time) {
            this.active_time = active_time;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
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
