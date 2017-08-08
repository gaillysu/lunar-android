package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/***
 * Created by karl-john on 17/12/15.
 */
public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder>
        implements CompoundButton.OnCheckedChangeListener {

    private OnBedtimeSwitchListener onAlarmSwitchedListener;
    private OnDeleteNormalAlarmListener onDeleteNormalAlarmListener;
    private OnBedtimeDeleteListener listener;
    private OnNormalAlarmSwitchListener normalAlarmSwitchListener;
    private Context context;
    private List<BedtimeModel> bedtimeList;
    private List<Integer> manyWeekday;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private ApplicationModel model;
    private List<Alarm> normalAlarmList;
    private ListView mAllSleepGoalList;
    private Context mContext;
    private List<SleepGoal> mAllSleepGoal;
    private ShowAllSleepGoalAdapter mGoalAdapter;
    private int newBedtimeSleepGoal = 0;
    private int selectHour = 0;
    private String sleepLableGoal;
    private int selectMinutes = 0;
    private OnAlarmConfigChangeListener onAlarmListener;
    private OnBedtimeConfigChangeListener onBedtimeListener;

    public AlarmRecyclerViewAdapter(ApplicationModel model, Context context, List<Alarm> allNormalAlarm, List<BedtimeModel> bedtimeAllList) {
        this.model = model;
        mContext = context;
        this.normalAlarmList = allNormalAlarm;
        this.bedtimeList = bedtimeAllList;
        manyWeekday = new ArrayList<>();
        mAllSleepGoal = new ArrayList<>();
        for (int i = 0; i < normalAlarmList.size() + bedtimeAllList.size(); i++) {
            expandState.append(i, true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.fragment_alatm_list_item_bedtime, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        initView(holder, position);
        holder.expandable.initLayout();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.alarm_adapter_item_weekday_ll)
        LinearLayout weekdayLL;
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
        @Bind(R.id.fragment_alarm_list_view_item_bottom_line)
        View bottomLine;
        @Bind(R.id.edit_bedtime_alarm_goal_tv)
        TextView showBedtimeGoalTv;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void initView(final ViewHolder viewHolder, final int position) {
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
                                        viewHolder.sunday.setChecked(true);
                                        break;
                                    case 1:
                                        viewHolder.monday.setChecked(true);
                                        break;
                                    case 2:
                                        viewHolder.tuesday.setChecked(true);
                                        break;
                                    case 3:
                                        viewHolder.wednesday.setChecked(true);
                                        break;
                                    case 4:
                                        viewHolder.thursday.setChecked(true);
                                        break;
                                    case 5:
                                        viewHolder.friday.setChecked(true);
                                        break;
                                    case 6:
                                        viewHolder.saturday.setChecked(true);
                                        break;
                                }
                            }
                        } else {
                            for (int i = 0; i < bedtimeModel.getWeekday().length; i++) {
                                switch (bedtimeModel.getWeekday()[i]) {
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
                    }
                }
            });
            viewHolder.weekdayLL.setVisibility(View.VISIBLE);
        } else {
            viewHolder.weekdayLL.setVisibility(View.GONE);
        }
    }

    private void setDataForItem(final ViewHolder viewHolder, final int position) {
        //init title
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
            viewHolder.weekdayLL.setVisibility(View.VISIBLE);
            viewHolder.settingBedTimeGoal.setVisibility(View.VISIBLE);
            setBedtimeData(viewHolder, position);
        } else if (position > bedtimeList.size() - 1 && normalAlarmList.size() > 0) {
            viewHolder.weekdayLL.setVisibility(View.GONE);
            viewHolder.settingBedTimeGoal.setVisibility(View.GONE);
            setNormalAlarmData(viewHolder, position - bedtimeList.size());
        }

        viewHolder.setIsRecyclable(false);
        viewHolder.expandable.setInRecyclerView(true);
        viewHolder.expandable.setExpanded(expandState.get(position));
        viewHolder.expandable.setInterpolator(Utils.createInterpolator(Utils.LINEAR_OUT_SLOW_IN_INTERPOLATOR));
        viewHolder.expandable.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                viewHolder.showInfoRl.setVisibility(View.GONE);
                viewHolder.bottomLine.setVisibility(View.GONE);
                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.bedtime_item_background_color));
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.window_background_color));
                viewHolder.showInfoRl.setVisibility(View.VISIBLE);
                viewHolder.bottomLine.setVisibility(View.VISIBLE);
                expandState.put(position, false);
            }
        });
        viewHolder.openExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(viewHolder.expandable);
            }
        });
        viewHolder.closeExpandableIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(viewHolder.expandable);
                if (position < bedtimeList.size()) {
                    saveBedtimeChangeConfig(viewHolder, position);
                } else if (position > bedtimeList.size() - 1 && normalAlarmList.size() > 0) {
                    saveNormalAlarmChangeConfig(viewHolder, position - bedtimeList.size());
                }
            }
        });

        viewHolder.settingBedTimeGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSleepGoalListDialog(viewHolder);
            }
        });
        viewHolder.sunday.setOnCheckedChangeListener(this);
        viewHolder.monday.setOnCheckedChangeListener(this);
        viewHolder.tuesday.setOnCheckedChangeListener(this);
        viewHolder.wednesday.setOnCheckedChangeListener(this);
        viewHolder.thursday.setOnCheckedChangeListener(this);
        viewHolder.friday.setOnCheckedChangeListener(this);
        viewHolder.saturday.setOnCheckedChangeListener(this);
    }

    private void saveNormalAlarmChangeConfig(final ViewHolder viewHolder, int position) {
        onAlarmListener.onConfigChangeListener(viewHolder.alarmSwitch.isChecked(), viewHolder.alarmNameTv.getText().toString(), position);
    }

    private void saveBedtimeChangeConfig(ViewHolder viewHolder, final int position) {
        Collections.sort(manyWeekday);
        byte[] weekday = new byte[manyWeekday.size()];
        for (int i = 0; i < manyWeekday.size(); i++) {
            weekday[i] = (byte) manyWeekday.get(i).intValue();
        }
        onBedtimeListener.onBedtimeConfigChangeListener(weekday, newBedtimeSleepGoal,
                viewHolder.alarmNameTv.getText().toString(), position);

    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    private void setNormalAlarmData(ViewHolder viewHolder, final int position) {
        final Alarm alarm = normalAlarmList.get(position);
        byte[] weekday = new byte[normalAlarmList.size()];
        for (int i = 0; i < normalAlarmList.size(); i++) {
            weekday[i] = normalAlarmList.get(i).getWeekDay();
        }
        if (alarm != null) {
            viewHolder.editBedtimeAlarmEd.setText(alarm.getLabel());
            viewHolder.repeatWeekDayTv.setText(obtainWeekday(new byte[]{alarm.getWeekDay()}));
            viewHolder.sleepTimeTv.setText(alarm.toString());
            viewHolder.alarmNameTv.setText(alarm.getLabel());
            viewHolder.alarmSwitch.setChecked(alarm.isEnable());
            viewHolder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    normalAlarmSwitchListener.onNormalAlarmSwitch((SwitchCompat) buttonView, alarm);
                }
            });

            viewHolder.deleteBedtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteNormalAlarmListener.onNormalAlarmDelete(alarm.getId(), position);
                }
            });
        }
    }

    private void setBedtimeData(ViewHolder viewHolder, final int position) {
        final BedtimeModel bedtimeModel = bedtimeList.get(position);
        if (bedtimeModel != null) {
            viewHolder.editBedtimeAlarmEd.setText(bedtimeModel.getName());
            viewHolder.repeatWeekDayTv.setText("");
            viewHolder.repeatWeekDayTv.setText(obtainWeekday(bedtimeModel.getWeekday()));
            viewHolder.alarmNameTv.setText(bedtimeModel.getName());
            viewHolder.showBedtimeGoalTv.setText(bedtimeModel.getGoalString());
            viewHolder.sleepTimeTv.setText(bedtimeModel.getSellpTime());
            viewHolder.wakeTimeTv.setText(bedtimeModel.toString());
            viewHolder.alarmSwitch.setChecked(bedtimeModel.isEnable());
            viewHolder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onAlarmSwitchedListener.onBedtimeSwitch((SwitchCompat) buttonView, bedtimeModel);
                }
            });

            viewHolder.deleteBedtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBedtimeDelete(bedtimeModel, position);
                }
            });
        }
    }

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.bedtime_sunday:
                if (isChecked) {
                    if (!manyWeekday.contains(1)) {
                        manyWeekday.add(0);
                    }
                }
                break;
            case R.id.bedtime_monday:
                if (isChecked) {
                    if (!manyWeekday.contains(1)) {
                        manyWeekday.add(1);
                    }
                }
                break;
            case R.id.bedtime_tuesday:
                if (isChecked) {
                    if (!manyWeekday.contains(1)) {
                        manyWeekday.add(2);
                    }
                }
                break;
            case R.id.bedtime_wednesday:
                if (isChecked) {
                    if (!manyWeekday.contains(1)) {
                        manyWeekday.add(3);
                    }
                }
                break;
            case R.id.bedtime_thursday:
                if (isChecked) {
                    if (!manyWeekday.contains(1)) {
                        manyWeekday.add(4);
                    }
                }
                break;
            case R.id.bedtime_friday:
                if (isChecked) {
                    if (!manyWeekday.contains(1)) {
                        manyWeekday.add(5);
                    }
                }
                break;
            case R.id.bedtime_saturday:
                if (isChecked) {
                    if (!manyWeekday.contains(1)) {
                        manyWeekday.add(6);
                    }
                }
                break;
        }
    }

    private void showSleepGoalListDialog(final ViewHolder viewHolder) {
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
                    newBedtimeSleepGoal = sleepGoal.getGoalDuration();
                    viewHolder.showBedtimeGoalTv.setText(sleepGoal.toString());
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
        void onBedtimeConfigChangeListener(byte[] weekday, int newBedtimeSleepGoal, String s, int position);
    }

    public interface OnAlarmConfigChangeListener {
        void onConfigChangeListener(boolean checked, String s, int position);
    }
}
