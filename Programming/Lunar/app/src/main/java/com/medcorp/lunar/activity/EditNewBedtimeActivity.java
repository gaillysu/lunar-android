package com.medcorp.lunar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.util.PublicUtils;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by Jason on 2017/6/7.
 */

public class EditNewBedtimeActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.new_alarm_name)
    TextView newAlarmName;
    @Bind(R.id.bedtime_sleep_goal)
    TextView sleepGoalTv;

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

    @Bind(R.id.select_alarm_morning_or_afternoon)
    PickerView selectAmOrPm;
    @Bind(R.id.select_alarm_hour)
    PickerView selectHour;
    @Bind(R.id.select_alarm_minute)
    PickerView selectMinute;

    private String alarmName;
    private List<Integer> manyWeekday;
    private int hourOfDay = 8;
    private int minuteOfHour = 0;
    private byte[] alarmNumber;
    private byte[] weekday;
    private int sleepGoal = 480;
    private boolean isMorning = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_new_bedtime_activity);
        ButterKnife.bind(this);
        manyWeekday = new ArrayList<>();
        initToolbar();
        initView();
        initData();
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        toolbarTitle.setText(getString(R.string.edit_new_bedtime_alarm));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.done_menu).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        getModel().getBedTimeDatabaseHelper().getAll().subscribe(new Consumer<List<BedtimeModel>>() {
            @Override
            public void accept(List<BedtimeModel> alarms) throws Exception {
                setCheckboxClickable(alarms);
            }
        });
        monday.setOnCheckedChangeListener(this);
        sunday.setOnCheckedChangeListener(this);
        tuesday.setOnCheckedChangeListener(this);
        wednesday.setOnCheckedChangeListener(this);
        thursday.setOnCheckedChangeListener(this);
        friday.setOnCheckedChangeListener(this);
        saturday.setOnCheckedChangeListener(this);
    }

    private void setCheckboxClickable(List<BedtimeModel> allBedtime) {
        List<Byte> allWeekday = new ArrayList<>();
        for (BedtimeModel bedtimeModel : allBedtime) {
            byte[] weekday = bedtimeModel.getWeekday();
            for (int i = 0; i < weekday.length; i++) {
                allWeekday.add(weekday[i]);
            }
        }
        for (int i = 0; i < allWeekday.size(); i++) {
            switch (allWeekday.get(i)) {
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

    private void initData() {
        List<String> tabList = new ArrayList<>();
        final List<String> hourList = new ArrayList<>();
        List<String> minuteList = new ArrayList<>();
        tabList.add(getString(R.string.time_able_morning));
        tabList.add(getString(R.string.time_able_afternoon));
        for (int i = 0; i <= 12; i++) {
            if (i < 10) {
                hourList.add("0" + i);
            } else {
                hourList.add(i + "");
            }
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minuteList.add("0" + i);
            } else {
                minuteList.add(i + "");
            }
        }

        selectAmOrPm.setData(tabList);
        selectHour.setData(hourList);
        selectMinute.setData(minuteList);
        selectAmOrPm.setTextColor(0xFFFFFF);
        selectHour.setTextColor(0xFFFFFF);
        selectMinute.setTextColor(0xFFFFFF);
        selectAmOrPm.setSelected(0);
        selectHour.setSelected(8);
        selectMinute.setSelected(0);
        selectAmOrPm.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                if (text.equals(getString(R.string.time_able_morning))) {
                    isMorning = true;
                } else {
                    isMorning = false;
                }
            }
        });
        selectHour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                if (!isMorning) {
                    hourOfDay = new Integer(text).intValue() + 12;
                } else {
                    hourOfDay = new Integer(text).intValue();
                }
            }
        });
        selectMinute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minuteOfHour = new Integer(text).intValue();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(-1);
                finish();
                break;
            case R.id.done_menu:
                alarmNumber = new byte[manyWeekday.size()];
                weekday = new byte[manyWeekday.size()];
                for (int i = 0; i < manyWeekday.size(); i++) {
                    Integer integer = manyWeekday.get(i);
                    weekday[i] = (byte) integer.intValue();
                    alarmNumber[i] = (byte) integer.intValue();
                }
                if (wednesday.length() > 0) {
                    BedtimeModel bedtimeModel = new BedtimeModel(alarmName, sleepGoal, alarmNumber, hourOfDay
                            , minuteOfHour, weekday, false);
                    getModel().getBedTimeDatabaseHelper().add(bedtimeModel).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            setResult(-1);
                            finish();
                        }
                    });
                }else{
                    ToastHelper.showShortToast(EditNewBedtimeActivity.this,getString(R.string.bedtime_set_week_day));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.edit_new_alarm_name_bt)
    public void editNewAlarmName() {
        new MaterialDialog.Builder(this)
                .title(R.string.alarm_edit)
                .content(getString(R.string.alarm_label_alarm))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.alarm_label), "Alarm", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.length() == 0)
                            return;
                        alarmName = input.toString();
                        newAlarmName.setText(input.toString());
                    }
                }).negativeText(R.string.alarm_cancel)
                .show();
    }

    @OnClick(R.id.edit_new_bedtime_sleep_goal_bt)
    public void editBedtimeSleepGoal() {
        getModel().getSleepDatabseHelper().getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(final List<SleepGoal> sleepGoals) throws Exception {
                List<String> stringList = new ArrayList<>();
                final List<SleepGoal> stepsGoalEnableList = new ArrayList<>();
                int selectIndex = 0;
                for (int i = 0; i < sleepGoals.size(); i++) {
                    SleepGoal sleepGoal = sleepGoals.get(i);
                    if (sleepGoal.isStatus()) {
                        selectIndex = i;
                    }
                    stringList.add(PublicUtils.obtainString(EditNewBedtimeActivity.this,
                            sleepGoal.getGoalName(), sleepGoal.getGoalDuration()));
                    stepsGoalEnableList.add(sleepGoal);

                }
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);

                if (sleepGoals.size() != 0) {
                    new MaterialDialog.Builder(EditNewBedtimeActivity.this)
                            .title(R.string.def_goal_sleep_name).itemsColor(getResources().getColor(R.color.edit_alarm_item_text_color))
                            .items(cs)
                            .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if (which >= 0) {
                                        sleepGoal = sleepGoals.get(which).getGoalDuration();
                                        sleepGoalTv.setText(PublicUtils.countTime
                                                (EditNewBedtimeActivity.this, sleepGoal));
                                    }
                                    return true;
                                }
                            })
                            .positiveText(R.string.goal_ok)
                            .negativeText(R.string.goal_cancel).contentColorRes(R.color.left_menu_item_text_color)
                            .show();
                }
            }
        });
    }
}
