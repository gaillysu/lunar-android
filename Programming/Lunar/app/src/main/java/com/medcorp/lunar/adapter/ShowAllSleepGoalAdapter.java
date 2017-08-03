package com.medcorp.lunar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.SleepGoal;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * Created by Jason on 2017/8/2.
 */

public class ShowAllSleepGoalAdapter extends BaseAdapter {

    private Context context;
    private List<SleepGoal> allSleepGoal;

    public ShowAllSleepGoalAdapter(Context context, List<SleepGoal> allSleepGoal) {
        this.context = context;
        this.allSleepGoal = allSleepGoal;
    }

    @Override
    public int getCount() {
        return allSleepGoal.size();
    }

    @Override
    public Object getItem(int position) {
        return allSleepGoal.get(position) == null ? null : allSleepGoal.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.show_all_sleep_goal_list_item, null);
            viewholder = new ViewHolder(convertView);
            convertView.setTag(viewholder);
        }else{
            viewholder = (ViewHolder) convertView.getTag();
        }
        SleepGoal sleepGoal = allSleepGoal.get(position);
        if(sleepGoal!=null){
            viewholder.goalText.setText(sleepGoal.getGoalName()+" - "+sleepGoal.toString());
        }
        return convertView;
    }

    class ViewHolder {

        @Bind(R.id.show_all_sleep_goal_list_item_text)
        TextView goalText;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
