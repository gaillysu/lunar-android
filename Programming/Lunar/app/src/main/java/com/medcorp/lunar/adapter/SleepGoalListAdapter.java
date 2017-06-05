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
import com.medcorp.lunar.listener.OnChangeSwitchListener;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by Jason on 2017/5/27.
 */

public class SleepGoalListAdapter extends ArrayAdapter<SleepGoal> {

    private Context context;
    private ApplicationModel model;
    private List<SleepGoal> listGoal;
    private OnChangeSwitchListener listener;


    public SleepGoalListAdapter(Context context, ApplicationModel model, List<SleepGoal> listGoal) {
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
        final SleepGoal goal = listGoal.get(position);
        presetLabel.setText(goal.getGoalName());
        presetValue.setText(countTime(goal));
        presetOnOff.setOnCheckedChangeListener(null);
        presetOnOff.setChecked(goal.isStatus());
        presetOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SleepGoal goal = listGoal.get(position);
                for (int i = 0; i < listGoal.size(); i++) {
                    SleepGoal sleepGoal = listGoal.get(i);
                    if (sleepGoal.getSleepGoalId() == goal.getSleepGoalId()) {
                        sleepGoal.setStatus(isChecked);
                    } else {
                        sleepGoal.setStatus(false);
                    }
                    model.getSleepDatabseHelper().update(sleepGoal).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                Log.i("jason", "update is success");
                                listener.onChangeSwitchListener();
                            }
                        }
                    });
                }
            }
        });
        return itemView;
    }

    public void dataUpdateNotification(OnChangeSwitchListener listener) {
        this.listener = listener;
    }

    private String countTime(SleepGoal goal) {
        StringBuffer sb = new StringBuffer();
        int goalDuration = goal.getGoalDuration();
        if (goalDuration > 60) {
            sb.append(goalDuration / 60 + context.getString(R.string.sleep_unit_hour)
                    + (goalDuration % 60 != 0 ? goalDuration % 60 + context.getString(R.string.sleep_unit_minute) : ""));
        } else if (goalDuration == 60) {
            sb.append(goalDuration / 60 + context.getString(R.string.sleep_unit_hour));
        } else {
            sb.append(goalDuration + context.getString(R.string.sleep_unit_minute));
        }
        return sb.toString();
    }
}
