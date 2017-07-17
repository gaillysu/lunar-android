package com.medcorp.lunar.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.MySpinnerAdapter;
import com.medcorp.lunar.adapter.PresetArrayAdapter;
import com.medcorp.lunar.adapter.SleepGoalListAdapter;
import com.medcorp.lunar.adapter.SolarGoalListAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.database.entry.SleepGoalDatabaseHelper;
import com.medcorp.lunar.event.bluetooth.DigitalTimeChangedEvent;
import com.medcorp.lunar.fragment.MainClockFragment;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.PickerView;
import com.medcorp.lunar.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/***
 * Created by Jason on 2016/12/14.
 */

public class MoreSettingActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.more_setting_select_sync_time_spinner)
    Spinner selectPlaceSpinner;
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
    private PopupWindow mPopupWindow;
    private static final int STEPS_FLAG = 0X01;
    private static final int SOLAR_FLAG = 0X02;
    private static final int SLEEP_FLAG = 0X03;
    private static final int MORE_SETTING_REQUEST_CODE = 0X01 << 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_setting_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        showPopupWindow(0, 0);
        initSunshineData();
        initSleepData();
        initStepsData();
    }

    private void showPopupWindow(final int type, final int id) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupWindownView = inflater.inflate(R.layout.more_setting_bottom_view, null);
        mPopupWindow = new PopupWindow(popupWindownView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        popupWindownView.findViewById(R.id.more_setting_action_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 0) {
                    mPopupWindow.dismiss();
                    editGoal(type, id);
                }
            }
        });
        popupWindownView.findViewById(R.id.more_setting_action_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 0) {
                    mPopupWindow.dismiss();
                    deleteGoal(type, id);
                }
            }
        });
        if (type != 0) {
            mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    private void initStepsData() {
        getModel().getAllGoal(new MainClockFragment.ObtainGoalListener() {
            @Override
            public void obtainGoal(List<StepsGoal> list) {
                mStepsGoalList = list;
                presetArrayAdapter = new PresetArrayAdapter(MoreSettingActivity.this, getModel(), mStepsGoalList);
                activityGoalsList.setAdapter(presetArrayAdapter);

            }
        });
        activityGoalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                showPopupWindow(STEPS_FLAG, mStepsGoalList.get(position).getId());
            }
        });
    }


    private void initSleepData() {
        sleepDatabaseHelper = getModel().getSleepGoalDatabseHelper();
        sleepDatabaseHelper.getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(List<SleepGoal> sleepGoals) throws Exception {
                sleepAdapter = new SleepGoalListAdapter(MoreSettingActivity.this, getModel(), sleepGoals);
                sleepGoalsList.setAdapter(sleepAdapter);
                allSleep = sleepGoals;
            }
        });
        sleepGoalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                showPopupWindow(SLEEP_FLAG, allSleep.get(position).getSleepGoalId());
            }
        });
    }

    private void initSunshineData() {
        getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
            @Override
            public void accept(List<SolarGoal> solarGoals) throws Exception {
                SolarAdapter = new SolarGoalListAdapter(MoreSettingActivity.this, getModel(), solarGoals);
                sunshineGoalsList.setAdapter(SolarAdapter);
                allSolar = solarGoals;
            }
        });
        sunshineGoalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                showPopupWindow(SOLAR_FLAG, allSolar.get(position).getSolarGoalId());
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
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case SLEEP_FLAG:
                Intent intentSleep = new Intent(this, EditGoalsActivity.class);
                intentSleep.putExtra(getString(R.string.launch_edit_goal_activity_flag), SLEEP_FLAG);
                intentSleep.putExtra(getString(R.string.key_preset_id), id);
                startActivityForResult(intentSleep, MORE_SETTING_REQUEST_CODE);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case SOLAR_FLAG:
                Intent intentSolar = new Intent(this, EditGoalsActivity.class);
                intentSolar.putExtra(getString(R.string.launch_edit_goal_activity_flag), SOLAR_FLAG);
                intentSolar.putExtra(getString(R.string.key_preset_id), id);
                startActivityForResult(intentSolar, MORE_SETTING_REQUEST_CODE);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
                            for (int i = 0; i < allSolar.size(); i++) {
                                if (allSolar.get(i).getSolarGoalId() == id) {
                                    allSolar.remove(i);
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

    private void initData() {
        List<String> placeList = new ArrayList<>();
        placeList.add(getString(R.string.more_setting_place_home));
        placeList.add(getString(R.string.more_setting_place_local));
        MySpinnerAdapter placeAdapter = new MySpinnerAdapter(this, placeList);
        selectPlaceSpinner.setAdapter(placeAdapter);
        selectPlaceSpinner.setSelection(Preferences.getPlaceSelect(this) ? 0 : 1);

        selectPlaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Preferences.savePlaceSelect(MoreSettingActivity.this, true);
                } else {
                    Preferences.savePlaceSelect(MoreSettingActivity.this, false);
                }
                EventBus.getDefault().post(new DigitalTimeChangedEvent(position == 0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static void SnackbarAddView(Snackbar snackbar, int layoutId, int index) {
        View snackbarView = snackbar.getView();
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarView;
        View add_view = LayoutInflater.from(snackbarView.getContext()).inflate(layoutId, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        snackbarLayout.addView(add_view, index, params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.more_setting_add_new_activity)
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
                        }).negativeText(R.string.goal_cancel)
                .show();
    }

    private void editActivityName() {
        new MaterialDialog.Builder(this)
                .title(R.string.goal_add)
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
                                            ToastHelper.showShortToast(MoreSettingActivity.this, getString(R.string.save_filed));
                                        }
                                    }
                                });
                            }
                        }).negativeText(R.string.goal_cancel)
                .show();
    }

    @OnClick(R.id.more_setting_add_new_inactivity)
    public void addNewInactivity() {
        new MaterialDialog.Builder(this)
                .title(R.string.goal_add)
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
                                startSettingGoalTime();
                            }
                        }).negativeText(R.string.goal_cancel)
                .show();
    }

    private void startSettingGoalTime() {
        View selectTimeDialog = LayoutInflater.from(this).inflate(R.layout.select_time_dialog_layou, null);
        final Dialog dialog = new AlertDialog.Builder(this).create();
        List<String> hourList = new ArrayList<>();
        List<String> minutes = new ArrayList<>();
        minutes.add(0 + "");
        minutes.add(30 + "");
        for (int i = 5; i <= 12; i++) {
            hourList.add(i + "");
        }
        PickerView hourPickerView = (PickerView) selectTimeDialog.findViewById(R.id.hour_pv);
        hourPickerView.setData(hourList);
        PickerView minutePickerView = (PickerView) selectTimeDialog.findViewById(R.id.minute_pv);
        minutePickerView.setData(minutes);
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
                final SleepGoal sleepGoal = new SleepGoal(sleepLableGoal, selectHour * 60 + selectMinutes, false);
                sleepDatabaseHelper.add(sleepGoal)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    allSleep.add(sleepGoal);
                                    MoreSettingActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sleepAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }

    @OnClick(R.id.more_setting_add_new_sunshine)
    public void addNewSunshine() {
        new MaterialDialog.Builder(this)
                .title(R.string.goal_add)
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
                                editSolarGoalTime();
                            }
                        }).negativeText(R.string.goal_cancel)
                .show();
    }

    private void editSolarGoalTime() {
        View selectTimeDialog = LayoutInflater.from(this).inflate(R.layout.select_time_dialog_layou, null);
        final Dialog dialog = new AlertDialog.Builder(this).create();
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
                final SolarGoal solarGoal = new SolarGoal(solarLableGoal, selectHour * 60 + selectMinutes, false);
                getModel().getSolarGoalDatabaseHelper().add(solarGoal).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        allSolar.add(solarGoal);
                        SolarAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
