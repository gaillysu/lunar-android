package com.medcorp.lunar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.SleepGoalListAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.database.entry.SleepGoalDatabaseHelper;
import com.medcorp.lunar.model.SleepGoal;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by Jason on 2017/5/27.
 */

public class SleepGoalActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar myToolbar;
    @Bind(R.id.activity_sleep_goals_list_view)
    ListView allSleepGoal;

    private SleepGoalDatabaseHelper sleepDatabaseHelper;
    private SleepGoalListAdapter adapter;
    private List<SleepGoal> all;
    private String lableGoal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_sleep_goal_activity);
        ButterKnife.bind(this);
        initToolbar();
        initData();
    }

    private void initData() {
        sleepDatabaseHelper = getModel().getSleepDatabseHelper();
        sleepDatabaseHelper.getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(List<SleepGoal> sleepGoals) throws Exception {
                adapter = new SleepGoalListAdapter(SleepGoalActivity.this,getModel(),sleepGoals);
                allSleepGoal.setAdapter(adapter);
                all = sleepGoals;
            }
        });
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
        switch(item.getItemId()){
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

    }
}
