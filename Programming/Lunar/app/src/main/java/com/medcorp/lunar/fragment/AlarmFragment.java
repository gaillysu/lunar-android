package com.medcorp.lunar.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.adapter.AlarmRecyclerViewAdapter;
import com.medcorp.lunar.adapter.NormalAlarmAdapter;
import com.medcorp.lunar.adapter.ShowAllSleepGoalAdapter;
import com.medcorp.lunar.event.bluetooth.RequestResponseEvent;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.util.PublicUtils;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.ToastHelper;
import com.medcorp.lunar.view.picker.RadialPickerLayout;
import com.medcorp.lunar.view.picker.Utils;
import com.wdullaer.materialdatetimepicker.HapticFeedbackController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.wdullaer.materialdatetimepicker.time.TimePickerDialog.HOUR_INDEX;
import static com.wdullaer.materialdatetimepicker.time.TimePickerDialog.MINUTE_INDEX;

/***
 * Created by karl-john on 11/12/15.
 */
public class AlarmFragment extends BaseObservableFragment implements CompoundButton.OnCheckedChangeListener,
        AlarmRecyclerViewAdapter.OnBedtimeDeleteListener, AlarmRecyclerViewAdapter.OnBedtimeSwitchListener,
         AlarmRecyclerViewAdapter.OnBedtimeConfigChangeListener, RadialPickerLayout.OnValueSelectedListener,
        NormalAlarmAdapter.OnDeleteNormalAlarmListener, NormalAlarmAdapter.OnNormalAlarmSwitchListener,
        NormalAlarmAdapter.OnAlarmConfigChangeListener {

    @Bind(R.id.all_bedtime_alarm_list_view)
    ListView bedtimeList;
    @Bind(R.id.all_normal_alarm_list_view)
    ListView normalList;

    private List<Alarm> alarmList;
    private AlarmRecyclerViewAdapter mAlarmRecyclerViewAdapter;
    private List<BedtimeModel> allBedtimeModels;
    private boolean showSyncAlarm = false;

    private Calendar calendar;
    private int mMinHour = 0;
    private int mMinMinute = 0;
    private int mMaxHour = 23;
    private int mMaxMinute = 59;
    private boolean mIs24HourMode = true;
    private boolean mAllowAutoAdvance = true;
    private RadialPickerLayout mTimePicker;
    private String mSelectHours;
    private String mSelectMinutes;
    private String mHourPickerDescription;
    private String mMinutePickerDescription;
    private static final String KEY_CURRENT_ITEM_SHOWING = "current_item_showing";
    private Bundle savedInstanceState;
    private static final int NORMAL_ALARM = 0x09;
    private static final int BEDTIME_ALARM = 0x08;
    private ListView mAllSleepGoalList;
    private ShowAllSleepGoalAdapter mGoalAdapter;
    private List<SleepGoal> mAllSleepGoal;
    private int newBedtimeSleepGoal = 0;
    private int selectHour = 0;
    private int selectMinutes = 0;
    private NormalAlarmAdapter normalAdaapter;

    private TextView mShowGoalText;
    private List<Integer> manyWeekday = new ArrayList<>();
    private String alarmName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        alarmList = new ArrayList<>();
        allBedtimeModels = new ArrayList<>();
        mAllSleepGoal = new ArrayList<>();
        this.savedInstanceState = savedInstanceState;
        initView();
        initData();
        return view;
    }

    private void initView() {
        mAlarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(AlarmFragment.this.getContext(),getModel(),allBedtimeModels);
        mAlarmRecyclerViewAdapter.setBedtimeDeleteListener(AlarmFragment.this);
        mAlarmRecyclerViewAdapter.setBedtimeSwitchListener(AlarmFragment.this);
        mAlarmRecyclerViewAdapter.setOnBedtimeConfigChangeListener(this);
        bedtimeList.setAdapter(mAlarmRecyclerViewAdapter);

        normalAdaapter = new NormalAlarmAdapter(AlarmFragment.this.getContext(),alarmList);
        normalAdaapter.setDeleteNormalAlarmListener(AlarmFragment.this);
        normalAdaapter.setNormalAlarmSwitchListener(AlarmFragment.this);
        normalAdaapter.setOnAlarmConfigChangeListener(AlarmFragment.this);
        normalList.setAdapter(normalAdaapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    private void initData() {
        getModel().getAlarmDatabaseHelper().getAll().subscribe(new Consumer<List<Alarm>>() {
            @Override
            public void accept(List<Alarm> alarms) throws Exception {
                for (Alarm alarm : alarms) {
                    if (alarm.getAlarmNumber() > 6 && alarm.getAlarmNumber() < 13) {
                        alarmList.add(alarm);
                    }
                }
                getModel().getBedTimeDatabaseHelper().getAll().subscribe(new Consumer<List<BedtimeModel>>() {
                    @Override
                    public void accept(List<BedtimeModel> bedtimeModels) throws Exception {
                        allBedtimeModels.addAll(bedtimeModels);
                        mAlarmRecyclerViewAdapter.notifyDataSetChanged();
                        normalAdaapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }

    @OnClick(R.id.alarm_fragment_add_new_normal_alarm_ft)
    public void addNewNormalAlarm() {
        showDialog(NORMAL_ALARM);
    }


    @OnClick(R.id.alarm_fragment_add_new_bedtime_alarm_ft)
    public void addNewBedtimeAlarm() {
        showDialog(BEDTIME_ALARM);
    }

    public void showDialog(final int type) {
        View inflate = LayoutInflater.from(AlarmFragment.this.getContext()).inflate(R.layout.add_normal_alarm_dialog_layout, null);
        mTimePicker = (RadialPickerLayout) inflate.findViewById(R.id.new_alarm_time_select_rp);
        LinearLayout allWeekdayLL = (LinearLayout) inflate.findViewById(R.id.add_new_alarm_dialog_weekday_ll);

        final EditText editNewAlarmNameEd = (EditText) inflate.findViewById(R.id.add_new_alarm_name);
        initPicker();
        final LinearLayout showGoalLL = (LinearLayout) inflate.findViewById(R.id.add_new_alarm_dialog_show_goal_ll);
        mShowGoalText = (TextView) inflate.findViewById(R.id.add_new_alarm_goal);
        String dialogTitle = null;
        if (type == NORMAL_ALARM) {
            showGoalLL.setVisibility(View.GONE);
            allWeekdayLL.setVisibility(View.GONE);
            dialogTitle = getString(R.string.add_new_normal_alarm_dialog_title);
        } else {
            showGoalLL.setVisibility(View.VISIBLE);
            allWeekdayLL.setVisibility(View.VISIBLE);
            dialogTitle = getString(R.string.add_new_bedtime_alarm_dialog_title);
        }
        getModel().getSleepGoalDatabseHelper().getSelectedGoal().subscribe(new Consumer<SleepGoal>() {
            @Override
            public void accept(SleepGoal sleepGoal) throws Exception {
                newBedtimeSleepGoal = sleepGoal.getGoalDuration();
                mShowGoalText.setText(sleepGoal.toString());
            }
        });
        showGoalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSleepGoalListDialog();
            }
        });
        initDialogView(inflate);
        new MaterialDialog.Builder(AlarmFragment.this.getContext())
                .title(dialogTitle)
                .titleColor(getResources().getColor(R.color.colorAccent))
                .backgroundColor(getResources().getColor(R.color.new_bedtime_dialog_background_color))
                .customView(inflate, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (type == NORMAL_ALARM) {
                            if (editNewAlarmNameEd.getText().toString().isEmpty()) {
                                alarmName = getString(R.string.def_alarm_name) + (alarmList.size() + 1);
                            } else {
                                alarmName = editNewAlarmNameEd.getText().toString();
                            }
                            selectWeekdayDialog();
                        } else {
                            if (manyWeekday.size() > 0) {
                                if (editNewAlarmNameEd.getText().toString().isEmpty()) {
                                    alarmName = getString(R.string.def_bedtime_name) + (allBedtimeModels.size() + 1);
                                } else {
                                    alarmName = editNewAlarmNameEd.getText().toString();
                                }
                                Collections.sort(manyWeekday);
                                byte[] weekday = new byte[manyWeekday.size()];
                                for (int i = 0; i < manyWeekday.size(); i++) {
                                    weekday[i] = (byte) manyWeekday.get(i).intValue();
                                }
                                manyWeekday.clear();
                                int[] time = PublicUtils.countTime(newBedtimeSleepGoal, mTimePicker.getHours(), mTimePicker.getMinutes(), weekday[0]);
                                int sleepTimeHour = time[0];
                                int sleepTimeMinute = time[1];
                                final BedtimeModel bedtimeModel = new BedtimeModel(alarmName, newBedtimeSleepGoal, weekday, sleepTimeHour, sleepTimeMinute, mTimePicker.getHours(), mTimePicker.getMinutes(), weekday, true);
                                getModel().getBedTimeDatabaseHelper().add(bedtimeModel).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            allBedtimeModels.add(bedtimeModel);
                                            createAlarm(bedtimeModel);
                                            mAlarmRecyclerViewAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            } else {
                                ((MainActivity) getActivity()).showStateString(R.string.edit_alarm_repeat_weekday, false);
                            }
                        }
                    }
                }).positiveText(R.string.goal_ok)
                .positiveColor(getResources().getColor(R.color.colorAccent))
                .negativeColor(getResources().getColor(R.color.colorAccent))
                .negativeText(R.string.goal_cancel)
                .show();
    }

    private void createAlarm(BedtimeModel bedtimeModel) {
        for (int i = 0; i < bedtimeModel.getWeekday().length; i++) {
            createWakeAlarm(bedtimeModel.getHour(), bedtimeModel.getMinute(), bedtimeModel.getWeekday()[i]
                    , bedtimeModel.getName(), bedtimeModel.getAlarmNumber()[i], bedtimeModel.isEnable());
            createSleepAlarm(bedtimeModel.getHour(), bedtimeModel.getMinute(), bedtimeModel.getWeekday()[i]
                    , bedtimeModel.getName(), bedtimeModel.getAlarmNumber()[i], bedtimeModel.getSleepGoal(), bedtimeModel.isEnable());
        }
    }

    private void createWakeAlarm(int hour, int minute, byte weekday, String name, byte alarmNumber, boolean isEnable) {
        final Alarm alarm = new Alarm(hour, minute, (byte) (0x80 | weekday), name, alarmNumber);
        alarm.setEnable(isEnable);
        getModel().getAlarmDatabaseHelper().add(alarm).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    getModel().getSyncController().setAlarm(alarm);
                }
            }
        });
    }

    private void createSleepAlarm(int hour, int minute, byte weekday, String name
            , byte alarmNumber, int sleepGoal, boolean enable) {
        int[] time = PublicUtils.countTime(sleepGoal, hour, minute, weekday);
        int hourOfDay = time[0];
        int minuteOfHour = time[1];
        int Weekday = time[2];
        final Alarm alarm = new Alarm(hourOfDay, minuteOfHour, (byte) (0x80 | Weekday), name, (byte) (alarmNumber + 13));
        alarm.setEnable(enable);
        getModel().getAlarmDatabaseHelper().add(alarm).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    getModel().getSyncController().setAlarm(alarm);
                }
            }
        });

    }

    private void selectWeekdayDialog() {
        new MaterialDialog.Builder(AlarmFragment.this.getContext())
                .title(getString(R.string.edit_alarm_repeat))
                .items(getResources().getStringArray(R.array.week_day))
                .positiveText(R.string.goal_ok)
                .positiveColor(getResources().getColor(R.color.colorAccent))
                .negativeColor(getResources().getColor(R.color.colorAccent))
                .negativeText(R.string.goal_cancel)
                .itemsCallbackSingleChoice(8, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        final Alarm normalAlarm = new Alarm(mTimePicker.getHours(), mTimePicker.getMinutes(),
                                (byte) (0x80 | which), alarmName, (byte) (alarmList.size() + 7));
                        if (normalAlarm.getAlarmNumber() < 13) {
                            getModel().getAlarmDatabaseHelper().add(normalAlarm).subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        alarmList.add(normalAlarm);
                                        normalAdaapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            return true;
                        } else {
                            ((MainActivity) getActivity()).showStateString(R.string.normal_alarm_max_toast, false);
                            return true;
                        }
                    }
                }).show();
    }

    private void initDialogView(View inflate) {
        final CheckBox sunday = (CheckBox) inflate.findViewById(R.id.bedtime_sunday);
        final CheckBox monday = (CheckBox) inflate.findViewById(R.id.bedtime_monday);
        final CheckBox tuesday = (CheckBox) inflate.findViewById(R.id.bedtime_tuesday);
        final CheckBox wednesday = (CheckBox) inflate.findViewById(R.id.bedtime_wednesday);
        final CheckBox thursday = (CheckBox) inflate.findViewById(R.id.bedtime_thursday);
        final CheckBox friday = (CheckBox) inflate.findViewById(R.id.bedtime_friday);
        final CheckBox saturday = (CheckBox) inflate.findViewById(R.id.bedtime_saturday);
        sunday.setOnCheckedChangeListener(this);
        monday.setOnCheckedChangeListener(this);
        tuesday.setOnCheckedChangeListener(this);
        wednesday.setOnCheckedChangeListener(this);
        thursday.setOnCheckedChangeListener(this);
        friday.setOnCheckedChangeListener(this);
        saturday.setOnCheckedChangeListener(this);
        getModel().getBedTimeDatabaseHelper().getAll().subscribe(new Consumer<List<BedtimeModel>>() {
            @Override
            public void accept(List<BedtimeModel> alarms) throws Exception {
                for (BedtimeModel bedtimeModel : alarms) {
                    for (int i = 0; i < bedtimeModel.getWeekday().length; i++) {
                        switch (bedtimeModel.getWeekday()[i]) {
                            case 0:
                                sunday.setClickable(false);
                                sunday.setBackground(getResources().getDrawable(R.drawable.shape_circle_weekday));
                                break;
                            case 1:
                                monday.setClickable(false);
                                monday.setBackground(getResources().getDrawable(R.drawable.shape_circle_weekday));
                                break;
                            case 2:
                                tuesday.setClickable(false);
                                tuesday.setBackground(getResources().getDrawable(R.drawable.shape_circle_weekday));
                                break;
                            case 3:
                                wednesday.setClickable(false);
                                wednesday.setBackground(getResources().getDrawable(R.drawable.shape_circle_weekday));
                                break;
                            case 4:
                                thursday.setClickable(false);
                                thursday.setBackground(getResources().getDrawable(R.drawable.shape_circle_weekday));
                                break;
                            case 5:
                                friday.setClickable(false);
                                friday.setBackground(getResources().getDrawable(R.drawable.shape_circle_weekday));
                                break;
                            case 6:
                                saturday.setClickable(false);
                                saturday.setBackground(getResources().getDrawable(R.drawable.shape_circle_weekday));
                                break;
                        }
                    }
                }
            }
        });

    }

    private void showSleepGoalListDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AlarmFragment.this.getContext());
        LayoutInflater inflater = LayoutInflater.from(AlarmFragment.this.getContext());
        View allGoalBottomView = inflater.inflate(R.layout.show_sleep_goal_bottom_dialog_view, null);
        mAllSleepGoalList = (ListView) allGoalBottomView.findViewById(R.id.show_all_sleep_goal_list);
        getModel().getSleepGoalDatabseHelper().getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(List<SleepGoal> sleepGoals) throws Exception {
                mAllSleepGoal.clear();
                mAllSleepGoal.addAll(sleepGoals);
                mGoalAdapter = new ShowAllSleepGoalAdapter(AlarmFragment.this.getContext(), sleepGoals);
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
                    mShowGoalText.setText(sleepGoal.toString());
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
        View selectTimeDialog = LayoutInflater.from(AlarmFragment.this.getContext()).inflate(R.layout.select_time_dialog_layou, null);
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

        new MaterialDialog.Builder(AlarmFragment.this.getContext()).customView(selectTimeDialog, false).
                title(getString(R.string.add_new_inactivity_goal_fb)).
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
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }


    public void addNewInactivity(final int hour, final int minute) {
        new MaterialDialog.Builder(AlarmFragment.this.getContext())
                .title(R.string.edit_goal_name)
                .content(R.string.goal_label_sleep)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.goal_name_goal_sleep), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String sleepLableGoal = input.toString();
                                if (input.length() == 0) {
                                    sleepLableGoal = getString(R.string.def_goal_sleep_name) + " " + (mAllSleepGoal.size() + 1);
                                }
                                final SleepGoal newSleepGoal = new SleepGoal(sleepLableGoal, hour * 60 + minute, false);
                                getModel().getSleepGoalDatabseHelper().add(newSleepGoal).subscribe(new Consumer<Boolean>() {
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
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(RequestResponseEvent event) {
        if (showSyncAlarm) {
            showSyncAlarm = false;
            int id = event.isSuccess() ? R.string.alarm_synced : R.string.alarm_error_sync;
            ((MainActivity) getActivity()).showStateString(id, false);
        }
    }

    private void initPicker() {
        calendar = Calendar.getInstance();
        mTimePicker.setOnValueSelectedListener(this);
        Resources res = getResources();
        mHourPickerDescription = res.getString(R.string.hour_picker_description);
        mSelectHours = res.getString(R.string.select_hours);
        mMinutePickerDescription = res.getString(R.string.minute_picker_description);
        mSelectMinutes = res.getString(R.string.select_minutes);
        mTimePicker.initialize(AlarmFragment.this.getContext(),
                new HapticFeedbackController(AlarmFragment.this.getContext()),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                true, mMinHour, mMaxHour, mMinMinute, mMaxMinute);
        mTimePicker.setCurrentItemShowing(calendar.HOUR_OF_DAY, true);
        int currentItemShowing = HOUR_INDEX;
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_CURRENT_ITEM_SHOWING)) {
            currentItemShowing = savedInstanceState.getInt(KEY_CURRENT_ITEM_SHOWING);
        }
        setCurrentItemShowing(currentItemShowing, false, true, true);
        mTimePicker.invalidate();
    }

    @Override
    public void onValueSelected(int pickerIndex, int newValue, boolean autoAdvance) {
        if (pickerIndex == HOUR_INDEX) {
            if (valueRespectsHoursConstraint(newValue)) {
                setHour(newValue, false);
                String announcement = String.format("%d", newValue);
                if (mAllowAutoAdvance && autoAdvance) {
                    setCurrentItemShowing(MINUTE_INDEX, true, true, false);
                    announcement += ". " + mSelectMinutes;
                } else {
                    mTimePicker.setContentDescription(mHourPickerDescription + ": " + newValue);
                }

                Utils.tryAccessibilityAnnounce(mTimePicker, announcement);
            }
        } else if (pickerIndex == MINUTE_INDEX) {
            if (valueRespectsMinutesConstraint(newValue)) {
                setMinute(newValue);
                String announcement = String.format("%d", newValue);
                mTimePicker.setContentDescription(mMinutePickerDescription + ": " + newValue);
                if (mAllowAutoAdvance && autoAdvance) {
                    setCurrentItemShowing(HOUR_INDEX, true, true, false);
                    announcement += ". " + mTimePicker.getHours();
                } else {
                    mTimePicker.setContentDescription(mHourPickerDescription + ": " + newValue);
                }
                Utils.tryAccessibilityAnnounce(mTimePicker, announcement);
            }
        }
    }

    private boolean valueRespectsMinutesConstraint(int value) {
        int hour = mTimePicker.getHours();
        boolean checkedMinMinute = true;
        boolean checkedMaxMinute = true;
        if (hour == mMinHour) {
            checkedMinMinute = (value >= mMinMinute);
        }
        if (hour == mMaxHour) {
            checkedMaxMinute = (value <= mMaxMinute);
        }
        return checkedMinMinute && checkedMaxMinute;
    }

    private void setMinute(int value) {
        if (value == 60) {
            value = 0;
        }
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        Utils.tryAccessibilityAnnounce(mTimePicker, text);
    }

    private void setCurrentItemShowing(int index, boolean animateCircle, boolean delayLabelAnimate,
                                       boolean announce) {
        mTimePicker.setCurrentItemShowing(index, animateCircle);
        if (index == HOUR_INDEX) {
            int hours = mTimePicker.getHours();
            if (!mIs24HourMode) {
                hours = hours % 12;
            }
            mTimePicker.setContentDescription(mHourPickerDescription + ": " + hours);
            if (announce) {
                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectHours);
            }
        } else {
            int minutes = mTimePicker.getMinutes();
            mTimePicker.setContentDescription(mMinutePickerDescription + ": " + minutes);
            if (announce) {
                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectMinutes);
            }
        }
    }

    private void setHour(int value, boolean announce) {
        String format;
        if (mIs24HourMode) {
            format = "%02d";
        } else {
            format = "%d";
            value = value % 12;
            if (value == 0) {
                value = 12;
            }
        }

        CharSequence text = String.format(format, value);
        if (announce) {
            Utils.tryAccessibilityAnnounce(mTimePicker, text);
        }
    }

    private boolean valueRespectsHoursConstraint(int value) {
        boolean respectsConstraint = (mMinHour <= value && mMaxHour >= value);
        return respectsConstraint;
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

    @Override
    public void onNormalAlarmSwitch(SwitchCompat alarmSwitch, Alarm alarm) {
        if (!getModel().isWatchConnected()) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        alarmSwitch.setChecked(alarmSwitch.isChecked());
        //save weekday to low 4 bit,bit 7 to save enable or disable
        alarm.setWeekDay(alarmSwitch.isChecked() ? (byte) (alarm.getWeekDay() | 0x80) : (byte) (alarm.getWeekDay() & 0x0F));
        alarm.setEnable(alarmSwitch.isChecked());
        getModel().updateAlarm(alarm);
        showSyncAlarm = true;
        getModel().getSyncController().setAlarm(alarm);
        ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
    }

    @Override
    public void onBedtimeSwitch(SwitchCompat alarmSwitch, BedtimeModel bedtime) {
        if (!getModel().isWatchConnected()) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        final byte[] alarmNumber = bedtime.getAlarmNumber();
        final boolean checked = alarmSwitch.isChecked();
        alarmSwitch.setChecked(checked);
        bedtime.setEnable(checked);
        getModel().getBedTimeDatabaseHelper().update(bedtime).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.i("jason", "success bedtime");
            }
        });
        for (int i = 0; i < alarmNumber.length; i++) {
            getModel().getAlarmDatabaseHelper().obtainAlarm(alarmNumber[i]).subscribe(new Consumer<Alarm>() {
                @Override
                public void accept(Alarm alarm) throws Exception {

                    alarm.setEnable(checked);
                    alarm.setWeekDay(checked ? (byte) (alarm.getWeekDay() | 0x80)
                            : (byte) (alarm.getWeekDay() & 0x0F));
                    getModel().getSyncController().setAlarm(alarm);
                }
            });
            getModel().getAlarmDatabaseHelper().obtainAlarm(alarmNumber[i] + 13).subscribe(new Consumer<Alarm>() {
                @Override
                public void accept(Alarm alarm) throws Exception {
                    alarm.setEnable(checked);
                    alarm.setWeekDay(checked ? (byte) (alarm.getWeekDay() | 0x80)
                            : (byte) (alarm.getWeekDay() & 0x0F));
                    getModel().getSyncController().setAlarm(alarm);
                }
            });
        }
    }


    @Override
    public void onBedtimeDelete(final BedtimeModel bedtimeModel, final int position) {
        ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
        getModel().getBedTimeDatabaseHelper().remove(bedtimeModel.getId()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    for (int i = 0; i < bedtimeModel.getAlarmNumber().length; i++) {
                        getModel().getAlarmDatabaseHelper().obtainAlarm(bedtimeModel.getAlarmNumber()[i]).subscribe(new Consumer<Alarm>() {
                            @Override
                            public void accept(Alarm alarm) throws Exception {
                                alarm.setWeekDay((byte) 0);
                                getModel().getSyncController().setAlarm(alarm);
                            }
                        });
                        getModel().getAlarmDatabaseHelper().obtainAlarm(bedtimeModel.getAlarmNumber()[i] + 13).subscribe(new Consumer<Alarm>() {
                            @Override
                            public void accept(Alarm alarm) throws Exception {
                                alarm.setWeekDay((byte) 0);
                                getModel().getSyncController().setAlarm(alarm);
                            }
                        });
                    }
                    allBedtimeModels.remove(position);
                    mAlarmRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onNormalAlarmDelete(final int id, final int position) {
        getModel().getAlarmDatabaseHelper().remove(id).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    getModel().getAlarmDatabaseHelper().get(id).subscribe(new Consumer<Alarm>() {
                        @Override
                        public void accept(Alarm alarm) throws Exception {
                            alarm.setWeekDay((byte) 0);
                            getModel().getSyncController().setAlarm(alarm);
                        }
                    });
                    alarmList.remove(position);
                    normalAdaapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onConfigChangeListener(boolean checked, String name, final int position) {
        final Alarm alarm = alarmList.get(position);
        alarm.setEnable(checked);
        alarm.setLabel(name);
        getModel().getAlarmDatabaseHelper().update(alarm).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    alarmList.remove(position);
                    alarmList.add(position, alarm);
                    normalAdaapter.notifyDataSetChanged();
                    getModel().getSyncController().setAlarm(alarm);
                }
            }
        });
    }

    @Override
    public void onBedtimeConfigChangeListener(byte[] weekday,final String name, int hour, int minute, final int position) {
        final BedtimeModel bedtimeModel = allBedtimeModels.get(position);
        bedtimeModel.setName(name);
        bedtimeModel.setWeekday(weekday);
        bedtimeModel.setSleepHour(hour);
        bedtimeModel.setSleepMinute(minute);
        getModel().getBedTimeDatabaseHelper().update(bedtimeModel).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    allBedtimeModels.remove(position);
                    allBedtimeModels.add(position, bedtimeModel);
                    mAlarmRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

        byte[] oldBedtimeAlarmWeekday = bedtimeModel.getWeekday();
        List<Byte> newBedtimeWeekday = new ArrayList(Arrays.asList(weekday));
        List<Byte> oldBedtimeWeekday = new ArrayList(Arrays.asList(oldBedtimeAlarmWeekday));
        for (int i = 0; i < oldBedtimeAlarmWeekday.length; i++) {
            if (!oldBedtimeWeekday.contains(weekday[i])) {
                Alarm wakeAlarm = new Alarm(bedtimeModel.getSleepHour(), bedtimeModel.getSleepMinute()
                        , (byte) (0x80 | weekday[i]), bedtimeModel.getName(), weekday[i]);
                int[] time = PublicUtils.countTime(bedtimeModel.getSleepGoal(), bedtimeModel.getSleepHour()
                        , bedtimeModel.getSleepMinute(), weekday[i]);
                int hourOfDay = time[0];
                int minuteOfHour = time[1];
                int Weekday = time[2];
                Alarm sleepAlarm = new Alarm(hourOfDay, minuteOfHour, (byte) (0x80 | Weekday), bedtimeModel.getName()
                        , (byte) (weekday[i] + 13));
                getModel().getSyncController().setAlarm(wakeAlarm);
                getModel().getSyncController().setAlarm(sleepAlarm);
            } else if (!newBedtimeWeekday.contains(oldBedtimeAlarmWeekday[i])) {
                getModel().getAlarmDatabaseHelper().obtainAlarm(oldBedtimeAlarmWeekday[i]).subscribe(new Consumer<Alarm>() {
                    @Override
                    public void accept(Alarm alarm) throws Exception {
                        getModel().getAlarmDatabaseHelper().remove(alarm.getId()).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Log.i("jason", "delete alarm");
                            }
                        });
                    }
                });
                getModel().getAlarmDatabaseHelper().obtainAlarm(oldBedtimeAlarmWeekday[i] + 13).subscribe(new Consumer<Alarm>() {
                    @Override
                    public void accept(Alarm alarm) throws Exception {
                        getModel().getAlarmDatabaseHelper().remove(alarm.getId()).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Log.i("jason", "delete alarm");
                            }
                        });
                    }
                });
            }
        }
    }
}