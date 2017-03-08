package com.medcorp.lunar.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.AlarmEditAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.view.ToastHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 21/12/15.
 */
public class EditAlarmActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_edit_alarm_list_view)
    ListView listView;

    private Alarm alarm;
    private boolean isRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.title_alarm);

        Bundle bundle = getIntent().getExtras();
        alarm = getModel().getAlarmById(bundle.getInt(getString(R.string.key_alarm_id)));
        listView.setAdapter(new AlarmEditAdapter(this, alarm));
        listView.setOnItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_menu:
                if (getModel().updateAlarm(alarm) && !isRepeat) {
                    ToastHelper.showShortToast(this, R.string.alarm_saved);
                    setResult(1);
                    finish();
                } else {
                    ToastHelper.showShortToast(this, R.string.alarm_could_not_save);
                }
                return true;
            default:
                setResult(0);
                finish();
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        heightVersion(position);
    }

    private void heightVersion(int position) {
        if (position == 0) {
            Dialog alarmDialog = new TimePickerDialog(this, R.style.NevoDialogStyle, timeSetListener, alarm.getHour(), alarm.getMinute(), true);
            alarmDialog.setTitle(R.string.alarm_edit);
            alarmDialog.show();
        } else if (position == 1) {
            new MaterialDialog.Builder(EditAlarmActivity.this)
                    .title(R.string.alarm_edit)
                    .content(getString(R.string.alarm_label_alarm))
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.alarm_label), alarm.getLabel(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0)
                                return;
                            alarm.setLabel(input.toString());
                            listView.setAdapter(new AlarmEditAdapter(EditAlarmActivity.this, alarm));
                        }
                    }).negativeText(R.string.alarm_cancel)
                    .show();
        } else if (position == 2) {
            String[] weekDays = getResources().getStringArray(R.array.week_day);
            String[] javaWeekDays = new String[]{weekDays[1], weekDays[2], weekDays[3], weekDays[4], weekDays[5], weekDays[6], weekDays[7]};
            new MaterialDialog.Builder(EditAlarmActivity.this)
                    .title(R.string.alarm_edit)
                    .content(getString(R.string.alarm_set_week_day_dialog_text))
                    .items(javaWeekDays)
                    .itemsCallbackSingleChoice((alarm.getWeekDay() & 0x0F) - 1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            List<Alarm> allAlarm = getModel().getAllAlarm();
                            String[] weekDayArray = getResources().getStringArray(R.array.week_day);
                            for (Alarm olderAlarm : allAlarm) {
                                byte weekDay = olderAlarm.getWeekDay();
                                if ((weekDay & 0x0F) == (which + 1) && olderAlarm.getAlarmNumber() == alarm.getAlarmNumber()) {
                                    isRepeat = true;
                                    break;
                                } else {
                                    isRepeat = false;
                                }
                            }
                            if (isRepeat) {
                                ToastHelper.showShortToast(EditAlarmActivity.this, alarm.getAlarmType() == (byte) 0 ?
                                        getString(R.string.prompt_user_alarm_no_repeat) + " " + weekDayArray[which + 1]
                                        : getString(R.string.prompt_user_alarm_no_repeat_two) + " " + weekDayArray[which + 1]);
                            } else {
                                alarm.setWeekDay(((alarm.getWeekDay() & 0x80) == 0x80) ? (byte) (0x80 | (which + 1)) : (byte) (which + 1));
                                listView.setAdapter(new AlarmEditAdapter(EditAlarmActivity.this, alarm));
                            }
                            return true;
                        }
                    })
                    .positiveText(R.string.goal_ok)
                    .negativeText(R.string.goal_cancel).contentColorRes(R.color.left_menu_item_text_color)
                    .show();
        } else if (position == 3) {
            if (!getModel().deleteAlarm(alarm)) {
                ToastHelper.showShortToast(EditAlarmActivity.this, R.string.alarm_could_not_change);
            } else {
                ToastHelper.showShortToast(EditAlarmActivity.this, R.string.alarm_deleted);
            }
            setResult(-1);
            finish();
        }
    }

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            alarm.setHour(hourOfDay);
            alarm.setMinute(minute);
            if (!getModel().updateAlarm(alarm)) {
                ToastHelper.showShortToast(EditAlarmActivity.this, R.string.alarm_could_not_change);
            } else {
                listView.setAdapter(new AlarmEditAdapter(EditAlarmActivity.this, alarm));
            }
        }
    };
}
