package com.medcorp.lunar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.LedLamp;

import java.util.List;

/**
 * Created by Jason on 2017/7/18.
 */

public class ChooseColorAdapter extends BaseAdapter {

    private Context context;
    private List<LedLamp> allList;

    public ChooseColorAdapter(Context context, List<LedLamp> allLamp) {
        this.context = context;
        this.allList = allLamp;
    }

    @Override
    public int getCount() {
        return allList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return allList.get(position) != null ? allList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.choose_color_adater_item_view, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.colorIv = (ImageView) convertView.findViewById(R.id.choose_color_adapter_item_iv);
        viewHolder.isChoose = (ImageView) convertView.findViewById(R.id.choose_color_adapter_is_choose);
        if (position < allList.size()) {
            LedLamp ledLamp = allList.get(position);
            if(ledLamp.isSelect()){
                viewHolder.isChoose.setVisibility(View.VISIBLE);
            }else{
                viewHolder.isChoose.setVisibility(View.GONE);
            }
            viewHolder.colorIv.setColorFilter(ledLamp.getColor());
        } else {
            viewHolder.colorIv.setImageResource(R.drawable.ic_choose_color_icon);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView colorIv;
        ImageView isChoose;
    }
}
