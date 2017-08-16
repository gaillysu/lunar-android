package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

/***
 * Created by karl-john on 17/12/15.
 */
public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter {

    private OnBedtimeSwitchListener onAlarmSwitchedListener;
    private OnDeleteNormalAlarmListener onDeleteNormalAlarmListener;
    private OnBedtimeDeleteListener listener;
    private OnNormalAlarmSwitchListener normalAlarmSwitchListener;
    private Context context;
    private List<BedtimeModel> bedtimeList;
    private ApplicationModel model;
    private List<Alarm> normalAlarmList;
    private ListView mAllSleepGoalList;
    private Context mContext;
    private List<SleepGoal> mAllSleepGoal;
    private ShowAllSleepGoalAdapter mGoalAdapter;
    private int selectHour = 0;
    private String sleepLableGoal;
    private int selectMinutes = 0;
    private OnAlarmConfigChangeListener onAlarmListener;
    private OnBedtimeConfigChangeListener onBedtimeListener;
    private static final int BEDTIME_UI_TYPE = 0x02 << 3;
    private static final int NORMAL_UI_TYPE = 0x03 << 3;

    public AlarmRecyclerViewAdapter(ApplicationModel model, Context context, List<Alarm> allNormalAlarm, List<BedtimeModel> bedtimeAllList) {
        this.model = model;
        mContext = context;
        this.normalAlarmList = allNormalAlarm;
        this.bedtimeList = bedtimeAllList;
        mAllSleepGoal = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (viewType == BEDTIME_UI_TYPE) {
            return new BedtimeViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.fragment_alatm_list_item_bedtime, parent, false));
        } else {
            return new NormalAlarmViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.fragment_alatm_list_item_normal, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < bedtimeList.size()) {
            ((BedtimeViewHolder) holder).initView(position);
            ((BedtimeViewHolder) holder).setBedtimeData(position);
            ((BedtimeViewHolder) holder).setDataForItem(position);

        } else {
            ((NormalAlarmViewHolder) holder).initView(position - bedtimeList.size());
            ((NormalAlarmViewHolder) holder).setNormalAlarmData(position - bedtimeList.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < bedtimeList.size()) {
            return BEDTIME_UI_TYPE;
        } else {
            return NORMAL_UI_TYPE;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bedtimeList.size() + normalAlarmList.size();
    }


    class NormalAlarmViewHolder extends RecyclerView.ViewHolder {
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
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void initView(final int position) {
            openExpandableIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInfoRl.setVisibility(View.GONE);
                    bottomLine.setVisibility(View.GONE);
                    rootView.setBackgroundColor(context.getResources().getColor(R.color.bedtime_item_background_color));
                }
            });
            closeExpandableIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.setBackgroundColor(context.getResources().getColor(R.color.window_background_color));
                    showInfoRl.setVisibility(View.VISIBLE);
                    bottomLine.setVisibility(View.VISIBLE);
                    saveNormalAlarmChangeConfig(alarmSwitch.isChecked(),
                            alarmNameTv.getText().toString() != null ? alarmNameTv.getText().toString()
                                    : normalAlarmList.get(position).getLabel(), position);
                }
            });
        }


        private void setNormalAlarmData(final int position) {
            final Alarm alarm = normalAlarmList.get(position);
            byte[] weekday = new byte[normalAlarmList.size()];
            for (int i = 0; i < normalAlarmList.size(); i++) {
                weekday[i] = normalAlarmList.get(i).getWeekDay();
            }
            if ((alarm.getWeekDay() & 0x80) == 0) {
                alarmSwitch.setChecked(false);
            } else {
                alarmSwitch.setChecked(true);
            }
            if (alarm != null) {
                editBedtimeAlarmEd.setText(alarm.getLabel());
                String[] weekDayArray = context.getResources().getStringArray(R.array.alarm_week_day);
                repeatWeekDayTv.setText(weekDayArray[alarm.getWeekDay() & 0x0F]);
                alarmNameTv.setText(alarm.getLabel());
                alarmSwitch.setChecked(!((alarm.getWeekDay() & 0x0F) == 0));
                alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        normalAlarmSwitchListener.onNormalAlarmSwitch((SwitchCompat) buttonView, alarm);
                    }
                });

                deleteBedtime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteNormalAlarmListener.onNormalAlarmDelete(alarm.getId(), position);
                    }
                });
            }
        }
    }

    private void saveNormalAlarmChangeConfig(boolean isChecked, String name, int position) {
        onAlarmListener.onConfigChangeListener(isChecked, name, position);
    }


    class BedtimeViewHolder extends RecyclerView.ViewHolder {
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

        public BedtimeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void initView(final int position) {
            if (position < bedtimeList.size()) {
                final int id = bedtimeList.get(position).getId();
                model.getBedTimeDatabaseHelper().getAll().subscribe(new Consumer<List<BedtimeModel>>() {
                    @Override
                    public void accept(List<BedtimeModel> alarms) throws Exception {
                        for (BedtimeModel bedtimeModel : alarms) {
                            if (bedtimeModel.getId() == id) {
                                for (int i = 0; i < bedtimeModel.getWeekday().length; i++) {
                                    switch (bedtimeModel.getWeekday()[i]) {
                                        case 0:
                                            sunday.setChecked(true);
                                            break;
                                        case 1:
                                            monday.setChecked(true);
                                            break;
                                        case 2:
                                            tuesday.setChecked(true);
                                            break;
                                        case 3:
                                            wednesday.setChecked(true);
                                            break;
                                        case 4:
                                            thursday.setChecked(true);
                                            break;
                                        case 5:
                                            friday.setChecked(true);
                                            break;
                                        case 6:
                                            saturday.setChecked(true);
                                            break;
                                    }
                                }
                            } else {
                                for (int i = 0; i < bedtimeModel.getWeekday().length; i++) {
                                    switch (bedtimeModel.getWeekday()[i]) {
                                        case 0:
                                            sunday.setClickable(false);
                                            sunday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                                            break;
                                        case 1:
                                            monday.setClickable(false);
                                            monday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                                            break;
                                        case 2:
                                            tuesday.setClickable(false);
                                            tuesday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                                            break;
                                        case 3:
                                            wednesday.setClickable(false);
                                            wednesday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                                            break;
                                        case 4:
                                            thursday.setClickable(false);
                                            thursday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                                            break;
                                        case 5:
                                            friday.setClickable(false);
                                            friday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                                            break;
                                        case 6:
                                            saturday.setClickable(false);
                                            saturday.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_weekday));
                                            break;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        public void setDataForItem(int position) {
            //init title
            if (bedtimeList.size() > 0 && position == 0) {
                titleTv.setVisibility(View.VISIBLE);
            } else {
                titleTv.setVisibility(View.GONE);
            }
        }

        private void setBedtimeData(final int position) {
            final BedtimeModel bedtimeModel = bedtimeList.get(position);
            if (bedtimeModel != null) {
                editBedtimeAlarmEd.setText(bedtimeModel.getName());
                repeatWeekDayTv.setText("");
                repeatWeekDayTv.setText(obtainWeekday(bedtimeModel.getWeekday()));
                alarmNameTv.setText(bedtimeModel.getName());
                showBedtimeGoalTv.setText(bedtimeModel.getGoalString());
                sleepTimeTv.setText(bedtimeModel.getSellpTime());
                wakeTimeTv.setText(bedtimeModel.toString());
                alarmSwitch.setChecked(bedtimeModel.isEnable());
                alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        onAlarmSwitchedListener.onBedtimeSwitch((SwitchCompat) buttonView, bedtimeModel);
                    }
                });

                deleteBedtime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onBedtimeDelete(bedtimeModel, position);
                    }
                });
            }
            openExpandableIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showInfoRl.setVisibility(View.GONE);
                    bottomLine.setVisibility(View.GONE);
                    rootView.setBackgroundColor(context.getResources().getColor(R.color.bedtime_item_background_color));
                }
            });
            closeExpandableIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.setBackgroundColor(context.getResources().getColor(R.color.window_background_color));
                    showInfoRl.setVisibility(View.VISIBLE);
                    bottomLine.setVisibility(View.VISIBLE);
                    saveBedtimeChangeConfig(position);
                }
            });

            settingBedTimeGoal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSleepGoalListDialog(showBedtimeGoalTv, position);
                }
            });
        }

        private void saveBedtimeChangeConfig(final int position) {
            BedtimeModel bedtimeModel = bedtimeList.get(position);
            int[] sleepTime = PublicUtils.countTime(bedtimeModel.getSleepGoal(),
                    bedtimeModel.getHour(), bedtimeModel.getMinute(), bedtimeModel.getWeekday()[0]);
            onBedtimeListener.onBedtimeConfigChangeListener(getWeekday(),
                    editBedtimeAlarmEd.getText().toString() != null
                            ? editBedtimeAlarmEd.getText().toString()
                            : bedtimeModel.getName(), sleepTime[0], sleepTime[1], position);
        }

        private byte[] getWeekday() {
            List<Byte> manyWeekday = new ArrayList<>();
            if (sunday.isChecked())
                manyWeekday.add((byte) 0);
            if (monday.isChecked())
                manyWeekday.add((byte) 1);
            if (tuesday.isChecked())
                manyWeekday.add((byte) 2);
            if (wednesday.isChecked())
                manyWeekday.add((byte) 3);
            if (thursday.isChecked())
                manyWeekday.add((byte) 4);
            if (friday.isChecked())
                manyWeekday.add((byte) 5);
            if (saturday.isChecked())
                manyWeekday.add((byte) 6);
            byte[] weekday = new byte[manyWeekday.size()];
            for (int i = 0; i < manyWeekday.size(); i++) {
                weekday[i] = manyWeekday.get(i);
            }
            return weekday;
        }
    }


    //    private void setDataForItem(final RecyclerView.ViewHolder viewHolder, final int position) {
    //        //init title
    //        if (bedtimeList.size() > 0 && position == 0) {
    //           titleTv.setVisibility(View.VISIBLE);
    //            titleTv.setText(context.getString(R.string.append_new_alarm_bedtime));
    //        } else if (normalAlarmList.size() > 0 && position == bedtimeList.size()) {
    //           titleTv.setVisibility(View.VISIBLE);
    //            titleTv.setText(context.getString(R.string.append_new_alarm_normal));
    //        } else {
    //            titleTv.setVisibility(View.GONE);
    //        }
    //
    //
    //        if (normalAlarmList.size() != 0 && position > bedtimeList.size() - 1) {
    //            viewHolder.wakeUpIv.setVisibility(View.GONE);
    //            viewHolder.wakeTimeTv.setVisibility(View.GONE);
    //        }
    //
    //        if (position < bedtimeList.size()) {
    //            viewHolder.weekdayLL.setVisibility(View.VISIBLE);
    //            viewHolder.settingBedTimeGoal.setVisibility(View.VISIBLE);
    //            setBedtimeData(viewHolder, position);
    //
    //        } else if (position > bedtimeList.size() - 1 && normalAlarmList.size() > 0) {
    //            viewHolder.weekdayLL.setVisibility(View.GONE);
    //            viewHolder.settingBedTimeGoal.setVisibility(View.GONE);
    //            setNormalAlarmData(viewHolder, position - bedtimeList.size());
    //        }
    //
    //        viewHolder.setIsRecyclable(false);
    //        viewHolder.expandable.setInRecyclerView(true);
    //        viewHolder.expandable.setExpanded(expandState.get(position));
    //        viewHolder.expandable.setInterpolator(Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR));
    //        viewHolder.expandable.setListener(new ExpandableLayoutListenerAdapter() {
    //            @Override
    //            public void onPreOpen() {
    //                viewHolder.showInfoRl.setVisibility(View.GONE);
    //                viewHolder.bottomLine.setVisibility(View.GONE);
    //                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.bedtime_item_background_color));
    //                expandState.put(position, true);
    //            }
    //
    //            @Override
    //            public void onPreClose() {
    //                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.window_background_color));
    //                viewHolder.showInfoRl.setVisibility(View.VISIBLE);
    //                viewHolder.bottomLine.setVisibility(View.VISIBLE);
    //                expandState.put(position, false);
    //            }
    //        });
    //        viewHolder.openExpandableIb.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                onClickButton(viewHolder.expandable);
    //            }
    //        });
    //        viewHolder.closeExpandableIb.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                onClickButton(viewHolder.expandable);
    //                if (position < bedtimeList.size()) {
    //                    saveBedtimeChangeConfig(viewHolder, position);
    //                } else if (position > bedtimeList.size() - 1 && normalAlarmList.size() > 0) {
    //                    saveNormalAlarmChangeConfig(viewHolder, position - bedtimeList.size());
    //                }
    //            }
    //        });
    //
    //        viewHolder.settingBedTimeGoal.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                showSleepGoalListDialog(viewHolder, position);
    //            }
    //        });
    //    }


    public String obtainWeekday(byte[] weekday) {
        String[] weekDayArray = context.getResources().getStringArray(R.array.alarm_week_day);
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

    private void showSleepGoalListDialog(final TextView showBedtimeGoalTv, int position) {
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
        mAllSleepGoalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bottomSheetDialog.dismiss();
                SleepGoal sleepGoal = mAllSleepGoal.get(position);
                if (sleepGoal != null) {
                    if (position < bedtimeList.size()) {
                        showBedtimeGoalTv.setText(sleepGoal.toString());
                        final BedtimeModel bedtimeModel = bedtimeList.get(position);
                        bedtimeModel.setSleepGoal(sleepGoal.getGoalDuration());
                        model.getBedTimeDatabaseHelper().update(bedtimeModel).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Log.i("jason", "bedtime sleep goal update");
                            }
                        });
                    }
                }
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

    public void setOnBedtimeConfigChangeListener(OnBedtimeConfigChangeListener onBedtimeConfigChangeListener) {
        this.onBedtimeListener = onBedtimeConfigChangeListener;
    }

    public void setOnAlarmConfigChangeListener(OnAlarmConfigChangeListener onConfigChangeListener) {
        this.onAlarmListener = onConfigChangeListener;
    }

    public void setBedtimeSwitchListener(OnBedtimeSwitchListener onAlarmSwitchedListener) {
        this.onAlarmSwitchedListener = onAlarmSwitchedListener;
    }

    public void setBedtimeDeleteListener(OnBedtimeDeleteListener listener) {
        this.listener = listener;
    }

    public void setNormalAlarmSwitchListener(OnNormalAlarmSwitchListener normalAlarmSwitchListener) {
        this.normalAlarmSwitchListener = normalAlarmSwitchListener;
    }

    public void setDeleteNormalAlarmListener(OnDeleteNormalAlarmListener onDeleteNormalAlarmListener) {
        this.onDeleteNormalAlarmListener = onDeleteNormalAlarmListener;
    }

    public interface OnBedtimeSwitchListener {
        void onBedtimeSwitch(SwitchCompat alarmSwitch, BedtimeModel alarm);
    }

    public interface OnNormalAlarmSwitchListener {
        void onNormalAlarmSwitch(SwitchCompat alarmSwitch, Alarm alarm);
    }

    public interface OnBedtimeDeleteListener {
        void onBedtimeDelete(BedtimeModel bedtimeModel, int position);
    }

    public interface OnDeleteNormalAlarmListener {
        void onNormalAlarmDelete(int id, int position);
    }

    public interface OnBedtimeConfigChangeListener {
        void onBedtimeConfigChangeListener(byte[] weekday, String name, int sleepHOur, int sleepMinute, int position);
    }

    public interface OnAlarmConfigChangeListener {
        void onConfigChangeListener(boolean checked, String s, int position);
    }
}
