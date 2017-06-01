package com.medcorp.lunar.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.SleepGoalListAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.database.entry.SleepGoalDatabaseHelper;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.view.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by Jason on 2017/5/27.
 */

public class SleepGoalActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar myToolbar;
    @Bind(R.id.activity_sleep_goals_list_view)
    ListView allSleepGoal;

    private SleepGoalDatabaseHelper sleepDatabaseHelper;
    private SleepGoalListAdapter adapter;
    private List<SleepGoal> all;
    private String lableGoal;

    private static final int SLEEP_FLAG = 0X03;
    private static final int SOLAR_REQUEST_CODE = 0X01 << 3;
    private int selectHour = 0;
    private int selectMinutes = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_sleep_goal_activity);
        ButterKnife.bind(this);
        sleepDatabaseHelper = getModel().getSleepDatabseHelper();
        initToolbar();
        initData();
    }

    private void initData() {
        sleepDatabaseHelper.getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(List<SleepGoal> sleepGoals) throws Exception {
                adapter = new SleepGoalListAdapter(SleepGoalActivity.this, getModel(), sleepGoals);
                allSleepGoal.setAdapter(adapter);
                all = sleepGoals;
            }
        });
        allSleepGoal.setOnItemClickListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView) myToolbar.findViewById(R.id.lunar_tool_bar_title);
        toolbarTitle.setText(getString(R.string.more_settings_sleep_goal));
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_menu:
                new MaterialDialog.Builder(this)
                        .title(R.string.goal_add)
                        .content(R.string.goal_label_sleep)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(getString(R.string.goal_name_goal_sleep), "",
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {
                                        if (input.length() == 0) {
                                            lableGoal = getString(R.string.def_goal_sleep_name) + " " + (all.size() + 1);
                                        } else {
                                            lableGoal = input.toString();
                                        }
                                        startSettingGoalTime();
                                    }
                                }).negativeText(R.string.goal_cancel)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            getModel().getSleepDatabseHelper().getAll().subscribe(new Consumer<List<SleepGoal>>() {
                @Override
                public void accept(List<SleepGoal> sleepGoals) throws Exception {
                    adapter = new SleepGoalListAdapter(SleepGoalActivity.this, getModel(), sleepGoals);
                    allSleepGoal.setAdapter(adapter);
                    all = sleepGoals;
                }
            });
        }
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
                sleepDatabaseHelper.add(new SleepGoal(lableGoal, selectHour * 60 + selectMinutes, false))
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    SleepGoalActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            initData();
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditGoalsActivity.class);
        SleepGoal solarGoal = all.get(position);
        intent.putExtra(getString(R.string.launch_edit_goal_activity_flag), SLEEP_FLAG);
        intent.putExtra(getString(R.string.key_preset_id), solarGoal.getSleepGoalId());
        startActivityForResult(intent, SOLAR_REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
