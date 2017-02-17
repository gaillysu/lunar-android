package com.medcorp.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.medcorp.R;

import java.util.List;

/**
 * Created by dengxiongcai on 16/12/27.
 */
public class MySpinnerAdapter implements SpinnerAdapter {
    private Context context;
    private List<String> contentLists;

    public MySpinnerAdapter(Context context, List<String> contentLists) {
        super();
        this.context = context;
        this.contentLists = contentLists;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return null == contentLists ? 0 : contentLists.size();
    }

    @Override
    public Object getItem(int position) {
        return contentLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setTextSize(10);
        textView.setTextColor(context.getResources().getColor(R.color.text_color));
        textView.setText(contentLists.get(position));
        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);

        View bottomLine = convertView.findViewById(R.id.bottom_line);
        TextView content = (TextView) convertView.findViewById(R.id.content_tv);

        bottomLine.setVisibility(position >= contentLists.size() - 1 ? View.GONE : View.VISIBLE);
        content.setText(contentLists.get(position));

        return convertView;
    }
}
