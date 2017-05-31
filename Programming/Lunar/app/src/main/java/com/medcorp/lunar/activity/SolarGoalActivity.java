package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.SolarGoalListAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.SolarGoal;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by Jason on 2017/5/27.
 */

public class SolarGoalActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.main_toolbar)
    Toolbar myToolbar;
    @Bind(R.id.activity_solar_goals_list_view)
    ListView allSolarGoal;
    private SolarGoalListAdapter adapter;
    private String lableGoal;
    private List<SolarGoal> all;
    private static final int SOLAR_FLAG = 0X02;
    private static final int SOLAR_REQUEST_CODE = 0X01 << 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_solar_goal_activity);
        ButterKnife.bind(this);
        initToolbar();
        initData();
    }

    private void initData() {
        getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
            @Override
            public void accept(List<SolarGoal> solarGoals) throws Exception {
                adapter = new SolarGoalListAdapter(SolarGoalActivity.this, getModel(), solarGoals);
                allSolarGoal.setAdapter(adapter);
                all = solarGoals;
            }
        });
        allSolarGoal.setOnItemClickListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView) myToolbar.findViewById(R.id.lunar_tool_bar_title);
        toolbarTitle.setText(getString(R.string.more_settings_solar_goal));
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
                        .content(R.string.goal_label_goal)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(getString(R.string.goal_name_goal), "",
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

    private void startSettingGoalTime() {
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        getModel().getSolarGoalDatabaseHelper().add(new SolarGoal(lableGoal, month, false))
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            SolarGoalActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    initData();
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                }).viewStyle(4)
                .viewTextSize(18)
                .dateChose("0")
                .build();
        pickerPopWin.showPopWin(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditGoalsActivity.class);
        SolarGoal solarGoal = all.get(position);
        intent.putExtra(getString(R.string.launch_edit_goal_activity_flag), SOLAR_FLAG);
        intent.putExtra(getString(R.string.key_preset_id),solarGoal.getSolarGoalId());
        startActivityForResult(intent, SOLAR_REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
                @Override
                public void accept(List<SolarGoal> solarGoals) throws Exception {
                    adapter = new SolarGoalListAdapter(SolarGoalActivity.this, getModel(), solarGoals);
                    allSolarGoal.setAdapter(adapter);
                    all = solarGoals;
                }
            });
        }
    }
}
