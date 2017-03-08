package com.medcorp.lunar.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.medcorp.lunar.R;


/**
 * Created by Karl on 12/22/15.
 */
public class HistoryMarkerView extends MarkerView {

    private TextView tvContent;

    public HistoryMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
//        tvContent.setText("" + e.getVal());
    }

    @Override
    public int getXOffset(float x) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float y) {
        return -getHeight();
    }

}