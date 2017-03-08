package com.medcorp.lunar.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.DfuActivity;
import com.medcorp.lunar.activity.MyNevoActivity;
import com.medcorp.lunar.model.MyNevo;
import com.medcorp.lunar.view.customfontview.RobotoTextView;

import java.util.ArrayList;


/**
 * Created by gaillysu on 15/12/28.
 */
public class MyNevoAdapter extends BaseAdapter {

    private Context context;
    private MyNevo mynevo;

    public MyNevoAdapter(Context context, MyNevo mynevo)
    {
        super();
        this.context = context;
        this.mynevo = mynevo;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return mynevo;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_mynevo_list_view_item, parent, false);
        RobotoTextView title = (RobotoTextView)itemView.findViewById(R.id.activity_mynevo_list_view_item_title_label);
        RobotoTextView values = (RobotoTextView)itemView.findViewById(R.id.activity_mynevo_list_view_item_version_label);
        RobotoTextView infomation = (RobotoTextView)itemView.findViewById(R.id.activity_mynevo_list_view_item_infomation_label);
        ImageView image = (ImageView)itemView.findViewById(R.id.activity_mynevo_list_view_item_update_image);
        if(position == 0)
        {
            title.setText(R.string.my_nevo_firmware);
            values.setText(mynevo.getBleFirmwareVersion() + "/" + mynevo.getMcuFirmwareVersion());
            if(mynevo.isAvailableVersion())
            {
                infomation.setText(R.string.my_nevo_new_version);
                image.setVisibility(View.VISIBLE);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DfuActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList(context.getString(R.string.key_firmwares),(ArrayList<String>)mynevo.getFirmwareURLs());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        ((MyNevoActivity)context).finish();
                    }
                });
            }
            else
            {
                infomation.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
            }
        }
        else if(position == 1)
        {
            infomation.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
            title.setText(R.string.my_nevo_battery);
            String str_battery=context.getString(R.string.my_nevo_battery_low);
            if(mynevo.getBatteryLevel()==2)
            {
                str_battery = context.getString(R.string.my_nevo_battery_full);
            }
            else if(mynevo.getBatteryLevel()==1)
            {
                str_battery = context.getString(R.string.my_nevo_battery_half);
            }
            values.setText(str_battery);
        }
        else if(position == 2)
        {
            infomation.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
            title.setText(R.string.my_nevo_application);
            values.setText(mynevo.getAppVersion());
        }

        return itemView;
    }
}
