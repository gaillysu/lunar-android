package com.medcorp.lunar.activity.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.MyWatchActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.ChangeGoalEvent;
import com.medcorp.lunar.fragment.MainClockFragment;
import com.medcorp.lunar.model.ChangeSleepGoalEvent;
import com.medcorp.lunar.model.ChangeSolarGoalEvent;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.StepsGoal;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/***
 * Created by Jason on 2017/6/20.
 */

public class ConfigGoalsActivity extends BaseActivity {

    @Bind(R.id.activity_goals_step_tv)
    TextView stepGoal;
    @Bind(R.id.activity_goals_sleep_tv)
    TextView sleepGoal;
    @Bind(R.id.activity_goals_solar_tv)
    TextView solarGoal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_layout);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.activity_goals_set_step_goal_bt)
    public void selectStepGoal() {
        getModel().getAllGoal(new MainClockFragment.ObtainGoalListener() {
            @Override
            public void obtainGoal(final List<StepsGoal> stepsGoalList) {
                List<String> stringList = new ArrayList<>();
                final List<StepsGoal> stepsGoalEnableList = new ArrayList<>();
                int selectIndex = 0;
                for (int i = 0; i < stepsGoalList.size(); i++) {
                    StepsGoal stepsGoal = stepsGoalList.get(i);
                    if (stepsGoal.isStatus()) {
                        selectIndex = i;
                    }
                    stringList.add(stepsGoal.toString());
                    stepsGoalEnableList.add(stepsGoal);

                }
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);

                if (stepsGoalList.size() != 0) {
                    new MaterialDialog.Builder(ConfigGoalsActivity.this)
                            .title(R.string.steps_goal_title).itemsColor(getResources().getColor(R.color.edit_alarm_item_text_color))
                            .items(cs)
                            .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if (which >= 0) {

                                        for (int i = 0; i < stepsGoalList.size(); i++) {
                                            StepsGoal stepsGoal = stepsGoalList.get(i);
                                            if (i == which) {
                                                stepsGoal.setStatus(true);
                                            } else {
                                                stepsGoal.setStatus(false);
                                            }
                                            getModel().updateGoal(stepsGoal);
                                        }
                                        getModel().setStepsGoal(stepsGoalEnableList.get(which));
                                        EventBus.getDefault().post(new ChangeGoalEvent(true));
                                        stepGoal.setText(getString(R.string.more_settings_sleep_goal) +
                                                " : " + stepsGoalList.get(which).getSteps());
                                    } else {
                                        stepGoal.setText(getString(R.string.more_settings_sleep_goal));
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

    @OnClick(R.id.activity_goals_set_sleep_goal_bt)
    public void selectSleepGoal() {
        getModel().getSleepGoalDatabseHelper().getAll().subscribe(new Consumer<List<SleepGoal>>() {
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
                    stringList.add(obtainString(sleepGoal.getGoalName(), sleepGoal.getGoalDuration()));
                    stepsGoalEnableList.add(sleepGoal);

                }
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);

                if (sleepGoals.size() != 0) {
                    new MaterialDialog.Builder(ConfigGoalsActivity.this)
                            .title(R.string.def_goal_sleep_name).itemsColor(getResources().getColor(R.color.edit_alarm_item_text_color))
                            .items(cs)
                            .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if (which >= 0) {

                                        for (int i = 0; i < sleepGoals.size(); i++) {
                                            SleepGoal sleepGoal = sleepGoals.get(i);
                                            if (i == which) {
                                                sleepGoal.setStatus(true);
                                            } else {
                                                sleepGoal.setStatus(false);
                                            }
                                            getModel().getSleepGoalDatabseHelper().update(sleepGoal).subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean aBoolean) throws Exception {
                                                    Log.i("jason", "change sleep goal");
                                                }
                                            });
                                        }
                                        EventBus.getDefault().post(new ChangeSleepGoalEvent(true));
                                        sleepGoal.setText(getString(R.string.more_settings_sleep_goal) +
                                                " : " + countTime(sleepGoals.get(which).getGoalDuration()));
                                    } else {
                                        sleepGoal.setText(getString(R.string.more_settings_sleep_goal));
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

    @OnClick(R.id.activity_goals_set_solar_goal_bt)
    public void selectSolarGoal() {
        getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
            @Override
            public void accept(final List<SolarGoal> solarGoals) throws Exception {
                List<String> stringList = new ArrayList<>();
                final List<SolarGoal> stepsGoalEnableList = new ArrayList<>();
                int selectIndex = 0;
                for (int i = 0; i < solarGoals.size(); i++) {
                    SolarGoal solarGoal = solarGoals.get(i);
                    if (solarGoal.isStatus()) {
                        selectIndex = i;
                    }
                    stringList.add(obtainString(solarGoal.getName(), solarGoal.getTime()));
                    stepsGoalEnableList.add(solarGoal);

                }
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);

                if (solarGoals.size() != 0) {
                    new MaterialDialog.Builder(ConfigGoalsActivity.this)
                            .title(R.string.def_goal_solar_name).itemsColor(getResources().getColor(R.color.edit_alarm_item_text_color))
                            .items(cs)
                            .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if (which >= 0) {

                                        for (int i = 0; i < solarGoals.size(); i++) {
                                            SolarGoal solar = solarGoals.get(i);
                                            if (i == which) {
                                                solar.setStatus(true);
                                            } else {
                                                solar.setStatus(false);
                                            }
                                            getModel().getSolarGoalDatabaseHelper().update(solar).subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean aBoolean) throws Exception {
                                                    Log.i("jason", "change sleep goal");
                                                }
                                            });
                                        }
                                        EventBus.getDefault().post(new ChangeSolarGoalEvent(true));
                                        solarGoal.setText(getString(R.string.more_settings_solar_goal) + " : "
                                                + countTime(solarGoals.get(which).getTime()));
                                    } else {
                                        solarGoal.setText(getString(R.string.more_settings_solar_goal));
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

    @OnClick(R.id.config_next_button)
    public void next() {
        int currentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
        final int buildingFirmwareVersion = getResources().getInteger(R.integer.launar_version);
        if (currentFirmwareVersion < buildingFirmwareVersion) {
            startActivity(MyWatchActivity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }

    private String obtainString(String name, int time) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(": ");
        builder.append(countTime(time));
        return builder.toString();

    }

    private String countTime(int goalDuration) {
        StringBuffer sb = new StringBuffer();
        if (goalDuration > 60) {
            sb.append(goalDuration / 60 + getString(R.string.sleep_unit_hour)
                    + (goalDuration % 60 != 0 ? goalDuration % 60 + getString(R.string.sleep_unit_minute) : ""));
        } else if (goalDuration == 60) {
            sb.append(goalDuration / 60 + getString(R.string.sleep_unit_hour));
        } else {
            sb.append(goalDuration + getString(R.string.sleep_unit_minute));
        }
        return sb.toString();
    }
}

