package com.medcorp.lunar.adapter;

import android.animation.ValueAnimator;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.util.PublicUtils;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

import static com.medcorp.lunar.R.id.edit_bedtime_alarm_time_ll;

/***
 * Created by karl-john on 17/12/15.
 */
public class AlarmRecyclerViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<BedtimeModel> bedtimeList;
    private ListView mAllSleepGoalList;
    private List<SleepGoal> mAllSleepGoal;
    private int bedtimeItemHeight = 0;
    private ApplicationModel model;
    private OnBedtimeSwitchListener onAlarmSwitchedListener;
    private OnBedtimeDeleteListener listener;
    private ShowAllSleepGoalAdapter mGoalAdapter;
    private OnBedtimeConfigChangeListener onBedtimeListener;
    private OnDeleteNormalAlarmListener onDeleteNormalAlarmListener;
    private OnNormalAlarmSwitchListener normalAlarmSwitchListener;
    private OnAlarmConfigChangeListener onAlarmListener;
    private int selectHour = 0;
    private int selectMinutes = 0;
    private String sleepLableGoal;
    private int newSleepGoalTime = 0;
    private List<Alarm> normalList;
    private static final int ITEM_TYPE_BEDTIME = 0x03;
    private static final int ITEM_TYPE_NORMAL = 0x04;
    private int normalItemHeight = 0;
    private BedtimeModel mCurrentBedtime;
    private int alarmHour = 0;
    private int alarmMinute = 0;

    public AlarmRecyclerViewAdapter(Context context, ApplicationModel model, List<BedtimeModel> bedtimeAllList, List<Alarm> alarmList) {
        mContext = context;
        this.model = model;
        this.normalList = alarmList;
        this.bedtimeList = bedtimeAllList;
        mAllSleepGoal = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return bedtimeList.size() + normalList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < bedtimeList.size()) {
            return bedtimeList.get(position) != null ? bedtimeList.get(position) : null;
        }
        return normalList.get(position - bedtimeList.size()) != null ? normalList.get(position - bedtimeList.size()) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < bedtimeList.size()) {
            return ITEM_TYPE_BEDTIME;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 100;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BedtimeViewHolder bedtimeHolder = null;
        NormalAlarmViewHolder normalHolder = null;
        switch (getItemViewType(position)) {
            case ITEM_TYPE_BEDTIME:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_alatm_list_item_bedtime, parent, false);
                    bedtimeHolder = new BedtimeViewHolder(convertView);
                    convertView.setTag(bedtimeHolder);
                } else {
                    bedtimeHolder = (BedtimeViewHolder) convertView.getTag();
                }
                initView(bedtimeHolder, position);
                setBedtimeData(bedtimeHolder, position);
                break;
            case ITEM_TYPE_NORMAL:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_alatm_list_item_normal, parent, false);
                    normalHolder = new NormalAlarmViewHolder(convertView);
                    convertView.setTag(normalHolder);
                } else {
                    normalHolder = (NormalAlarmViewHolder) convertView.getTag();
                }
                setNormalData(normalHolder, position - bedtimeList.size());
                break;
        }
        return convertView;
    }

    class BedtimeViewHolder {
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
        LinearLayout expandable;
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
        @Bind(R.id.fragment_alarm_list_view_item_bottom_line)
        View bottomLine;
        @Bind(R.id.edit_bedtime_alarm_goal_tv)
        TextView showBedtimeGoalTv;
        @Bind(edit_bedtime_alarm_time_ll)
        RelativeLayout editTime;

        public BedtimeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void initView(final BedtimeViewHolder holder, final int position) {
        if (position == 0) {
            holder.titleTv.setVisibility(View.VISIBLE);
        } else {
            holder.titleTv.setVisibility(View.GONE);
        }

        for (int i = 0; i < bedtimeList.size(); i++) {
            if (bedtimeList.get(i).getId() == bedtimeList.get(position).getId()) {
                byte[] currentWeekday = bedtimeList.get(position).getWeekday();
                for (int j = 0; j < currentWeekday.length; j++) {
                    switch (bedtimeList.get(position).getWeekday()[j]) {
                        case 0:
                            holder.sunday.setChecked(true);
                            break;
                        case 1:
                            holder.monday.setChecked(true);
                            break;
                        case 2:
                            holder.tuesday.setChecked(true);
                            break;
                        case 3:
                            holder.wednesday.setChecked(true);
                            break;
                        case 4:
                            holder.thursday.setChecked(true);
                            break;
                        case 5:
                            holder.friday.setChecked(true);
                            break;
                        case 6:
                            holder.saturday.setChecked(true);
                            break;
                    }
                }
            } else {
                for (int j = 0; j < bedtimeList.get(i).getWeekday().length; j++) {
                    byte[] weekday = bedtimeList.get(i).getWeekday();
                    switch (weekday[j]) {
                        case 0:
                            holder.sunday.setClickable(false);
                            holder.sunday.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 1:
                            holder.monday.setClickable(false);
                            holder.monday.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 2:
                            holder.tuesday.setClickable(false);
                            holder.tuesday.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 3:
                            holder.wednesday.setClickable(false);
                            holder.wednesday.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 4:
                            holder.thursday.setClickable(false);
                            holder.thursday.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 5:
                            holder.friday.setClickable(false);
                            holder.friday.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                        case 6:
                            holder.saturday.setClickable(false);
                            holder.saturday.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_weekday));
                            break;
                    }

                }
            }
        }
    }

    private void setBedtimeData(final BedtimeViewHolder holder, final int position) {
        alarmHour = bedtimeList.get(position).getHour();
        alarmMinute = bedtimeList.get(position).getMinute();
        newSleepGoalTime = bedtimeList.get(position).getSleepGoal();
        holder.closeExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableClick(v, holder, position);
            }
        });
        holder.openExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableClick(v, holder, position);
            }
        });
        holder.editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(mContext, R.style.NevoDialogStyle,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alarmHour = hourOfDay;
                                alarmMinute = minute;
                                int[] sleepTime = PublicUtils.countTime(newSleepGoalTime,
                                        hourOfDay, minute, bedtimeList.get(position).getWeekday()[0]);
                                holder.sleepTimeTv.setText(PublicUtils.getTimeString(sleepTime[0], sleepTime[1]));
                                holder.wakeTimeTv.setText(PublicUtils.getTimeString(hourOfDay, minute));
                            }
                        }, bedtimeList.get(position).getHour(), bedtimeList.get(position).getMinute(), true)
                        .show();
            }
        });

        mCurrentBedtime = bedtimeList.get(position);
        if (mCurrentBedtime != null) {
            holder.editBedtimeAlarmEd.setText(mCurrentBedtime.getName());
            holder.repeatWeekDayTv.setText("");
            holder.repeatWeekDayTv.setText(obtainWeekday(mCurrentBedtime.getWeekday()));
            holder.alarmNameTv.setText(mCurrentBedtime.getName());
            holder.showBedtimeGoalTv.setText(PublicUtils.getGoalString(mContext, mCurrentBedtime.getSleepGoal()/60
                    , mCurrentBedtime.getSleepGoal()%60));
            holder.sleepTimeTv.setText(mCurrentBedtime.getSleepTime());
            holder.wakeTimeTv.setText(mCurrentBedtime.toString());
            holder.alarmSwitch.setChecked(mCurrentBedtime.isEnable());
            holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onAlarmSwitchedListener.onBedtimeSwitch((SwitchCompat) buttonView, mCurrentBedtime);
                }
            });

            holder.deleteBedtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBedtimeDelete(mCurrentBedtime, position);
                }
            });
        }
        holder.settingBedTimeGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSleepGoalListDialog(holder, position);
            }
        });
    }

    private void expandableClick(View v, final BedtimeViewHolder holder, int position) {
        ValueAnimator valueAnimation = null;
        if (bedtimeItemHeight == 0) {
            bedtimeItemHeight = holder.rootView.getHeight();
        }
        if (v.getId() == R.id.bedtime_open_expandable_ib) {
            holder.showInfoRl.setVisibility(View.GONE);
            holder.bottomLine.setVisibility(View.GONE);
            holder.expandable.setVisibility(View.VISIBLE);
            holder.rootView.setBackgroundColor(mContext.getResources().getColor(R.color.bedtime_item_background_color));
            valueAnimation = ValueAnimator.ofInt(bedtimeItemHeight, (int) (bedtimeItemHeight * 3.4));
        } else if (v.getId() == R.id.close_edit_expandable_ib) {
            holder.rootView.setBackgroundColor(mContext.getResources().getColor(R.color.window_background_color));
            holder.showInfoRl.setVisibility(View.VISIBLE);
            holder.bottomLine.setVisibility(View.VISIBLE);
            holder.expandable.setVisibility(View.GONE);
            saveBedtimeChangeConfig(holder, alarmHour, alarmMinute, position);
            valueAnimation = ValueAnimator.ofInt((int) (bedtimeItemHeight * 3.4), bedtimeItemHeight);
        }
        valueAnimation.setDuration(150);
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


    public String obtainWeekday(byte[] weekday) {
        String[] weekDayArray = mContext.getResources().getStringArray(R.array.alarm_weekday);
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

    private void showSleepGoalListDialog(final BedtimeViewHolder holder, int pos) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View allGoalBottomView = inflater.inflate(R.layout.show_sleep_goal_bottom_dialog_view, null);
        mAllSleepGoalList = (ListView) allGoalBottomView.findViewById(R.id.show_all_sleep_goal_list);
        model.getSleepGoalDatabseHelper().getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(List<SleepGoal> sleepGoals) throws Exception {
                mAllSleepGoal.clear();
                mAllSleepGoal.addAll(sleepGoals);
                mGoalAdapter = new ShowAllSleepGoalAdapter(mContext, sleepGoals);
                mAllSleepGoalList.setAdapter(mGoalAdapter);
            }
        });
        final BedtimeModel bedtimeModel = bedtimeList.get(pos);
        mAllSleepGoalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bottomSheetDialog.dismiss();
                SleepGoal sleepGoal = mAllSleepGoal.get(position);
                int[] sleepTime = PublicUtils.countTime(sleepGoal.getGoalDuration(), bedtimeModel.getHour()
                        , bedtimeModel.getMinute(), bedtimeModel.getWeekday()[0]);
                holder.sleepTimeTv.setText(PublicUtils.getTimeString(sleepTime[0], sleepTime[1]));
                holder.showBedtimeGoalTv.setText(PublicUtils.getGoalString(mContext,
                        sleepGoal.getGoalDuration() / 60, sleepGoal.getGoalDuration() % 60));
                newSleepGoalTime = sleepGoal.getGoalDuration();
            }
        });
        Button addPresetsGoal = (Button) allGoalBottomView.findViewById(R.id.add_presets_sleep_goal_bt);
        addPresetsGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                addNewSleepGoalMethod();
            }
        });
        bottomSheetDialog.setContentView(allGoalBottomView);
        bottomSheetDialog.show();
    }

    private void saveBedtimeChangeConfig(BedtimeViewHolder holder, int hour, int minute, int position) {
        BedtimeModel bedtimeModel = bedtimeList.get(position);
        int[] sleepTime = PublicUtils.countTime(newSleepGoalTime,
                hour, minute, bedtimeModel.getWeekday()[0]);
        onBedtimeListener.onBedtimeConfigChangeListener(getWeekday(holder),
                holder.editBedtimeAlarmEd.getText().toString() != null
                        ? holder.editBedtimeAlarmEd.getText().toString() : bedtimeModel.getName(),
                sleepTime[0], sleepTime[1], hour, minute, newSleepGoalTime, position);
    }

    private byte[] getWeekday(BedtimeViewHolder holder) {
        List<Byte> manyWeekday = new ArrayList<>();
        if (holder.sunday.isChecked())
            manyWeekday.add((byte) 0);
        if (holder.monday.isChecked())
            manyWeekday.add((byte) 1);
        if (holder.tuesday.isChecked())
            manyWeekday.add((byte) 2);
        if (holder.wednesday.isChecked())
            manyWeekday.add((byte) 3);
        if (holder.thursday.isChecked())
            manyWeekday.add((byte) 4);
        if (holder.friday.isChecked())
            manyWeekday.add((byte) 5);
        if (holder.saturday.isChecked())
            manyWeekday.add((byte) 6);
        byte[] weekday = new byte[manyWeekday.size()];
        for (int i = 0; i < manyWeekday.size(); i++) {
            weekday[i] = manyWeekday.get(i);
        }
        return weekday;
    }

    private void addNewSleepGoalMethod() {
        selectHour = 0;
        selectMinutes = 0;
        View selectTimeDialog = LayoutInflater.from(mContext).inflate(R.layout.select_time_dialog_layou, null);
        List<String> hourList = new ArrayList<>();
        List<String> minutes = new ArrayList<>();
        minutes.add(0 + "");
        minutes.add(30 + "");
        for (int i = 5; i <= 12; i++) {
            hourList.add(i + "");
        }
        PickerView hourPickerView = (PickerView) selectTimeDialog.findViewById(R.id.hour_pv);
        hourPickerView.setData(hourList);
        hourPickerView.setSelected(3);
        PickerView minutePickerView = (PickerView) selectTimeDialog.findViewById(R.id.minute_pv);
        minutePickerView.setData(minutes);
        minutePickerView.setSelected(0);
        hourPickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectHour = new Integer(text).intValue();
            }
        });

        minutePickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectMinutes = new Integer(text).intValue();
            }
        });

        new MaterialDialog.Builder(mContext).customView(selectTimeDialog, false).
                title(mContext.getString(R.string.add_new_inactivity_goal_fb)).
                onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (selectHour == 0 && selectMinutes == 0) {
                            selectHour = 8;
                        }
                        addNewInactivity(selectHour, selectMinutes);
                    }
                }).positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .negativeColor(mContext.getResources().getColor(R.color.colorPrimary))
                .positiveColor(mContext.getResources().getColor(R.color.colorPrimary))
                .show();
    }

    public void addNewInactivity(final int hour, final int minute) {
        new MaterialDialog.Builder(mContext)
                .title(R.string.edit_goal_name)
                .content(R.string.goal_label_sleep)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(mContext.getString(R.string.goal_name_goal_sleep), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.length() == 0) {
                                    sleepLableGoal = mContext.getString(R.string.def_goal_sleep_name) + " " + (mAllSleepGoal.size() + 1);
                                } else {
                                    sleepLableGoal = input.toString();
                                }
                                final SleepGoal newSleepGoal = new SleepGoal(sleepLableGoal, hour * 60 + minute, false);
                                model.getSleepGoalDatabseHelper().add(newSleepGoal).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            mAllSleepGoal.add(newSleepGoal);
                                            mGoalAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }).negativeText(R.string.goal_cancel)
                .negativeColor(mContext.getResources().getColor(R.color.colorPrimary))
                .positiveColor(mContext.getResources().getColor(R.color.colorPrimary))
                .show();
    }


    /***
     * normal adapter
     */
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
        @Bind(edit_bedtime_alarm_time_ll)
        RelativeLayout editTime;

        public NormalAlarmViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }


    private void setNormalData(final NormalAlarmViewHolder holder, final int position) {
        holder.openExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandableClick(v, holder, position);
            }
        });
        holder.closeExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandableClick(v, holder, position);
            }
        });

        if (position == 0) {
            holder.titleTv.setVisibility(View.VISIBLE);
        } else {
            holder.titleTv.setVisibility(View.GONE);
        }
        final Alarm alarm = normalList.get(position);
        if ((alarm.getWeekDay() & 0x80) == 0) {
            holder.alarmSwitch.setChecked(false);
        } else {
            holder.alarmSwitch.setChecked(true);
        }

        holder.editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(mContext, R.style.NevoDialogStyle,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alarmHour = hourOfDay;
                                alarmMinute = minute;
                                holder.wakeTimeTv.setText(PublicUtils.getTimeString(hourOfDay, minute));
                            }
                        }, normalList.get(position).getHour(), normalList.get(position).getMinute(), true)
                        .show();
            }
        });
        if (alarm != null) {
            holder.wakeTimeTv.setText(alarm.toString());
            holder.editBedtimeAlarmEd.setText(alarm.getLabel());
            String[] weekDayArray = mContext.getResources().getStringArray(R.array.alarm_weekday);
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

    public void setExpandableClick(View v, final NormalAlarmViewHolder holder, int position) {
        ValueAnimator valueAnimation = null;
        if (normalItemHeight == 0) {
            normalItemHeight = holder.rootView.getHeight();
        }
        if (v.getId() == R.id.normal_open_expandable_ib) {
            holder.showInfoRl.setVisibility(View.GONE);
            holder.bottomLine.setVisibility(View.GONE);
            holder.rootView.setBackgroundColor(mContext.getResources().
                    getColor(R.color.bedtime_item_background_color));
            holder.expandable.setVisibility(View.VISIBLE);
            valueAnimation = ValueAnimator.ofInt(normalItemHeight, (int) (normalItemHeight * 2.3));
        } else if (v.getId() == R.id.close_edit_expandable_ib) {
            holder.rootView.setBackgroundColor(
                    mContext.getResources().getColor(R.color.window_background_color));
            holder.showInfoRl.setVisibility(View.VISIBLE);
            holder.bottomLine.setVisibility(View.VISIBLE);
            holder.expandable.setVisibility(View.GONE);
            valueAnimation = ValueAnimator.ofInt((int) (normalItemHeight * 2.3), normalItemHeight);
            onAlarmListener.onConfigChangeListener(holder.alarmSwitch.isChecked(),
                    holder.alarmNameTv.getText().toString(), alarmHour, alarmMinute, position);
        }
        valueAnimation.setDuration(150);
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

    public void setOnBedtimeConfigChangeListener(OnBedtimeConfigChangeListener onBedtimeConfigChangeListener) {
        this.onBedtimeListener = onBedtimeConfigChangeListener;
    }


    public void setBedtimeSwitchListener(OnBedtimeSwitchListener onAlarmSwitchedListener) {
        this.onAlarmSwitchedListener = onAlarmSwitchedListener;
    }


    public void setBedtimeDeleteListener(OnBedtimeDeleteListener listener) {
        this.listener = listener;
    }

    public interface OnBedtimeSwitchListener {
        void onBedtimeSwitch(SwitchCompat alarmSwitch, BedtimeModel alarm);
    }

    public interface OnBedtimeDeleteListener {
        void onBedtimeDelete(BedtimeModel bedtimeModel, int position);
    }


    public interface OnBedtimeConfigChangeListener {
        void onBedtimeConfigChangeListener(byte[] weekday, String name, int sleepHOur, int sleepMinute
                , int wakeHour, int wakeMinute, int sleepGoal, int position);
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
        void onConfigChangeListener(boolean checked, String s, int hour, int minute, int position);
    }
}

