package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.PresetArrayAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.fragment.MainClockFragment;
import com.medcorp.lunar.model.StepsGoal;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/23.
 */
public class StepsGoalsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_goals_list_view)
    ListView presetListView;

    List<StepsGoal> mStepsGoalList;
    PresetArrayAdapter presetArrayAdapter;
    StepsGoal mStepsGoal;
    private int steps = 0;
    private String lableGoal;
    private static final int STEPS_FLAG = 0X01;
    private static final int STEPS_REQUEST_CODE = 0X01<<3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        initToolbar();
        presetListView.setVisibility(View.VISIBLE);
        getModel().getAllGoal(new MainClockFragment.ObtainGoalListener() {
            @Override
            public void obtainGoal(List<StepsGoal> list) {
                mStepsGoalList = list;
                presetArrayAdapter = new PresetArrayAdapter(StepsGoalsActivity.this, getModel(), mStepsGoalList);
                presetListView.setAdapter(presetArrayAdapter);
                presetListView.setOnItemClickListener(StepsGoalsActivity.this);

            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        toolbarTitle.setText(getString(R.string.more_settings_steps_goal));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditGoalsActivity.class);
        intent.putExtra(getString(R.string.launch_edit_goal_activity_flag),STEPS_FLAG);
        intent.putExtra(getString(R.string.key_preset_id), mStepsGoalList.get(position).getId());
        startActivityForResult(intent, STEPS_REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //delete or update the mStepsGoal, refresh list
        if (resultCode != 0) {
            getModel().getAllGoal(new MainClockFragment.ObtainGoalListener() {
                @Override
                public void obtainGoal(List<StepsGoal> stepsGoalList) {
                    presetArrayAdapter.setDataset(stepsGoalList);
                    presetArrayAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_menu).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
                new MaterialDialog.Builder(StepsGoalsActivity.this)
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
                                        new MaterialDialog.Builder(StepsGoalsActivity.this)
                                                .title(R.string.goal_add)
                                                .content(R.string.goal_label_goal)
                                                .inputType(InputType.TYPE_CLASS_TEXT)
                                                .input(getString(R.string.goal_name_goal), "",
                                                        new MaterialDialog.InputCallback() {
                                                            @Override
                                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                                if (input.length() == 0) {
                                                                    lableGoal = getString(R.string.def_goal_name) + " " + (mStepsGoalList.size() + 1);
                                                                } else {
                                                                    lableGoal = input.toString();
                                                                }

                                                                mStepsGoal = new StepsGoal(lableGoal, true, steps);
                                                                getModel().addGoal(mStepsGoal);
                                                                getModel().getAllGoal(new MainClockFragment.ObtainGoalListener() {
                                                                    @Override
                                                                    public void obtainGoal(List<StepsGoal> stepsGoalList) {
                                                                        presetArrayAdapter.setDataset(stepsGoalList);
                                                                        presetArrayAdapter.notifyDataSetChanged();
                                                                    }
                                                                });
                                                            }
                                                        }).negativeText(R.string.goal_cancel)
                                                .show();
                                    }
                                }).negativeText(R.string.goal_cancel)
                        .show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
