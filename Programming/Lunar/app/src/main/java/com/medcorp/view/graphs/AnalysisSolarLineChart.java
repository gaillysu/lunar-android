package com.medcorp.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.medcorp.R;
import com.medcorp.model.Solar;
import com.medcorp.util.TimeUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Karl on 8/24/16.
 */

public class AnalysisSolarLineChart extends LineChart {

    private List<Solar> solarList = new ArrayList<>();
    private int maxDays;

    public AnalysisSolarLineChart(Context context) {
        super(context);
        initGraph();

    }

    public AnalysisSolarLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public AnalysisSolarLineChart(Context context, AttributeSet attrs, int defStyle) {
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

    public void addData(List<Solar> solarList, int maxDays) {
        this.solarList = solarList;
        this.maxDays = maxDays;
        List<Entry> yValue = new ArrayList<Entry>();
        int maxValue = 0;

        final int stepsModulo = 30;
        for (int i = 0; i < maxDays; i++) {
            if (i < solarList.size()) {
                Solar solar = solarList.get(i);
                if (solar.getTotalHarvestingTime() > maxValue) {
                    maxValue = solar.getTotalHarvestingTime();
                }
                yValue.add(new Entry(i, solar.getTotalHarvestingTime()));
            } else {
                yValue.add(new Entry(i, 0));
            }
        }

        //Log.w("Karl", "Max vlaue = " + maxValue);
        if (maxValue == 0) {
            maxValue = stepsModulo;
        } else {
            maxValue = maxValue + abs(stepsModulo - (maxValue % stepsModulo));
        }

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
        if (solarList.size() > 7) {
            getXAxis().setLabelCount(solarList.size());
        }
        getXAxis().setValueFormatter(new XValueFormatter());
        dataSets.add(set);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setValueFormatter(new YValueFormatter());
        leftAxis.setAxisMaxValue(maxValue * 1.0f);
        leftAxis.setLabelCount(maxValue / 30);
        LineData data = new LineData(dataSets);
        setData(data);

        animateY(2, Easing.EasingOption.EaseInCirc);
        invalidate();
        setOnClickListener(null);
    }


    private class XValueFormatter implements AxisValueFormatter {

        private int count = 0;

        private XValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            if (solarList.isEmpty()) {
                if (count >= maxDays) {
                    count = 0;
                }
                DateTime time = new DateTime().plusDays(count);
                count += 1;
                return time.toString("dd/MM");
            }

            if (solarList.size() < 11) {
                if (count >= solarList.size()) {
                    count = 0;
                }
                Solar solar = solarList.get(count);
                count++;
                DateTime time = new DateTime(solar.getDate());

                return time.toString("dd/MM");
            } else {
                if (count == 0 || count % 5 == 0 || count == (solarList.size() - 1)) {
                    if (count >= solarList.size()) {
                        count = 0;
                    }
                    Solar solar = solarList.get(count);
                    count++;

                    DateTime time = new DateTime(solar.getDate());
                    return time.toString("dd/MM");
                } else {
                    if (count >= solarList.size()) {
                        count = 0;
                    }
                    count++;
                    return "";
                }
            }
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
