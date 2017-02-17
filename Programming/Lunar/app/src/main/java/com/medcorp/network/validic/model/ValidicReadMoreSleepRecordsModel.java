package com.medcorp.network.validic.model;


/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicReadMoreSleepRecordsModel {
    ValidicSummary summary;
    ValidicSleepRecordModelBase[] sleep;

    public ValidicSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidicSummary summary) {
        this.summary = summary;
    }

    public ValidicSleepRecordModelBase[] getSleep() {
        return sleep;
    }

    public void setSleep(ValidicSleepRecordModelBase[] sleep) {
        this.sleep = sleep;
    }
}
