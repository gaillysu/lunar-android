package com.medcorp.lunar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.controller.SyncControllerImpl;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/***
 * Created by Jason on 2017/6/5.
 */

public class EditNewAlarmActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.new_alarm_name)
    TextView newAlarmName;
    @Bind(R.id.new_alarm_week_day)
    TextView weekdayTv;

    @Bind(R.id.select_alarm_morning_or_afternoon)
    PickerView selectAmOrPm;
    @Bind(R.id.select_alarm_hour)
    PickerView selectHour;
    @Bind(R.id.select_alarm_minute)
    PickerView selectMinute;

    private String alarmName;
    private int weekDay = 7;
    private int hourOfDay = 8;
    private int minuteOfHour = 0;
    private int alarmNumber;
    private boolean isMorning = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.edit_new_alarm_activity);
        ButterKnife.bind(this);
        initToolbar();
        initData();
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
                }
            }
        });
        selectHour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                if (!isMorning) {
                    hourOfDay = new Integer(text).intValue()+12;
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

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        toolbarTitle.setText(getString(R.string.edit_new_normal_alarm));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.done_menu:
                saveNormalAlarm();
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

    @OnClick(R.id.edit_new_alarm_weekday_bt)
    public void editNewAlarmWeekday() {
        final String[] weekDays = getResources().getStringArray(R.array.week_day);
        String[] javaWeekDays = new String[]{weekDays[0], weekDays[1], weekDays[2],
                weekDays[3], weekDays[4], weekDays[5], weekDays[6], weekDays[7], weekDays[8]};

        new MaterialDialog.Builder(this)
                .title(R.string.alarm_edit)
                .content(getString(R.string.alarm_set_week_day_dialog_text))
                .items(javaWeekDays)
                .itemsCallbackSingleChoice(7, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {
                        getModel().getAllAlarm(new SyncControllerImpl.SyncAlarmToWatchListener() {
                            @Override
                            public void syncAlarmToWatch(List<Alarm> alarms) {
                                weekDay = (byte) which;
                                weekdayTv.setText(weekDays[which]);
                            }
                        });
                        return true;
                    }
                })
                .positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel).contentColorRes(R.color.left_menu_item_text_color)
                .show();
    }

    private void saveNormalAlarm() {
        final List<Alarm> normalAlarm = new ArrayList<>();
        if (alarmName != null) {
            getModel().getAlarmDatabaseHelper().getAll().subscribe(new Consumer<List<Alarm>>() {
                @Override
                public void accept(List<Alarm> alarms) throws Exception {
                    int alarmNumber = 7;
                    for (Alarm alarm : alarms) {
                        if (alarm.getAlarmNumber() > 6 && alarm.getAlarmNumber() < 13) {
                            normalAlarm.add(alarm);
                        }
                    }
                    alarmNumber += normalAlarm.size();
                    if (alarmNumber <= 12) {
                        final Alarm newAlarm = new Alarm(hourOfDay, minuteOfHour, (byte) weekDay, alarmName, (byte) 1, (byte) alarmNumber);
                        getModel().getAlarmDatabaseHelper().add(newAlarm).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    getModel().getSyncController().setAlarm(newAlarm);
                                    setResult(-1);
                                    finish();
                                }
                            }
                        });
                    } else {
                        ToastHelper.showShortToast(EditNewAlarmActivity.this, getString(R.string.alarm_max_value));
                    }
                }
            });
        } else {
            ToastHelper.showShortToast(this, "please check your config");
        }
    }
}
