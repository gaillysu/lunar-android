package com.medcorp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.model.ChooseCityViewModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/10/31.
 */

public class SearchWorldAdapter extends BaseAdapter {

    private List<ChooseCityViewModel> cityList;
    private Context mContext;

    public SearchWorldAdapter(List<ChooseCityViewModel> cityList, Context context) {
        this.cityList = cityList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return cityList.size() != 0 ? cityList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position) != null ? cityList.size() : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_world_clock_pull_menu_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.resultTextView.setText(cityList.get(position).getDisplayName());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.search_city_result_tv)
        TextView resultTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
