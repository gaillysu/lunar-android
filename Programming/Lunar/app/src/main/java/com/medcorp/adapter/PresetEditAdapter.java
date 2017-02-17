package com.medcorp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medcorp.R;
import com.medcorp.model.Goal;
import com.medcorp.view.customfontview.RobotoTextView;

/**
 * Created by gaillysu on 16/1/19.
 */
public class PresetEditAdapter extends BaseAdapter {
    Context context;
    Goal goal;

    public PresetEditAdapter(Context context, Goal goal)
    {
        super();
        this.context = context;
        this.goal = goal;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return goal;
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

        RobotoTextView title = (RobotoTextView)itemView.findViewById(R.id.activity_preset_edit_list_view_item_title_label);
        RobotoTextView summary = (RobotoTextView)itemView.findViewById(R.id.activity_preset_edit_list_view_item_summary_label);
        RobotoTextView delete = (RobotoTextView)itemView.findViewById(R.id.activity_preset_edit_list_view_item_delete_label);
        if(position == 0)
        {
            title.setText(goal.getSteps()+"");
            summary.setText(R.string.goal_input);
        }
        else if(position == 1)
        {
            title.setText(goal.getLabel());
            summary.setText(R.string.goal_label_goal);
        }
        else if(position == 2)
        {
            summary.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }

        return itemView;
    }
}
