package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.listener.OnChangeSwitchListener;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by gaillysu on 15/12/23.
 */
public class PresetArrayAdapter extends ArrayAdapter<StepsGoal> {
    private Context context;
    private ApplicationModel model;
    private List<StepsGoal> mListStepsGoal;
    private OnChangeSwitchListener listener;

    public PresetArrayAdapter(Context context, ApplicationModel model, List<StepsGoal> listStepsGoal) {
        super(context, 0, listStepsGoal);
        this.context = context;
        this.model = model;
        this.mListStepsGoal = listStepsGoal;
    }

    public void setDataset(List<StepsGoal> listStepsGoal) {
        this.mListStepsGoal = listStepsGoal;
    }

    @Override
    public int getCount() {
        return mListStepsGoal == null ? 0 : mListStepsGoal.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_goals_list_view_item, parent, false);
        RobotoTextView presetLabel = (RobotoTextView) itemView.findViewById(R.id.activity_goals_list_view_item_goals_label);
        RobotoTextView presetValue = (RobotoTextView) itemView.findViewById(R.id.activity_goals_list_view_item_goal_steps);
        SwitchCompat presetOnOff = (SwitchCompat) itemView.findViewById(R.id.activity_goals_list_view_item_goals_switch);
        final StepsGoal stepsGoal = mListStepsGoal.get(position);
        presetLabel.setText(stepsGoal.getLabel());
        presetValue.setText(stepsGoal.getSteps() + " " + context.getString(R.string.steps_steps));
        presetOnOff.setOnCheckedChangeListener(null);
        presetOnOff.setChecked(stepsGoal.isStatus());
        presetOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StepsGoal stepsGoal = mListStepsGoal.get(position);
                for (int i = 0; i < mListStepsGoal.size(); i++) {
                    StepsGoal steps = mListStepsGoal.get(i);
                    if (steps.getId() == stepsGoal.getId()) {
                        steps.setStatus(isChecked);
                    } else {
                        steps.setStatus(false);
                    }
                    model.updateGoal(steps);
                    listener.onChangeSwitchListener();
                }
            }
        });
        return itemView;
    }

    public void dataUpdateNotification(OnChangeSwitchListener listener){
        this.listener = listener;
    }
}
