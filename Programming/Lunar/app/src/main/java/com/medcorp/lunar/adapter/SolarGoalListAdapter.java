package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by Jason on 2017/5/27.
 */

public class SolarGoalListAdapter extends ArrayAdapter<SolarGoal> {

    private Context context;
    private ApplicationModel model;
    private List<SolarGoal> listGoal;

    public SolarGoalListAdapter(Context context, ApplicationModel model, List<SolarGoal> listGoal) {
        super(context, 0, listGoal);
        this.context = context;
        this.model = model;
        this.listGoal = listGoal;
    }

    @Override
    public int getCount() {
        return listGoal == null ? 0 : listGoal.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_goals_list_view_item, parent, false);
        RobotoTextView presetLabel = (RobotoTextView) itemView.findViewById(R.id.activity_goals_list_view_item_goals_label);
        RobotoTextView presetValue = (RobotoTextView) itemView.findViewById(R.id.activity_goals_list_view_item_goal_steps);
        SwitchCompat presetOnOff = (SwitchCompat) itemView.findViewById(R.id.activity_goals_list_view_item_goals_switch);
        final SolarGoal goal = listGoal.get(position);
        presetLabel.setText(goal.getName());
        presetValue.setText(countTime(goal));
        presetOnOff.setOnCheckedChangeListener(null);
        presetOnOff.setChecked(goal.isStatus());
        presetOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SolarGoal goal = listGoal.get(position);
                goal.setStatus(isChecked);
                model.getSolarGoalDatabaseHelper().update(goal).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Log.i("jason", "update is success");
                        }
                    }
                });
            }
        });
        return itemView;
    }

    private String countTime(SolarGoal goal) {
        StringBuffer sb = new StringBuffer();
        int goalDuration = goal.getTime();

        if (goalDuration > 60) {
            sb.append(goalDuration / 60 + context.getString(R.string.sleep_unit_hour));
            int minutes = goalDuration % 60;
            sb.append(minutes != 0 ? minutes + context.getString(R.string.sleep_unit_minute) : "");
        } else if (goalDuration == 60) {
            sb.append(goalDuration / 60 + context.getString(R.string.sleep_unit_hour));
        } else {
            sb.append(goalDuration + context.getString(R.string.sleep_unit_minute));
        }
        return sb.toString();
    }
}
