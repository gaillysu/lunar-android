package com.medcorp.lunar.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.medcorp.lunar.R;
import com.medcorp.lunar.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jason on 2017/7/7.
 */

public class DailySolarChart extends LineChart {

    public DailySolarChart(Context context) {
        super(context);
        initGraph();
    }

    public DailySolarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public DailySolarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGraph();
    }

    private void initGraph() {
        setContentDescription("");
        setDescription("");
        setNoDataTextDescription("");
        setNoDataText("");
        setDragEnabled(false);
        setScaleEnabled(false);
        //        setTouchEnabled(true);
        setPinchZoom(false);
        setClickable(false);
        setHighlightPerTapEnabled(false);
        setHighlightPerDragEnabled(false);
        dispatchSetSelected(false);
        getLegend().setEnabled(false);


        YAxis leftAxis = getAxisLeft();
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        leftAxis.setAxisMinValue(0.0f);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisLineColor(Color.BLACK);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLimitLinesBehindData(false);
        rightAxis.setDrawLabels(false);

        XAxis xAxis = getXAxis();
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        xAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    public void setDataInChart(int[] solarData, int goal) {
        List<Entry> yValue = new ArrayList<>();
        int maxValue = 0;
        for (int i = 0; i < solarData.length; i++) {
            BarEntry entry = new BarEntry(i, solarData[i], i + ":???");
            yValue.add(entry);
            if (solarData[i] > maxValue) {
                maxValue = solarData[i];
            }
        }

        maxValue += 120;
        LineDataSet set = new LineDataSet(yValue, "");
        set.setColor(Color.BLACK);
        set.setCircleColor(R.color.transparent);
        set.setLineWidth(1.5f);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawCircles(false);
        set.setFillAlpha(128);
        set.setDrawFilled(true);
        set.setDrawValues(false);
        set.setCircleColorHole(Color.BLACK);
        set.setFillColor(getResources().getColor(R.color.colorPrimaryDark));

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.chart_gradient);
        set.setFillDrawable(drawable);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        getXAxis().setValueFormatter(new XAxisValueFormatter());
        dataSets.add(set);

        LimitLine limitLine = new LimitLine(goal, "Goal");
        limitLine.setLineWidth(1.50f);
        limitLine.setLineColor(getResources().getColor(R.color.colorPrimary));
        limitLine.setTextSize(18f);
        limitLine.setTextColor(getResources().getColor(R.color.colorPrimary));

        YAxis leftAxis = getAxisLeft();
        leftAxis.setValueFormatter(new YValueFormatter());
        leftAxis.setAxisMaxValue(maxValue * 1.0f);
        leftAxis.setLabelCount(maxValue / 30);
        leftAxis.addLimitLine(limitLine);
        LineData data = new LineData(dataSets);
        setData(data);

        animateY(2, Easing.EasingOption.EaseInCirc);
        invalidate();
        setOnClickListener(null);
    }

    private class XAxisValueFormatter implements AxisValueFormatter {

        XAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.valueOf(Math.round(value) / 60 + " hours");
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    private class YValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int minutes = Math.round(value);
            return TimeUtil.formatTime(minutes);
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}
