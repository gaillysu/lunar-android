package com.medcorp.lunar.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.PresetEditAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by gaillysu on 15/12/23.
 */
public class EditGoalsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_goals_list_view)
    ListView presetListView;

    private StepsGoal stepsGoal;
    private SolarGoal solarGoal;
    private SleepGoal sleepGoal;
    private int mFlag;
    private int selectHour = 0;
    private int selectMinutes = 0;

    public static final int SLEEP_RESULT_CODE = 0X02 << 1;
    public static final int SOLAR_RESULT_CODE = 0X02 << 2;
    public static final int STEPS_RESULT_CODE = 0X02 << 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.goal_edit);
    }

    private void initData() {
        Intent intent = getIntent();
        mFlag = intent.getIntExtra(getString(R.string.launch_edit_goal_activity_flag), -1);

        switch (mFlag) {
            case 0x01:
                stepsGoal = getModel().getGoalById(intent.getIntExtra(getString(R.string.key_preset_id), -1));
                presetListView.setVisibility(View.VISIBLE);
                presetListView.setOnItemClickListener(this);
                presetListView.setAdapter(new PresetEditAdapter(this, getModel(), mFlag, stepsGoal.getId()));
                break;
            case 0x02:
                getModel().getSolarGoalDatabaseHelper().get(intent.getIntExtra(getString(R.string.key_preset_id), -1)).
                        subscribe(new Consumer<SolarGoal>() {
                            @Override
                            public void accept(SolarGoal goal) throws Exception {
                                solarGoal = goal;
                                presetListView.setVisibility(View.VISIBLE);
                                presetListView.setOnItemClickListener(EditGoalsActivity.this);
                                presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this
                                        , getModel(), mFlag, goal.getSolarGoalId()));
                            }
                        });
                break;
            case 0x03:
                getModel().getSleepGoalDatabseHelper().get(intent.getIntExtra(getString(R.string.key_preset_id), -1)).
                        subscribe(new Consumer<SleepGoal>() {
                            @Override
                            public void accept(SleepGoal goal) throws Exception {
                                sleepGoal = goal;
                                presetListView.setVisibility(View.VISIBLE);
                                presetListView.setOnItemClickListener(EditGoalsActivity.this);
                                presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this
                                        , getModel(), mFlag, goal.getSleepGoalId()));
                            }
                        });
                break;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mFlag) {
            case 0x01:
                editStepsGoal(position);
                break;
            case 0x02:
                int time = solarGoal.getTime();
                selectMinutes = time % 60;
                selectHour = time / 60;
                editSolarGoal(position);
                break;
            case 0x03:
                int goalDuration = sleepGoal.getGoalDuration();
                selectMinutes = goalDuration % 60;
                selectHour = goalDuration / 60;
                editSleepGoal(position);
                break;
        }

    }


    private void editStepsGoal(int position) {
        if (position == 0) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_input)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("", "" + stepsGoal.getSteps(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0)
                                return;
                            stepsGoal.setSteps(Integer.parseInt(input.toString()));
                            getModel().updateGoal(stepsGoal);
                            presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this, getModel(), 0x01, stepsGoal.getId()));
                        }
                    }).negativeText(R.string.goal_cancel).show();
        } else if (position == 1) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_label_goal)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.goal_label), stepsGoal.getLabel(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0)
                                return;
                            stepsGoal.setLabel(input.toString());
                            getModel().updateGoal(stepsGoal);
                            presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this, getModel(), 0x01, stepsGoal.getId()));
                        }
                    }).negativeText(R.string.goal_cancel)
                    .show();
        } else if (position == 2) {
            getModel().getStepsGoalDatabaseHelper().remove(stepsGoal.getId()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    ToastHelper.showShortToast(EditGoalsActivity.this, R.string.goal_deleted);
                    setResult(-1);
                    finish();
                }
            });
        }
    }


    private void editSolarGoal(int position) {
        if (position == 0) {
            List<String> hourList = new ArrayList<>();
            List<String> minutes = new ArrayList<>();
            minutes.add(0 + "");
            minutes.add(30 + "");
            for (int i = 0; i <= 4; i++) {
                hourList.add(i + "");
            }
            startSettingGoalTime(hourList, minutes);
        } else if (position == 1) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_label_sleep)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.goal_label), solarGoal.getName(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0)
                                return;
                            solarGoal.setName(input.toString());
                            getModel().getSolarGoalDatabaseHelper().update(solarGoal).subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this,
                                                getModel(), 0x02, solarGoal.getSolarGoalId()));
                                    }
                                }
                            });
                        }
                    }).negativeText(R.string.goal_cancel)
                    .show();
        } else if (position == 2) {
            getModel().getSolarGoalDatabaseHelper().remove(solarGoal.getSolarGoalId())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                ToastHelper.showShortToast(EditGoalsActivity.this, R.string.goal_deleted);
                                setResult(-1);
                                finish();
                            }
                        }
                    });
        }
    }

    private void editSleepGoal(int position) {
        if (position == 0) {
            List<String> hourList = new ArrayList<>();
            List<String> minutes = new ArrayList<>();
            minutes.add(0 + "");
            minutes.add(30 + "");
            for (int i = 5; i <= 12; i++) {
                hourList.add(i + "");
            }
            startSettingGoalTime(hourList, minutes);
        } else if (position == 1) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_label_sleep)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.goal_label), sleepGoal.getGoalName(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0)
                                return;
                            sleepGoal.setGoalName(input.toString());
                            getModel().getSleepGoalDatabseHelper().update(sleepGoal).subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this,
                                                getModel(), 0x03, sleepGoal.getSleepGoalId()));
                                    }
                                }
                            });
                        }
                    }).negativeText(R.string.goal_cancel)
                    .show();
        } else if (position == 2) {
            getModel().getSleepGoalDatabseHelper().remove(sleepGoal.getSleepGoalId()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        ToastHelper.showShortToast(EditGoalsActivity.this, R.string.goal_deleted);
                        setResult(SLEEP_RESULT_CODE);
                        finish();
                    }
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (mFlag) {
                    case 0x01:
                        EditGoalsActivity.this.setResult(STEPS_RESULT_CODE);
                        EditGoalsActivity.this.finish();
                        break;
                    case 0x02:
                        EditGoalsActivity.this.setResult(SOLAR_RESULT_CODE);
                        EditGoalsActivity.this.finish();
                        break;
                    case 0x03:
                        EditGoalsActivity.this.setResult(SLEEP_RESULT_CODE);
                        EditGoalsActivity.this.finish();
                        break;
                }
                EditGoalsActivity.this.setResult(0);
                EditGoalsActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingGoalTime(List<String> hourList, List<String> minutes) {
        View selectTimeDialog = LayoutInflater.from(this).inflate(R.layout.select_time_dialog_layou, null);
        final Dialog dialog = new AlertDialog.Builder(this).create();

        PickerView hourPickerView = (PickerView) selectTimeDialog.findViewById(R.id.hour_pv);
        hourPickerView.setData(hourList);
        PickerView minutePickerView = (PickerView) selectTimeDialog.findViewById(R.id.minute_pv);
        minutePickerView.setData(minutes);
        hourPickerView.setSelected(hourList.indexOf(selectHour + ""));
        minutePickerView.setSelected(minutes.indexOf(selectMinutes + ""));
        Button cancelButton = (Button) selectTimeDialog.findViewById(R.id.select_time_cancel_bt);
        Button selectButton = (Button) selectTimeDialog.findViewById(R.id.select_time_select_bt);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(selectTimeDialog);
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
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mFlag == 0x02) {
                    solarGoal.setTime(selectHour * 60 + selectMinutes);
                    getModel().getSolarGoalDatabaseHelper().update(solarGoal).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this,
                                        getModel(), 0x02, solarGoal.getSolarGoalId()));
                            }
                        }
                    });
                } else if (mFlag == 0x03) {
                    sleepGoal.setGoalDuration(selectHour * 60 + selectMinutes);
                    getModel().getSleepGoalDatabseHelper().update(sleepGoal).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this,
                                        getModel(), 0x03, sleepGoal.getSleepGoalId()));
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (mFlag) {
                case 0x01:
                    setResult(STEPS_RESULT_CODE);
                    EditGoalsActivity.this.finish();
                    break;
                case 0x02:
                    setResult(SOLAR_RESULT_CODE);
                    EditGoalsActivity.this.finish();
                    break;
                case 0x03:
                    setResult(SLEEP_RESULT_CODE);
                    EditGoalsActivity.this.finish();
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
