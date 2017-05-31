package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.PresetEditAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.view.ToastHelper;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        initToolbar();
        initData();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        toolbarTitle.setText(getString(R.string.goal_edit));
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
                getModel().getSleepDatabseHelper().get(intent.getIntExtra(getString(R.string.key_preset_id), -1)).
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
                editSolarGoal(position);
                break;
            case 0x03:
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
            DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(this,
                    new DatePickerPopWin.OnDatePickedListener() {
                        @Override
                        public void onDatePickCompleted(int year, int month,
                                                        int day, String dateDesc) {
                            solarGoal.setTime(month);
                            getModel().getSolarGoalDatabaseHelper().update(solarGoal)
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            if (aBoolean) {
                                                presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this,
                                                        getModel(), 0x02, solarGoal.getSolarGoalId()));
                                            }
                                        }
                                    });
                        }
                    }).viewStyle(4)
                    .viewTextSize(18)
                    .dateChose("0")
                    .build();
            pickerPopWin3.showPopWin(this);
        } else if (position == 1) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_label_goal)
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
            DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(this,
                    new DatePickerPopWin.OnDatePickedListener() {
                        @Override
                        public void onDatePickCompleted(int year, int month,
                                                        int day, String dateDesc) {
                            sleepGoal.setGoalDuration(month);
                            getModel().getSleepDatabseHelper().update(sleepGoal)
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            if (aBoolean) {
                                                presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this,
                                                        getModel(), 0x03, sleepGoal.getSleepGoalId()));
                                            }
                                        }
                                    });
                        }
                    }).viewStyle(5)
                    .viewTextSize(18)
                    .dateChose("0")
                    .build();
            pickerPopWin3.showPopWin(this);
        } else if (position == 1) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_label_goal)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.goal_label), sleepGoal.getGoalName(), new MaterialDialog.InputCallback() {
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
                                                getModel(), 0x03, sleepGoal.getSleepGoalId()));
                                    }
                                }
                            });
                        }
                    }).negativeText(R.string.goal_cancel)
                    .show();
        } else if (position == 2) {
            getModel().getSleepDatabseHelper().remove(sleepGoal.getSleepGoalId()).subscribe(new Consumer<Boolean>() {
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                EditGoalsActivity.this.setResult(0);
                EditGoalsActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
