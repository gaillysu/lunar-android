package com.medcorp.lunar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import io.reactivex.functions.Consumer;


/**
 * Created by gaillysu on 16/1/19.
 */
public class PresetEditAdapter extends BaseAdapter {
    Context context;
    StepsGoal mStepsGoal;
    private SolarGoal solarGoal;
    private SleepGoal sleepGoal;
    private int flag;
    private ApplicationModel model;

    public PresetEditAdapter(Context context, ApplicationModel model, int flag, int goalId) {
        super();
        this.context = context;
        this.flag = flag;
        this.model = model;
        initData(flag, goalId);
    }

    private void initData(int flag, int goalId) {
        switch (flag) {
            case 0x01:
                mStepsGoal = model.getGoalById(goalId);
                break;
            case 0x02:
                model.getSolarGoalDatabaseHelper().get(goalId).subscribe(new Consumer<SolarGoal>() {
                    @Override
                    public void accept(SolarGoal goal) throws Exception {
                        solarGoal = goal;
                    }
                });
                break;
            case 0x03:
                model.getSleepDatabseHelper().get(goalId).subscribe(new Consumer<SleepGoal>() {
                    @Override
                    public void accept(SleepGoal goal) throws Exception {
                        sleepGoal = goal;
                    }
                });
                break;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        switch (flag) {
            case 0x01:
                return mStepsGoal;
            case 0x02:
                return solarGoal;
            case 0x03:
                return sleepGoal;
            default:
                return mStepsGoal;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_preset_edit_list_view_item, parent, false);

        RobotoTextView title = (RobotoTextView) itemView.findViewById(R.id.activity_preset_edit_list_view_item_title_label);
        RobotoTextView summary = (RobotoTextView) itemView.findViewById(R.id.activity_preset_edit_list_view_item_summary_label);
        RobotoTextView delete = (RobotoTextView) itemView.findViewById(R.id.activity_preset_edit_list_view_item_delete_label);
        if (position == 0) {
            setGoal(title, summary);
        } else if (position == 1) {
            setGoalName(title, summary);
        } else if (position == 2) {
            summary.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }

        return itemView;
    }

    private void setGoalName(RobotoTextView title, RobotoTextView summary) {
        switch (flag) {
            case 0x01:
                title.setText(mStepsGoal.getLabel());
                break;
            case 0x02:
                title.setText(solarGoal.getName());
                break;
            case 0x03:
                title.setText(sleepGoal.getGoalName());
                break;
        }
        summary.setText(R.string.goal_label_goal);
    }

    private void setGoal(RobotoTextView title, RobotoTextView summary) {
        switch (flag) {
            case 0x01:
                title.setText(mStepsGoal.getSteps() + "");
                break;
            case 0x02:
                title.setText( countTime(solarGoal.getTime()));
                break;
            case 0x03:
                title.setText( countTime(sleepGoal.getGoalDuration()));
                break;
        }
        summary.setText(R.string.goal_input);
    }

    private String countTime(int goalDuration) {
        StringBuffer sb = new StringBuffer();
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
