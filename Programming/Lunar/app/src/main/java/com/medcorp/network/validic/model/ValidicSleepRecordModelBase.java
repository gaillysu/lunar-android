package com.medcorp.network.validic.model;

/**
 * Created by med on 16/3/23.
 */
public class ValidicSleepRecordModelBase {
    String _id;
    String timestamp;
    String utc_offset;
    double awake;
    double deep;
    double light;
    double rem;
    double times_woken;
    double total_sleep;
    String source;
    String source_name;
    String last_updated;
    Boolean validated;
    NevoHourlySleepData extras;
    String activity_id;

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public NevoHourlySleepData getExtras() {
        return extras;
    }

    public void setExtras(NevoHourlySleepData extras) {
        this.extras = extras;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUtc_offset() {
        return utc_offset;
    }

    public void setUtc_offset(String utc_offset) {
        this.utc_offset = utc_offset;
    }

    public double getAwake() {
        return awake;
    }

    public void setAwake(double awake) {
        this.awake = awake;
    }

    public double getDeep() {
        return deep;
    }

    public void setDeep(double deep) {
        this.deep = deep;
    }

    public double getLight() {
        return light;
    }

    public void setLight(double light) {
        this.light = light;
    }

    public double getRem() {
        return rem;
    }

    public void setRem(double rem) {
        this.rem = rem;
    }

    public double getTimes_woken() {
        return times_woken;
    }

    public void setTimes_woken(double times_woken) {
        this.times_woken = times_woken;
    }

    public double getTotal_sleep() {
        return total_sleep;
    }

    public void setTotal_sleep(double total_sleep) {
        this.total_sleep = total_sleep;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
}
