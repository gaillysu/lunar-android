package com.medcorp.lunar.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.PresetEditAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.Goal;
import com.medcorp.lunar.view.ToastHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/23.
 */
public class EditGoalsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_goals_list_view)
    ListView presetListView;

    private Goal goal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.goal_edit);
        Bundle bundle = getIntent().getExtras();
        goal = getModel().getGoalById(bundle.getInt("Preset_ID"));
        presetListView.setVisibility(View.VISIBLE);
        presetListView.setOnItemClickListener(this);
        presetListView.setAdapter(new PresetEditAdapter(this, goal));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_input)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("", "" + goal.getSteps(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0)
                                return;
                            goal.setSteps(Integer.parseInt(input.toString()));
                            presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this, goal));
                        }
                    }).negativeText(R.string.goal_cancel).show();
        } else if (position == 1) {
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title(R.string.goal_edit)
                    .content(R.string.goal_label_goal)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.goal_label), goal.getLabel(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input.length() == 0)
                                return;
                            goal.setLabel(input.toString());
                            presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this, goal));
                        }
                    }).negativeText(R.string.goal_cancel)
                    .show();
        } else if (position == 2) {
            ToastHelper.showShortToast(this, R.string.goal_deleted);
            setResult(-1);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_menu:
                if (getModel().updateGoal(goal)) {
                    ToastHelper.showShortToast(EditGoalsActivity.this, R.string.goal_saved);
                    EditGoalsActivity.this.setResult(1);
                    EditGoalsActivity.this.finish();
                } else {
                    ToastHelper.showShortToast(EditGoalsActivity.this, R.string.goal_could_not_save);
                }
                return true;
            case android.R.id.home:
                EditGoalsActivity.this.setResult(0);
                EditGoalsActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
