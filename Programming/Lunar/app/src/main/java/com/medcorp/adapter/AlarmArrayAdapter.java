package com.medcorp.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.medcorp.R;
import com.medcorp.fragment.listener.OnAlarmSwitchListener;
import com.medcorp.model.Alarm;
import com.medcorp.view.ToastHelper;
import com.medcorp.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by karl-john on 17/12/15.
 */
public class AlarmArrayAdapter extends ArrayAdapter<Alarm> {

    private OnAlarmSwitchListener onAlarmSwitchedListener;
    private Context context;
    private List<Alarm> alarmList;
    private AlarmArrayAdapter adapter;
    private String alarmStyle;
    private int isDefWeekDay;

    public AlarmArrayAdapter(Context context, List<Alarm> alarmList, OnAlarmSwitchListener listener) {
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
        View itemView = inflater.inflate(R.layout.fragment_alarm_list_view_item, parent, false);
        final Alarm alarm = alarmList.get(position);
        RobotoTextView alarmTimeTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_time);
        RobotoTextView alarmLabelTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_label);
        final SwitchCompat onOffSwitch = (SwitchCompat) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_switch);

        RobotoTextView repeatTextDec = (RobotoTextView) itemView.findViewById(R.id.fragment_alarm_list_view_item_alarm_repeat_dec);
        RobotoTextView repeatText = (RobotoTextView) itemView.findViewById(R.id.fragment_alarm_list_view_item_alarm_repeat);
        alarmTimeTextView.setText(alarm.toString());
        alarmLabelTextView.setText(alarm.getLabel());
        onOffSwitch.setOnCheckedChangeListener(null);
        if ((alarm.getWeekDay() & 0x80) == 0) {
            onOffSwitch.setChecked(false);
        } else {
            onOffSwitch.setChecked(true);
        }
        if (alarm.getAlarmType() == 0) {
            alarmStyle = getContext().getString(R.string.edit_alarm_sleep);
        } else if (alarm.getAlarmType() == 1) {
            alarmStyle = getContext().getString(R.string.edit_alarm_wake);
        }

        String[] weekDayArray = getContext().getResources().getStringArray(R.array.week_day);
        String weekDay = weekDayArray[alarm.getWeekDay() & 0x0F];
        repeatText.setText(weekDay);
        repeatTextDec.setText(alarmStyle + ": ");
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDefWeekDay = alarm.getWeekDay() & 0x0F;
                if (isDefWeekDay != 0) {
                    onAlarmSwitchedListener.onAlarmSwitch((SwitchCompat) buttonView, alarm);
                } else {
                    onOffSwitch.setChecked(false);
                    ToastHelper.showShortToast(context, context.getString(R.string.tell_user_change_week_day));
                }

            }
        });
        return itemView;
    }
}
