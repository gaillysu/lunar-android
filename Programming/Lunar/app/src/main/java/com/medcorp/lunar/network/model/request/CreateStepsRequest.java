package com.medcorp.lunar.network.model.request;

import com.medcorp.lunar.network.model.base.BaseRequest;

/**
 * Created by Jason on 2017/4/7.
 */

public class CreateStepsRequest implements BaseRequest {

    private StepsBean steps;

    public CreateStepsRequest(String uid, String steps, String date, int calories, int active_time, double distance) {
        this.steps = new StepsBean(uid,steps,date,calories,active_time,distance);
    }

    private class StepsBean {
        private String uid;
        private String steps;
        private String date;
        private int calories;
        private int active_time;
        private double distance;

        public StepsBean(String uid, String steps, String date, int calories, int active_time, double distance) {
            this.uid = uid;
            this.steps = steps;
            this.date = date;
            this.calories = calories;
            this.active_time = active_time;
            this.distance = distance;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

        public int getActive_time() {
            return active_time;
        }

        public void setActive_time(int active_time) {
            this.active_time = active_time;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }
}
