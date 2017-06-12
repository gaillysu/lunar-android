package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.util.PublicUtils;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by karl-john on 17/12/15.
 */
public class BedtimeAdapter extends ArrayAdapter<BedtimeModel> {

    private OnBedtimeSwitchListener onAlarmSwitchedListener;
    private Context context;
    private List<BedtimeModel> alarmList;
    private BedtimeAdapter adapter;

    public BedtimeAdapter(Context context, List<BedtimeModel> alarmList, OnBedtimeSwitchListener listener) {
        super(context, 0, alarmList);
        this.context = context;
        this.alarmList = alarmList;
        this.onAlarmSwitchedListener = listener;
        adapter = this;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fragment_alatm_list_item_bedtime, parent, false);
        final BedtimeModel bedtime = alarmList.get(position);
        RobotoTextView alarmTimeTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarm_list_view_item_bedtime);
        RobotoTextView alarmLabelTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarm_list_view_item_bedtime_label);
        final SwitchCompat onOffSwitch = (SwitchCompat) itemView.findViewById(R.id.fragment_alarm_list_view_item_bedtime_switch);
        RobotoTextView sleepGoal = (RobotoTextView) itemView.findViewById(R.id.fragment_alarm_list_view_item_sleep_goal);
        RobotoTextView repeatText = (RobotoTextView) itemView.findViewById(R.id.fragment_alarm_list_view_item_bedtime_repeat);
        alarmTimeTextView.setText(bedtime.toString());
        alarmLabelTextView.setText(bedtime.getName());
        sleepGoal.setText(PublicUtils.countTime(context,bedtime.getSleepGoal()));
        onOffSwitch.setOnCheckedChangeListener(null);

        if (bedtime.isEnable()) {
            onOffSwitch.setChecked(false);
        } else {
            onOffSwitch.setChecked(true);
        }

        String[] weekDayArray = getContext().getResources().getStringArray(R.array.week_day);
        byte[] weekday = bedtime.getWeekday();
        StringBuffer weekdayString = new StringBuffer();
        for (int i = 0; i < weekday.length; i++) {
            weekdayString.append(weekDayArray[weekday[i]] + " ,");
        }
        repeatText.setText(weekdayString.toString());

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onAlarmSwitchedListener.onBedtimeSwitch((SwitchCompat) buttonView, bedtime);
                onOffSwitch.setChecked(isChecked);
            }
        });
        return itemView;
    }

    public interface OnBedtimeSwitchListener {
        void onBedtimeSwitch(SwitchCompat alarmSwitch, BedtimeModel alarm);
    }

}
