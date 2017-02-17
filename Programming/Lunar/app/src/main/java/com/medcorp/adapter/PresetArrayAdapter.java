package com.medcorp.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.medcorp.R;
import com.medcorp.application.ApplicationModel;
import com.medcorp.model.Goal;
import com.medcorp.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by gaillysu on 15/12/23.
 */
public class PresetArrayAdapter extends ArrayAdapter<Goal> {
    private Context context;
    private ApplicationModel model;
    private List<Goal> listGoal;

    public PresetArrayAdapter(Context context,ApplicationModel model, List<Goal> listGoal)
    {
        super(context,0, listGoal);
        this.context = context;
        this.model = model;
        this.listGoal = listGoal;
    }

    public void setDataset(List<Goal> listGoal)
    {
        this.listGoal = listGoal;
    }

    @Override
    public int getCount() {
        return listGoal ==null?0: listGoal.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_goals_list_view_item, parent, false);
        RobotoTextView  presetLabel = (RobotoTextView)itemView.findViewById(R.id.activity_goals_list_view_item_goals_label);
        RobotoTextView  presetValue = (RobotoTextView)itemView.findViewById(R.id.activity_goals_list_view_item_goal_steps);
        SwitchCompat presetOnOff = (SwitchCompat) itemView.findViewById(R.id.activity_goals_list_view_item_goals_switch);
        final Goal goal = listGoal.get(position);
        presetLabel.setText(goal.getLabel());
        presetValue.setText(goal.getSteps() + " " + context.getString(R.string.steps_steps));
        presetOnOff.setOnCheckedChangeListener(null);
        presetOnOff.setChecked(goal.isStatus());
        presetOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Goal goal = listGoal.get(position);
                goal.setStatus(isChecked);
                model.updateGoal(goal);
            }
        });
        return itemView;
    }
}
