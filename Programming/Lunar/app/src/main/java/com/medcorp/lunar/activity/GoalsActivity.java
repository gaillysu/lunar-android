package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.PresetArrayAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.Goal;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/23.
 */
public class GoalsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_goals_list_view)
    ListView presetListView;

    List<Goal> goalList;
    PresetArrayAdapter presetArrayAdapter;
    Goal goal;
    private int steps = 0;
    private String lableGoal;

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
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.steps_goal_title);
        presetListView.setVisibility(View.VISIBLE);
        goalList = getModel().getAllGoal();
        presetArrayAdapter = new PresetArrayAdapter(this, getModel(), goalList);
        presetListView.setAdapter(presetArrayAdapter);
        presetListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditGoalsActivity.class);
        goal = goalList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.key_preset_id), goal.getId());
        bundle.putString(getString(R.string.key_preset_label), goal.getLabel());
        bundle.putInt(getString(R.string.key_preset_steps), goal.getSteps());
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //delete or update the goal, refresh list
        if (resultCode != 0) {
            goalList = getModel().getAllGoal();
            presetArrayAdapter.setDataset(goalList);
            presetArrayAdapter.notifyDataSetChanged();
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
                new MaterialDialog.Builder(GoalsActivity.this)
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
                                        new MaterialDialog.Builder(GoalsActivity.this)
                                                .title(R.string.goal_add)
                                                .content(R.string.goal_label_goal)
                                                .inputType(InputType.TYPE_CLASS_TEXT)
                                                .input(getString(R.string.goal_name_goal), "",
                                                        new MaterialDialog.InputCallback() {
                                                            @Override
                                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                                if (input.length() == 0) {
                                                                    lableGoal = getString(R.string.def_goal_name) + " " + (goalList.size() + 1);
                                                                } else {
                                                                    lableGoal = input.toString();
                                                                }

                                                                goal = new Goal(getModel().getAllGoal().size()+1,lableGoal, true, steps);
                                                                getModel().addGoal(goal);
                                                                goalList = getModel().getAllGoal();
                                                                presetArrayAdapter.setDataset(goalList);
                                                                presetArrayAdapter.notifyDataSetChanged();
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
