package com.medcorp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medcorp.R;
import com.medcorp.model.Alarm;
import com.medcorp.view.customfontview.RobotoTextView;

/**
 * Created by gaillysu on 16/1/19.
 */
public class AlarmEditAdapter extends BaseAdapter {
    Context context;
    Alarm alarm;

    public AlarmEditAdapter(Context context, Alarm alarm) {
        super();
        this.context = context;
        this.alarm = alarm;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return alarm;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_alarm_edit_list_view_item, parent, false);

        RobotoTextView title = (RobotoTextView) itemView.findViewById(R.id.activity_alarm_edit_list_view_item_title_label);
        RobotoTextView summary = (RobotoTextView) itemView.findViewById(R.id.activity_alarm_edit_list_view_item_summary_label);
        RobotoTextView delete = (RobotoTextView) itemView.findViewById(R.id.activity_alarm_edit_list_view_item_delete_label);

        if (position == 0) {
            title.setText(alarm.toString());
            summary.setText(context.getString(R.string.alarm_set_different_time));
        } else if (position == 1) {
            title.setText(alarm.getLabel());
            summary.setText(context.getString(R.string.alarm_set_label_for_alarm));
        } else if (position == 2) {
            String[] weekDayArray = context.getResources().getStringArray(R.array.week_day);
            String weekDay = weekDayArray[alarm.getWeekDay() & 0x0F];
            title.setText(weekDay);
            summary.setText(context.getString(R.string.alarm_set_week_day));
        } else if (position == 3) {
            summary.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }
        return itemView;
    }
}
