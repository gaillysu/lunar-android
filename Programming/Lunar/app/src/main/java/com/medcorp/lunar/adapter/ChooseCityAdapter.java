package com.medcorp.lunar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.l4digital.fastscroll.FastScroller;
import com.medcorp.lunar.R;
import com.medcorp.lunar.model.ChooseCityViewModel;
import com.medcorp.lunar.util.Preferences;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseCityAdapter extends RecyclerView.Adapter<ChooseCityAdapter.ViewHolder> implements SectionIndexer, FastScroller.SectionIndexer, View.OnClickListener {
    private List<ChooseCityViewModel> list = null;
    private Context mContext;
    private String homeCityName;
    private String homeCityCountry;
    private boolean flag;
    private RecyclerViewItemClick clickListener;

    public ChooseCityAdapter(Context mContext, List<ChooseCityViewModel> list, boolean flag) {
        this.mContext = mContext;
        this.list = list;
        this.flag = flag;
        homeCityCountry = Preferences.getPositionCountry(mContext);
        homeCityName = Preferences.getPositionCity(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.choose_city_adapter_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ChooseCityViewModel mContent = list.get(position);
        if (!(position != 0 && list.get(position).getSortLetter().equals(list.get(position - 1).getSortLetter()))) {
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.title.setText(mContent.getSortLetter());
        } else {
            viewHolder.title.setVisibility(View.INVISIBLE);
        }

        if (flag) {
            viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
        viewHolder.tvTitle.setText(mContent.getDisplayName());
        if (mContent.getDisplayName().equals(homeCityName + ", " + homeCityCountry)) {
            viewHolder.isCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.isCheck.setVisibility(View.GONE);
        }
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRecyclerViewItemClick(mContent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onRecyclerViewItemClick((ChooseCityViewModel) v.getTag());
        }
    }

    @Override
    public String getSectionText(int position) {
        return list.get(position).getSortLetter();
    }

    final static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.choose_city_item_root)
        RelativeLayout rootView;
        @Bind(R.id.choose_adapter_item_title_tv)
        TextView title;
        @Bind(R.id.choose_adapter_item_tv)
        TextView tvTitle;
        @Bind(R.id.world_clock_adapter_item_is_check)
        ImageView isCheck;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetter().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < list.size(); i++) {
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

    public void setRecyclerViewItemClick(RecyclerViewItemClick listener) {
        clickListener = listener;
    }

    public interface RecyclerViewItemClick {
        void onRecyclerViewItemClick(ChooseCityViewModel data);
    }
}