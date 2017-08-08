package com.medcorp.lunar.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.medcorp.lunar.activity.EditAlarmActivity;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.adapter.AlarmRecyclerViewAdapter;
import com.medcorp.lunar.adapter.ShowAllSleepGoalAdapter;
import com.medcorp.lunar.event.bluetooth.RequestResponseEvent;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.ToastHelper;
import com.medcorp.lunar.view.picker.RadialPickerLayout;
import com.medcorp.lunar.view.picker.Utils;
import com.wdullaer.materialdatetimepicker.HapticFeedbackController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
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
public class AlarmFragment extends BaseObservableFragment
        implements RadialPickerLayout.OnValueSelectedListener, CompoundButton.OnCheckedChangeListener,
        AlarmRecyclerViewAdapter.OnBedtimeDeleteListener, AlarmRecyclerViewAdapter.OnNormalAlarmSwitchListener, AlarmRecyclerViewAdapter.OnDeleteNormalAlarmListener, AlarmRecyclerViewAdapter.OnBedtimeSwitchListener {

    @Bind(R.id.all_alarm_recycler_view)
    RecyclerView allAlarm;

    private List<Alarm> alarmList;
    private AlarmRecyclerViewAdapter mAlarmRecyclerViewAdapter;
    private List<BedtimeModel> allBedtimeModels;
    private boolean showSyncAlarm = false;
    private Alarm editAlarm;

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
    private String sleepLableGoal;
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
        allAlarm.setLayoutManager(new LinearLayoutManager(AlarmFragment.this.getContext()));
        mAlarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(getModel(), AlarmFragment.this.getContext(), alarmList, allBedtimeModels);
        mAlarmRecyclerViewAdapter.setBedtimeDeleteListener(AlarmFragment.this);
        mAlarmRecyclerViewAdapter.setBedtimeSwitchListener(AlarmFragment.this);
        mAlarmRecyclerViewAdapter.setDeleteNormalAlarmListener(AlarmFragment.this);
        mAlarmRecyclerViewAdapter.setNormalAlarmSwitchListener(AlarmFragment.this);
        allAlarm.setAdapter(mAlarmRecyclerViewAdapter);
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
                                alarmName = getString(R.string.def_alarm_name) + alarmList.size() + 1;
                            } else {
                                alarmName = editNewAlarmNameEd.getText().toString();
                            }
                            selectWeekdayDialog();
                        } else {
                            if (manyWeekday.size() > 0) {
                                if (editNewAlarmNameEd.getText().toString().isEmpty()) {
                                    alarmName = getString(R.string.def_bedtime_name) + allBedtimeModels.size() + 1;
                                } else {
                                    alarmName = editNewAlarmNameEd.getText().toString();
                                }
                                Collections.sort(manyWeekday);
                                byte[] weekday = new byte[manyWeekday.size()];
                                for (int i = 0; i < manyWeekday.size(); i++) {
                                    weekday[i] = (byte) manyWeekday.get(i).intValue();
                                }
                                manyWeekday.clear();
                                final BedtimeModel bedtimeModel = new BedtimeModel(alarmName, newBedtimeSleepGoal, weekday, mTimePicker.getHours(), mTimePicker.getMinutes(), weekday, true);
                                getModel().getBedTimeDatabaseHelper().add(bedtimeModel).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            allBedtimeModels.add(bedtimeModel);
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

    private void selectWeekdayDialog() {
        new MaterialDialog.Builder(AlarmFragment.this.getContext())
                .title(getString(R.string.edit_alarm_repeat))
                .items(getResources().getStringArray(R.array.week_day))
                .positiveText(R.string.goal_ok)
                .positiveColor(getResources().getColor(R.color.colorAccent))
                .negativeColor(getResources().getColor(R.color.colorAccent))
                .negativeText(R.string.goal_cancel)
                .itemsCallbackSingleChoice(7, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        final Alarm normalAlarm = new Alarm(mTimePicker.getHours(), mTimePicker.getMinutes(),
                                (byte) which, alarmName, (byte) (alarmList.size() + 7));
                        if (normalAlarm.getAlarmNumber() < 13) {
                            getModel().getAlarmDatabaseHelper().add(normalAlarm).subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        alarmList.add(normalAlarm);
                                        mAlarmRecyclerViewAdapter.notifyDataSetChanged();
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
                                if (input.length() == 0) {
                                    sleepLableGoal = getString(R.string.def_goal_sleep_name) + " " + (mAllSleepGoal.size() + 1);
                                } else {
                                    sleepLableGoal = input.toString();
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

    private void syncAlarmByEditor(boolean delete) {
        if (!getModel().isWatchConnected()) {
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        if (delete) {
            showSyncAlarm = true;
            ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
        } else {
            getModel().getAlarmById(editAlarm.getId(), new EditAlarmActivity.ObtainAlarmListener() {
                @Override
                public void obtainAlarm(Alarm alarm) {
                    editAlarm = alarm;
                    showSyncAlarm = true;
                    getModel().getSyncController().setAlarm(editAlarm);
                    ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
                }
            });
        }
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
        mTimePicker.initialize(AlarmFragment.this.getContext(), new HapticFeedbackController(AlarmFragment.this.getContext()), calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE), true, mMinHour, mMaxHour, mMinMinute, mMaxMinute);
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
                mTimePicker.setContentDescription(mMinutePickerDescription + ": " + newValue);
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
        alarm.setEnable(alarmSwitch.isChecked());
        Log.e("jason", alarmSwitch.isChecked() + "AAl");
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
                    getModel().getSyncController().setAlarm(alarm);
                }
            });
            getModel().getAlarmDatabaseHelper().obtainAlarm(alarmNumber[i] + 13).subscribe(new Consumer<Alarm>() {
                @Override
                public void accept(Alarm alarm) throws Exception {
                    alarm.setEnable(checked);
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
                    getModel().getAlarmDatabaseHelper().getAll().subscribe(new Consumer<List<Alarm>>() {
                        @Override
                        public void accept(List<Alarm> alarms) throws Exception {
                            for (final Alarm alarm : alarms) {
                                for (int i = 0; i < bedtimeModel.getAlarmNumber().length; i++) {
                                    if (alarm.getAlarmNumber() == bedtimeModel.getAlarmNumber()[i]) {
                                        if (bedtimeModel.getAlarmNumber()[i] < 7 | bedtimeModel.getAlarmNumber()[i] == bedtimeModel.getAlarmNumber()[i] + 13) {
                                            getModel().getAlarmDatabaseHelper().remove(alarm.getId()).subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean aBoolean) throws Exception {
                                                    getModel().getSyncController().setAlarm(alarm);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    });
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
                    alarmList.remove(position);
                    mAlarmRecyclerViewAdapter.notifyDataSetChanged();
                    getModel().getAlarmDatabaseHelper().get(id).subscribe(new Consumer<Alarm>() {
                        @Override
                        public void accept(Alarm alarm) throws Exception {
                            getModel().getSyncController().setAlarm(alarm);
                        }
                    });
                }
            }
        });
    }
}