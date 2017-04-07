package com.medcorp.lunar.network_new.modle.request;

import com.medcorp.lunar.network_new.modle.base.BaseRequest;

import java.util.List;

/**
 * Created by Jason on 2017/4/7.
 */

public class CreateMultiStepsRequest implements BaseRequest {

    private List<StepsBean> steps;

    public List<StepsBean> getSteps() {
        return steps;
    }

    public void setSteps(List<StepsBean> steps) {
        this.steps = steps;
    }

    public static class StepsBean {
        /**
         * uid : 52
         * steps : {{steps_example}}
         * date : {{date_example}}
         */

        private String uid;
        private String steps;
        private String date;

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
    }
}
