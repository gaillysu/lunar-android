package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 17/12/15.
 */
public class BedtimeAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private OnBedtimeSwitchListener onAlarmSwitchedListener;
    private OnBedtimeDeleteListener listener;
    private Context context;
    private List<BedtimeModel> bedtimeList;

    public BedtimeAdapter(OnBedtimeSwitchListener onAlarmSwitchedListener
            , OnBedtimeDeleteListener listener ,Context context, List<BedtimeModel> alarmList) {
        this.listener = listener;
        this.onAlarmSwitchedListener = onAlarmSwitchedListener;
        this.context = context;
        this.bedtimeList = alarmList;
    }

    @Override
    public int getCount() {
        return bedtimeList.size();
    }

    @Override
    public Object getItem(int position) {
        return bedtimeList.get(position) != null ? bedtimeList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_alatm_list_item_bedtime, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setDataForItem(viewHolder, position);

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.fragment_alarm_list_view_sleep_time_item)
        RobotoTextView sleepTimeTv;
        @Bind(R.id.fragment_alarm_list_view_wake_up_time_item)
        RobotoTextView wakeTimeTv;
        @Bind(R.id.fragment_alarm_list_view_item_bedtime_switch)
        SwitchCompat alarmSwitch;
        @Bind(R.id.fragment_alarm_list_view_item_bedtime_label)
        RobotoTextView alarmNameTv;
        @Bind(R.id.fragment_alarm_list_view_item_bedtime_repeat)
        RobotoTextView repeatWeekDayTv;
        @Bind(R.id.bedtime_sunday)
        CheckBox sunday;
        @Bind(R.id.bedtime_monday)
        CheckBox monday;
        @Bind(R.id.bedtime_tuesday)
        CheckBox tuesday;
        @Bind(R.id.bedtime_wednesday)
        CheckBox wednesday;
        @Bind(R.id.bedtime_thursday)
        CheckBox thursday;
        @Bind(R.id.bedtime_friday)
        CheckBox friday;
        @Bind(R.id.bedtime_saturday)
        CheckBox saturday;
        @Bind(R.id.edit_bedtime_alarm_goal_ll)
        RelativeLayout settingBedTimeGoal;
        @Bind(R.id.edit_bedtime_alarm_label_tv)
        EditText editBedtimeAlarmEd;
        @Bind(R.id.edit_bedtime_delete_alarm_ll)
        RelativeLayout deleteBedtime;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void setDataForItem(ViewHolder viewHolder, final int position) {
        final BedtimeModel bedtime = bedtimeList.get(position);
        //        private String name;
        //        private int SleepGoal;
        //        private byte[] alarmNumber;
        //        private int hour;
        //        private int minute;
        //        private byte[] weekday;
        //        private boolean enable;
        if (bedtime != null) {
            String[] weekDayArray = context.getResources().getStringArray(R.array.week_day);
            byte[] weekday = bedtime.getWeekday();
            StringBuffer weekdayString = new StringBuffer();
            for (int i = 0; i < weekday.length; i++) {
                if (i != weekday.length - 1) {
                    weekdayString.append(weekDayArray[weekday[i]] + " ,");
                } else {
                    weekdayString.append(weekDayArray[weekday[i]]);
                }
            }
            viewHolder.repeatWeekDayTv.setTag(weekdayString);
            viewHolder.sleepTimeTv.setText("23:30");
            viewHolder.wakeTimeTv.setText(bedtime.toString());
            viewHolder.alarmSwitch.setChecked(bedtime.isEnable());
            viewHolder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onAlarmSwitchedListener.onBedtimeSwitch((SwitchCompat) buttonView, bedtime);
                }
            });
            viewHolder.alarmNameTv.setText(bedtime.getName());
            viewHolder.deleteBedtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBedtimeDelete(position);
                }
            });

            viewHolder.editBedtimeAlarmEd.setText(bedtime.getName());
            viewHolder.monday.setOnCheckedChangeListener(this);
            viewHolder.sunday.setOnCheckedChangeListener(this);
            viewHolder.tuesday.setOnCheckedChangeListener(this);
            viewHolder.wednesday.setOnCheckedChangeListener(this);
            viewHolder.thursday.setOnCheckedChangeListener(this);
            viewHolder.friday.setOnCheckedChangeListener(this);
            viewHolder.saturday.setOnCheckedChangeListener(this);

        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    public interface OnBedtimeSwitchListener {
        void onBedtimeSwitch(SwitchCompat alarmSwitch, BedtimeModel alarm);
    }

    public interface OnBedtimeDeleteListener {
        void onBedtimeDelete(int position);
    }
}
