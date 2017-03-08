package com.medcorp.lunar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.model.ChooseCityViewModel;
import com.medcorp.lunar.util.Preferences;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseCityAdapter extends BaseAdapter implements SectionIndexer {
    private List<ChooseCityViewModel> list = null;
    private Context mContext;
    private String homeCityName;
    private String homeCityCountry;

    public ChooseCityAdapter(Context mContext, List<ChooseCityViewModel> list) {
        this.mContext = mContext;
        this.list = list;

        homeCityCountry = Preferences.getPositionCountry(mContext);
        homeCityName = Preferences.getPositionCity(mContext);
    }

    public void updateListView(List<ChooseCityViewModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final ChooseCityViewModel mContent = list.get(position);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.choose_city_adapter_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvTitle.setText(mContent.getDisplayName());
        if (mContent.getDisplayName().equals(homeCityName + ", " + homeCityCountry)) {
            viewHolder.isCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.isCheck.setVisibility(View.GONE);
        }
        return view;

    }


    final static class ViewHolder {
        @Bind(R.id.choose_adapter_item_tv)
        TextView tvTitle;
        @Bind(R.id.world_clock_adapter_item_is_check)
        ImageView isCheck;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetter().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}