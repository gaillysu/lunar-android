package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.PresetArrayAdapter;
import com.medcorp.lunar.adapter.SleepGoalListAdapter;
import com.medcorp.lunar.adapter.SolarGoalListAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.database.entry.SleepGoalDatabaseHelper;
import com.medcorp.lunar.fragment.MainClockFragment;
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

/***
 * Created by Jason on 2016/12/14.
 */

public class GoalsSettingActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_goals_list_view)
    ListView activityGoalsList;
    @Bind(R.id.activity_sleep_goals_list_view)
    ListView sleepGoalsList;
    @Bind(R.id.activity_solar_goals_list_view)
    ListView sunshineGoalsList;
    @Bind(R.id.more_setting_activity_root_view)
    RelativeLayout rootView;

    private SleepGoalDatabaseHelper sleepDatabaseHelper;
    private SleepGoalListAdapter sleepAdapter;
    private List<SleepGoal> allSleep;
    private List<SolarGoal> allSolar;
    private SolarGoalListAdapter SolarAdapter;
    List<StepsGoal> mStepsGoalList;
    PresetArrayAdapter presetArrayAdapter;
    private int steps;
    private String stepsLableGoal;
    private StepsGoal stepsGoal;
    private String sleepLableGoal;
    private String solarLableGoal;
    private int selectHour = 0;
    private int selectMinutes = 0;
    public static final int STEPS_FLAG = 0X01;
    public static final int SOLAR_FLAG = 0X02;
    public static final int SLEEP_FLAG = 0X03;
    private static final int MORE_SETTING_REQUEST_CODE = 0X01 << 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_setting_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initSunshineData();
        initSleepData();
        initStepsData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_menu).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        switch (item.getItemId()) {
            case R.id.add_menu:
                showAddNewGoalBottomDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddNewGoalBottomDialog() {
        View addNewGoalLayout = LayoutInflater.from(this).inflate(R.layout.add_new_goal_layout, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(addNewGoalLayout);
        dialog.show();
        AppCompatButton activeButton = (AppCompatButton) addNewGoalLayout.findViewById(R.id.more_setting_add_new_activity);
        AppCompatButton inactiveButton = (AppCompatButton) addNewGoalLayout.findViewById(R.id.more_setting_add_new_inactivity);
        AppCompatButton sunshineButton = (AppCompatButton) addNewGoalLayout.findViewById(R.id.more_setting_add_new_sunshine);
        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addNewActivity();
            }
        });
        inactiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startSettingGoalTime();
            }
        });
        sunshineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                editSolarGoalTime();
            }
        });

    }

    private void showBottomDialog(final int type, final int id) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.more_setting_bottom_view, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(contentView);
        dialog.show();
        Button editButton = (Button) contentView.findViewById(R.id.more_setting_action_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 0) {
                    dialog.dismiss();
                    editGoal(type, id);
                }
            }
        });
        Button deleteButton = (Button) contentView.findViewById(R.id.more_setting_action_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 0) {
                    dialog.dismiss();
                    new MaterialDialog.Builder(GoalsSettingActivity.this)
                            .title(getString(R.string.goal_delete))
                            .content(getString(R.string.settings_sure))
                            .positiveText(R.string.goal_ok)
                            .negativeText(R.string.goal_cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    deleteGoal(type, id);
                                }
                            })
                            .negativeColor(getResources().getColor(R.color.colorPrimary))
                            .positiveColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            }
        });
    }

    private void initStepsData() {
        getModel().getAllGoal(new MainClockFragment.ObtainGoalListener() {
            @Override
            public void obtainGoal(List<StepsGoal> list) {
                mStepsGoalList = list;
                presetArrayAdapter = new PresetArrayAdapter(GoalsSettingActivity.this, getModel(), mStepsGoalList);
                activityGoalsList.setAdapter(presetArrayAdapter);

            }
        });
        activityGoalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBottomDialog(STEPS_FLAG, mStepsGoalList.get(position).getId());
            }
        });
    }


    private void initSleepData() {
        sleepDatabaseHelper = getModel().getSleepGoalDatabseHelper();
        sleepDatabaseHelper.getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(List<SleepGoal> sleepGoals) throws Exception {
                sleepAdapter = new SleepGoalListAdapter(GoalsSettingActivity.this, getModel(), sleepGoals);
                sleepGoalsList.setAdapter(sleepAdapter);
                allSleep = sleepGoals;
            }
        });
        sleepGoalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBottomDialog(SLEEP_FLAG, allSleep.get(position).getSleepGoalId());
            }
        });
    }

    private void initSunshineData() {
        getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
            @Override
            public void accept(List<SolarGoal> solarGoals) throws Exception {
                SolarAdapter = new SolarGoalListAdapter(GoalsSettingActivity.this, getModel(), solarGoals);
                sunshineGoalsList.setAdapter(SolarAdapter);
                allSolar = solarGoals;
            }
        });
        sunshineGoalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBottomDialog(SOLAR_FLAG, allSolar.get(position).getSolarGoalId());
            }
        });
    }

    private void editGoal(int type, int id) {

        switch (type) {
            case STEPS_FLAG:
                Intent intent = new Intent(this, EditGoalsActivity.class);
                intent.putExtra(getString(R.string.launch_edit_goal_activity_flag), STEPS_FLAG);
                intent.putExtra(getString(R.string.key_preset_id), id);
                startActivityForResult(intent, MORE_SETTING_REQUEST_CODE);
                break;
            case SLEEP_FLAG:
                Intent intentSleep = new Intent(this, EditGoalsActivity.class);
                intentSleep.putExtra(getString(R.string.launch_edit_goal_activity_flag), SLEEP_FLAG);
                intentSleep.putExtra(getString(R.string.key_preset_id), id);
                startActivityForResult(intentSleep, MORE_SETTING_REQUEST_CODE);
                break;
            case SOLAR_FLAG:
                Intent intentSolar = new Intent(this, EditGoalsActivity.class);
                intentSolar.putExtra(getString(R.string.launch_edit_goal_activity_flag), SOLAR_FLAG);
                intentSolar.putExtra(getString(R.string.key_preset_id), id);
                startActivityForResult(intentSolar, MORE_SETTING_REQUEST_CODE);
                break;
        }
    }

    private void deleteGoal(int type, final int id) {
        switch (type) {
            case STEPS_FLAG:
                getModel().getStepsGoalDatabaseHelper().remove(id).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            for (int i = 0; i < mStepsGoalList.size(); i++) {
                                if (mStepsGoalList.get(i).getId() == id) {
                                    mStepsGoalList.remove(i);
                                    break;
                                }
                            }
                            presetArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case SLEEP_FLAG:
                sleepDatabaseHelper.remove(id).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            for (int i = 0; i < allSleep.size(); i++) {
                                if (allSleep.get(i).getSleepGoalId() == id) {
                                    allSleep.remove(i);
                                    break;
                                }
                            }
                            sleepAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case SOLAR_FLAG:
                getModel().getSolarGoalDatabaseHelper().remove(id).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Log.e("jason", "delete solar goal");
                            for (int i = 0; i < allSolar.size(); i++) {
                                if (allSolar.get(i).getSolarGoalId() == id) {
                                    allSolar.remove(i);
                                    break;
                                }
                            }
                            SolarAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MORE_SETTING_REQUEST_CODE) {
            switch (resultCode) {
                case EditGoalsActivity.STEPS_RESULT_CODE:
                    getModel().getStepsGoalDatabaseHelper().getAll().subscribe(new Consumer<List<StepsGoal>>() {
                        @Override
                        public void accept(List<StepsGoal> stepsGoals) throws Exception {
                            mStepsGoalList.clear();
                            mStepsGoalList.addAll(stepsGoals);
                            presetArrayAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case EditGoalsActivity.SLEEP_RESULT_CODE:
                    sleepDatabaseHelper.getAll().subscribe(new Consumer<List<SleepGoal>>() {
                        @Override
                        public void accept(List<SleepGoal> sleepGoals) throws Exception {
                            allSleep.clear();
                            allSleep.addAll(sleepGoals);
                            sleepAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case EditGoalsActivity.SOLAR_RESULT_CODE:
                    getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
                        @Override
                        public void accept(List<SolarGoal> solarGoals) throws Exception {
                            allSolar.clear();
                            allSolar.addAll(solarGoals);
                            SolarAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toolbar.setTitle(R.string.settings_more);
    }

    public void addNewActivity() {
        new MaterialDialog.Builder(this)
                .title(R.string.goal_add)
                .content(R.string.goal_input)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(getString(R.string.goal_step_goal), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.length() == 0)
                                    return;
                                steps = Integer.parseInt(input.toString());
                                editActivityName();
                            }
                        })
                .positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void editActivityName() {
        new MaterialDialog.Builder(this)
                .title(R.string.edit_goal_name)
                .content(R.string.goal_label_goal)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.goal_name_goal), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.length() == 0) {
                                    stepsLableGoal = getString(R.string.def_goal_name) + " " + (mStepsGoalList.size() + 1);
                                } else {
                                    stepsLableGoal = input.toString();
                                }

                                stepsGoal = new StepsGoal(stepsLableGoal, true, steps);
                                getModel().addGoal(stepsGoal).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            mStepsGoalList.add(stepsGoal);
                                            presetArrayAdapter.notifyDataSetChanged();
                                        } else {
                                            ToastHelper.showShortToast(GoalsSettingActivity.this, getString(R.string.save_filed));
                                        }
                                    }
                                });
                            }
                        }).negativeText(R.string.goal_cancel)
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }


    public void startSettingGoalTime() {
        selectHour = 0;
        selectMinutes = 0;
        View selectTimeDialog = LayoutInflater.from(this).inflate(R.layout.select_time_dialog_layou, null);
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

        new MaterialDialog.Builder(this).customView(selectTimeDialog, false).
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
        new MaterialDialog.Builder(this)
                .title(R.string.edit_goal_name)
                .content(R.string.goal_label_sleep)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.goal_name_goal_sleep), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.length() == 0) {
                                    sleepLableGoal = getString(R.string.def_goal_sleep_name) + " " + (allSleep.size() + 1);
                                } else {
                                    sleepLableGoal = input.toString();
                                }
                                final SleepGoal sleepGoal = new SleepGoal(sleepLableGoal, hour * 60 + minute, false);
                                sleepDatabaseHelper.add(sleepGoal)
                                        .subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(Boolean aBoolean) throws Exception {
                                                if (aBoolean) {
                                                    allSleep.add(sleepGoal);
                                                    GoalsSettingActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            sleepAdapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        }).negativeText(R.string.goal_cancel)
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    public void editSolarGoalTime() {
        selectHour = 0;
        selectMinutes = 0;
        View selectTimeDialog = LayoutInflater.from(this).inflate(R.layout.select_time_dialog_layou, null);
        List<String> hourList = new ArrayList<>();
        List<String> minutes = new ArrayList<>();
        minutes.add(0 + "");
        minutes.add(30 + "");
        for (int i = 0; i <= 4; i++) {
            hourList.add(i + "");
        }
        PickerView hourPickerView = (PickerView) selectTimeDialog.findViewById(R.id.hour_pv);
        hourPickerView.setData(hourList);
        PickerView minutePickerView = (PickerView) selectTimeDialog.findViewById(R.id.minute_pv);
        minutePickerView.setData(minutes);
        minutePickerView.setSelected(1);
        hourPickerView.setSelected(0);
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

        new MaterialDialog.Builder(this).customView(selectTimeDialog, false).
                title(getString(R.string.and_new_sunshine_fb))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (selectHour == 0 && selectMinutes == 0) {
                            selectHour = 0;
                            selectMinutes = 30;
                        }
                        addNewSunshine(selectHour, selectMinutes);
                    }
                }).positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    public void addNewSunshine(final int hour, final int minute) {
        new MaterialDialog.Builder(this)
                .title(R.string.edit_goal_name)
                .content(R.string.goal_label_solar)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.goal_name_goal_solar), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.length() == 0) {
                                    solarLableGoal = getString(R.string.def_goal_solar_name) + " " + (allSolar.size() + 1);
                                } else {
                                    solarLableGoal = input.toString();
                                }
                                final SolarGoal solarGoal = new SolarGoal(solarLableGoal, hour * 60 + minute, false);
                                getModel().getSolarGoalDatabaseHelper().add(solarGoal).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        allSolar.add(solarGoal);
                                        SolarAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).negativeText(R.string.goal_cancel)
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }
}
