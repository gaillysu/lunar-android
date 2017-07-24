package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/***
 * Created by karl-john on 17/12/15.
 */
public class BedtimeAdapter extends RecyclerView.Adapter<BedtimeAdapter.ViewHolder>
        implements CompoundButton.OnCheckedChangeListener {

    private OnBedtimeSwitchListener onAlarmSwitchedListener;
    private OnBedtimeDeleteListener listener;
    private Context context;
    private List<BedtimeModel> bedtimeList;
    private List<Integer> manyWeekday;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private ApplicationModel model;
    private List<Alarm> normalAlarmList;

    public BedtimeAdapter(OnBedtimeSwitchListener onAlarmSwitchedListener, OnBedtimeDeleteListener listener,
                          ApplicationModel model,Context context, List<Alarm> normalAlarmList, List<BedtimeModel> bedtimeAllList) {
        this.listener = listener;
        this.onAlarmSwitchedListener = onAlarmSwitchedListener;
        this.model = model;
        this.normalAlarmList = normalAlarmList;
        this.context = context;
        this.bedtimeList = bedtimeAllList;
        manyWeekday = new ArrayList<>();
    }

    //    @Override
    //    public int getCount() {
    //        return bedtimeList.size();
    //    }
    //
    //    @Override
    //    public Object getItem(int position) {
    //        return bedtimeList.get(position) != null ? bedtimeList.get(position) : null;
    //    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.fragment_alatm_list_item_bedtime, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        setDataForItem(holder, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bedtimeList.size() + normalAlarmList.size();
    }

    //    @Override
    //    public View getView(int position, View convertView, ViewGroup parent) {
    //        ViewHolder viewHolder = null;
    //        if (convertView == null) {
    //            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_alatm_list_item_bedtime, parent, false);
    //            viewHolder = new ViewHolder(convertView);
    //            convertView.setTag(viewHolder);
    //        } else {
    //            viewHolder = (ViewHolder) convertView.getTag();
    //        }
    //        setDataForItem(viewHolder, position);
    //
    //        return convertView;
    //    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
        @Bind(R.id.bedtime_item_expandable_edit_layout)
        ExpandableLinearLayout expandable;
        @Bind(R.id.bedtime_open_expandable_ib)
        ImageButton openExpandableIb;
        @Bind(R.id.close_edit_expandable_ib)
        ImageButton closeExpandableIb;
        @Bind(R.id.bedtime_item_show_alarm_info)
        RelativeLayout showInfoRl;
        @Bind(R.id.bedtime_item_root_view)
        LinearLayout rootView;
        @Bind(R.id.alarm_adapter_item_title)
        TextView titleTv;
        @Bind(R.id.bedtime_item_wake_up_icon)
        ImageView wakeUpIv;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            initView(this);
        }
    }

    private void initView(final ViewHolder viewHolder) {
        model.getBedTimeDatabaseHelper().getAll().subscribe(new Consumer<List<BedtimeModel>>() {
            @Override
            public void accept(List<BedtimeModel> alarms) throws Exception {

                List<Byte> allWeekday = new ArrayList<>();
                for (BedtimeModel bedtimeModel : alarms) {
                    byte[] weekday = bedtimeModel.getWeekday();
                    for (int i = 0; i < weekday.length; i++) {
                        allWeekday.add(weekday[i]);
                    }
                }
                for (int i = 0; i < allWeekday.size(); i++) {
                    switch (allWeekday.get(i)) {
                        case 0:
                            viewHolder.sunday.setClickable(false);
                            viewHolder.sunday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 1:
                            viewHolder.monday.setClickable(false);
                            viewHolder.monday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 2:
                            viewHolder.tuesday.setClickable(false);
                            viewHolder.tuesday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 3:
                            viewHolder.wednesday.setClickable(false);
                            viewHolder.wednesday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 4:
                            viewHolder.thursday.setClickable(false);
                            viewHolder.thursday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 5:
                            viewHolder.friday.setClickable(false);
                            viewHolder.friday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 6:
                            viewHolder.saturday.setClickable(false);
                            viewHolder.saturday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                    }
                }
            }
        });
    }

    private void setDataForItem(final ViewHolder viewHolder, final int position) {

        if (bedtimeList.size() > 0 && position == 0) {
            viewHolder.titleTv.setVisibility(View.VISIBLE);
            viewHolder.titleTv.setText(context.getString(R.string.append_new_alarm_bedtime));
        } else if (normalAlarmList.size() > 0 && position == bedtimeList.size()) {
            viewHolder.titleTv.setVisibility(View.VISIBLE);
            viewHolder.titleTv.setText(context.getString(R.string.append_new_alarm_normal));
        } else {
            viewHolder.titleTv.setVisibility(View.GONE);
        }
        if (normalAlarmList.size() != 0 && position > bedtimeList.size() - 1) {
            viewHolder.wakeUpIv.setVisibility(View.GONE);
            viewHolder.wakeTimeTv.setVisibility(View.GONE);
        }
        if (position < bedtimeList.size()) {
            setBedtimeData(viewHolder, position);
        } else if (position > bedtimeList.size() - 1 && normalAlarmList.size() > 0) {
            setNormalAlarmData(viewHolder, position);
        }


        viewHolder.expandable.setInRecyclerView(false);
        viewHolder.expandable.setExpanded(expandState.get(position));
        viewHolder.expandable.setInterpolator(Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR));
        viewHolder.expandable.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                viewHolder.showInfoRl.setVisibility(View.GONE);
                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.bedtime_item_background_color));
                expandState.put(position, false);
            }

            @Override
            public void onPreClose() {
                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.window_background_color));
                viewHolder.showInfoRl.setVisibility(View.VISIBLE);
                expandState.put(position, true);
            }
        });
        viewHolder.openExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.expandable.toggle();
            }
        });
        viewHolder.closeExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.expandable.toggle();
            }
        });
        viewHolder.monday.setOnCheckedChangeListener(this);
        viewHolder.sunday.setOnCheckedChangeListener(this);
        viewHolder.tuesday.setOnCheckedChangeListener(this);
        viewHolder.wednesday.setOnCheckedChangeListener(this);
        viewHolder.thursday.setOnCheckedChangeListener(this);
        viewHolder.friday.setOnCheckedChangeListener(this);
        viewHolder.saturday.setOnCheckedChangeListener(this);
    }

    private void setNormalAlarmData(ViewHolder viewHolder, int position) {
        final Alarm alarm = normalAlarmList.get(position);
        byte[] weekday = new byte[normalAlarmList.size()];
        for (int i = 0; i < normalAlarmList.size(); i++) {
            weekday[i] = normalAlarmList.get(i).getWeekDay();
        }
        if (alarm != null) {
            viewHolder.editBedtimeAlarmEd.setText(alarm.getLabel());
            viewHolder.repeatWeekDayTv.setTag(obtainWeekday(weekday));
            viewHolder.sleepTimeTv.setText(alarm.toString());
            viewHolder.alarmSwitch.setChecked(alarm.isEnable());
            viewHolder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    onAlarmSwitchedListener.onBedtimeSwitch((SwitchCompat) buttonView, alarm);
                }
            });

            viewHolder.alarmNameTv.setText(alarm.getLabel());
            viewHolder.deleteBedtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBedtimeDelete(new byte[alarm.getAlarmNumber()]);
                }
            });
        }
    }

    private void setBedtimeData(ViewHolder viewHolder, int position) {
        final BedtimeModel bedtimeModel = bedtimeList.get(position);
        if (bedtimeModel != null) {
            viewHolder.editBedtimeAlarmEd.setText(bedtimeModel.getName());
            viewHolder.repeatWeekDayTv.setTag(obtainWeekday(bedtimeModel.getWeekday()));
            viewHolder.sleepTimeTv.setText("23:30");
            viewHolder.wakeTimeTv.setText(bedtimeModel.toString());
            viewHolder.alarmSwitch.setChecked(bedtimeModel.isEnable());
            viewHolder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onAlarmSwitchedListener.onBedtimeSwitch((SwitchCompat) buttonView, bedtimeModel);
                }
            });

            viewHolder.alarmNameTv.setText(bedtimeModel.getName());
            viewHolder.deleteBedtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBedtimeDelete(bedtimeModel.getAlarmNumber());
                }
            });
        }
    }

    public String obtainWeekday(byte[] weekday) {
        String[] weekDayArray = context.getResources().getStringArray(R.array.week_day);
        StringBuffer weekdayString = new StringBuffer();
        for (int i = 0; i < weekday.length; i++) {
            if (i != weekday.length - 1) {
                weekdayString.append(weekDayArray[weekday[i]] + " ,");
            } else {
                weekdayString.append(weekDayArray[weekday[i]]);
            }
        }
        return weekdayString.toString();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.bedtime_sunday:
                if (isChecked) {
                    manyWeekday.add(0);
                }
                break;
            case R.id.bedtime_monday:
                if (isChecked) {
                    manyWeekday.add(1);
                }
                break;
            case R.id.bedtime_tuesday:
                if (isChecked) {
                    manyWeekday.add(2);
                }
                break;
            case R.id.bedtime_wednesday:
                if (isChecked) {
                    manyWeekday.add(3);
                }
                break;
            case R.id.bedtime_thursday:
                if (isChecked) {
                    manyWeekday.add(4);
                }
                break;
            case R.id.bedtime_friday:
                if (isChecked) {
                    manyWeekday.add(5);
                }
                break;
            case R.id.bedtime_saturday:
                if (isChecked) {
                    manyWeekday.add(6);
                }
                break;
        }
    }

    public interface OnBedtimeSwitchListener {
        void onBedtimeSwitch(SwitchCompat alarmSwitch, BedtimeModel alarm);
    }

    public interface OnBedtimeDeleteListener {
        void onBedtimeDelete(byte[] alarmNumber);
    }
}
