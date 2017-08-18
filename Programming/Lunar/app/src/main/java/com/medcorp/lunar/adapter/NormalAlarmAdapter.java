package com.medcorp.lunar.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2017/8/18.
 */

public class NormalAlarmAdapter extends BaseAdapter {

    private Context mContext;
    private List<Alarm> normalAlarmList;
    private OnDeleteNormalAlarmListener onDeleteNormalAlarmListener;
    private OnNormalAlarmSwitchListener normalAlarmSwitchListener;
    private OnAlarmConfigChangeListener onAlarmListener;
    private int normalItemHeight = 0;

    public NormalAlarmAdapter(Context context, List<Alarm> normalAlarm) {
        mContext = context;
        this.normalAlarmList = normalAlarm;
    }


    @Override
    public int getCount() {
        return normalAlarmList.size();
    }

    @Override
    public Object getItem(int position) {
        return normalAlarmList.get(position) != null ? normalAlarmList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NormalAlarmViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_alatm_list_item_normal, parent, false);
            holder = new NormalAlarmViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (NormalAlarmViewHolder) convertView.getTag();
        }


        setBedtimeData(holder, position);
        return convertView;
    }

    private void setBedtimeData(final NormalAlarmViewHolder holder, final int position) {

        holder.openExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandableClick(v, holder);
            }
        });
        holder.closeExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandableClick(v, holder);
            }
        });

        if (position == 0) {
            holder.titleTv.setVisibility(View.VISIBLE);
        } else {
            holder.titleTv.setVisibility(View.GONE);
        }
        final Alarm alarm = normalAlarmList.get(position);
        byte[] weekday = new byte[normalAlarmList.size()];
        for (int i = 0; i < normalAlarmList.size(); i++) {
            weekday[i] = normalAlarmList.get(i).getWeekDay();
        }
        if ((alarm.getWeekDay() & 0x80) == 0) {
            holder.alarmSwitch.setChecked(false);
        } else {
            holder.alarmSwitch.setChecked(true);
        }
        if (alarm != null) {
            holder.wakeTimeTv.setText(alarm.toString());
            holder.editBedtimeAlarmEd.setText(alarm.getLabel());
            String[] weekDayArray = mContext.getResources().getStringArray(R.array.alarm_week_day);
            holder.repeatWeekDayTv.setText(weekDayArray[alarm.getWeekDay() & 0x0F]);
            holder.alarmNameTv.setText(alarm.getLabel());
            holder.alarmSwitch.setChecked(!((alarm.getWeekDay() & 0x0F) == 0));
            holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    normalAlarmSwitchListener.onNormalAlarmSwitch((SwitchCompat) buttonView, alarm);
                }
            });

            holder.deleteBedtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteNormalAlarmListener.onNormalAlarmDelete(alarm.getId(), position);
                }
            });
        }

    }

    public void setExpandableClick(View v, final NormalAlarmViewHolder holder) {
        ValueAnimator valueAnimation = null;
        if (normalItemHeight == 0) {
            normalItemHeight = holder.rootView.getHeight();
        }
        if (v.getId() == R.id.normal_open_expandable_ib) {
            holder.showInfoRl.setVisibility(View.GONE);
            holder.bottomLine.setVisibility(View.GONE);
            holder.rootView.setBackgroundColor(mContext.getResources().getColor(R.color.bedtime_item_background_color));
            holder.expandable.setVisibility(View.VISIBLE);
            valueAnimation = ValueAnimator.ofInt(normalItemHeight, (int) (normalItemHeight * 1.6));
        } else if (v.getId() == R.id.close_edit_expandable_ib) {
            holder.rootView.setBackgroundColor(
                    mContext.getResources().getColor(R.color.window_background_color));
            holder.showInfoRl.setVisibility(View.VISIBLE);
            holder.bottomLine.setVisibility(View.VISIBLE);
            holder.expandable.setVisibility(View.GONE);
            valueAnimation = ValueAnimator.ofInt((int) (normalItemHeight * 1.6), normalItemHeight);
        }
        valueAnimation.setDuration(200);
        valueAnimation.setInterpolator(new LinearInterpolator());
        valueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                holder.rootView.getLayoutParams().height = value.intValue();
                holder.rootView.requestLayout();
            }
        });
        valueAnimation.start();
    }


    public void setOnAlarmConfigChangeListener(OnAlarmConfigChangeListener onConfigChangeListener) {
        this.onAlarmListener = onConfigChangeListener;
    }

    public void setNormalAlarmSwitchListener(OnNormalAlarmSwitchListener normalAlarmSwitchListener) {
        this.normalAlarmSwitchListener = normalAlarmSwitchListener;
    }

    public void setDeleteNormalAlarmListener(OnDeleteNormalAlarmListener onDeleteNormalAlarmListener) {
        this.onDeleteNormalAlarmListener = onDeleteNormalAlarmListener;
    }

    public interface OnNormalAlarmSwitchListener {
        void onNormalAlarmSwitch(SwitchCompat alarmSwitch, Alarm alarm);
    }


    public interface OnDeleteNormalAlarmListener {
        void onNormalAlarmDelete(int id, int position);
    }

    public interface OnAlarmConfigChangeListener {
        void onConfigChangeListener(boolean checked, String s, int position);
    }

    class NormalAlarmViewHolder {
        @Bind(R.id.fragment_alarm_list_view_wake_up_time_item)
        RobotoTextView wakeTimeTv;
        @Bind(R.id.fragment_alarm_list_view_item_normal_switch)
        SwitchCompat alarmSwitch;
        @Bind(R.id.fragment_alarm_list_view_item_normal_label)
        RobotoTextView alarmNameTv;
        @Bind(R.id.fragment_alarm_list_view_item_normal_repeat)
        RobotoTextView repeatWeekDayTv;
        @Bind(R.id.edit_normal_alarm_label_tv)
        EditText editBedtimeAlarmEd;
        @Bind(R.id.edit_normal_delete_alarm_ll)
        RelativeLayout deleteBedtime;
        @Bind(R.id.normal_item_expandable_edit_layout)
        LinearLayout expandable;
        @Bind(R.id.normal_open_expandable_ib)
        ImageButton openExpandableIb;
        @Bind(R.id.close_edit_expandable_ib)
        ImageButton closeExpandableIb;
        @Bind(R.id.normal_item_show_alarm_info)
        RelativeLayout showInfoRl;
        @Bind(R.id.normal_item_root_view)
        LinearLayout rootView;
        @Bind(R.id.alarm_adapter_item_title)
        TextView titleTv;
        @Bind(R.id.fragment_alarm_list_view_item_bottom_line)
        View bottomLine;

        public NormalAlarmViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
