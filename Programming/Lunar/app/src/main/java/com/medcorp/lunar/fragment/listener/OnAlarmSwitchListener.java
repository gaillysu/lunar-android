package com.medcorp.lunar.fragment.listener;

import android.support.v7.widget.SwitchCompat;

import com.medcorp.lunar.model.Alarm;


/**
 * Created by gaillysu on 16/1/19.
 */
public interface OnAlarmSwitchListener {
    public void onAlarmSwitch(SwitchCompat alarmSwitch, Alarm alarm);
}
