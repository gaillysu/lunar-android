package com.medcorp.lunar.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.medcorp.lunar.R;
import com.medcorp.lunar.model.SleepData;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Karl on 8/24/16.
 */

public class AnalysisSleepLineChart extends LineChart {

    private List<SleepData> sleepList = new ArrayList<>();
    private int maxDayInGraph;

    public AnalysisSleepLineChart(Context context) {
        super(context);
        initGraph();

    }

    public AnalysisSleepLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public AnalysisSleepLineChart(Context context, AttributeSet attrs, int defStyle) {
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
        setTouchEnabled(true);
        setPinchZoom(false);
        setClickable(false);
        setHighlightPerTapEnabled(true);
        setHighlightPerDragEnabled(false);
        dispatchSetSelected(false);
        Legend legend = getLegend();
        legend.setEnabled(true);
        legend.setTextColor(getResources().getColor(R.color.graph_text_color));
        legend.setTextSize(12.0f);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(5f);
        legend.setYOffset(-5f);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setAxisLineColor(getResources().getColor(R.color.graph_grid_line_color));
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        leftAxis.setAxisMinValue(0f);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisLineColor(getResources().getColor(R.color.graph_grid_line_color));
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLimitLinesBehindData(false);
        rightAxis.setDrawLabels(false);

        XAxis xAxis = getXAxis();
        xAxis.setAxisLineColor(getResources().getColor(R.color.graph_grid_line_color));
        xAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    public void addData(List<SleepData> sleepList,int maxDayInGraph) {
        this.sleepList = sleepList;
        this.maxDayInGraph = maxDayInGraph;
        List<Entry> wakeEntries = new ArrayList<>();
        List<Entry> lightSleepEntries = new ArrayList<>();
        List<Entry> deepSleepEntries = new ArrayList<>();
        int maxValue = 0;

        for (int i = 0; i < this.sleepList.size(); i++) {
            if (i < this.sleepList.size()) {
                SleepData sleep = this.sleepList.get(i);

                wakeEntries.add(new Entry(i, sleep.getAwake()));
                lightSleepEntries.add(new Entry(i, sleep.getLightSleep()));
                deepSleepEntries.add(new Entry(i, sleep.getDeepSleep()));

                if (maxValue < sleep.getLightSleep()) {
                    maxValue = sleep.getLightSleep();
                }
                if (maxValue < sleep.getDeepSleep()) {
                    maxValue = sleep.getDeepSleep();
                }
                if (maxValue < sleep.getAwake()) {
                    maxValue = sleep.getAwake();
                }
            } else {
                wakeEntries.add(new Entry(i, 0));
                lightSleepEntries.add(new Entry(i, 0));
                deepSleepEntries.add(new Entry(i, 0));
            }
        }
        maxValue += 120;
        maxValue = ((Math.round(maxValue / 60)) * 60);
        List<ILineDataSet> dataSets = new ArrayList<>();

        getXAxis().setLabelCount(this.sleepList.size() - 1);
        getXAxis().setValueFormatter(new XValueFormatter());
        Drawable lightGradient = ContextCompat.getDrawable(getContext(), R.drawable.analysis_sleep_light_gradient);
        Drawable deepGradient = ContextCompat.getDrawable(getContext(), R.drawable.analysis_sleep_deep_gradient);
        Drawable wakeGradient = ContextCompat.getDrawable(getContext(), R.drawable.analysis_sleep_wake_gradient);
        dataSets.add(getDataSet(wakeEntries, "Awake", wakeGradient, R.color.analysis_sleep_wake_line_color));
        dataSets.add(getDataSet(lightSleepEntries, "Light Sleep", lightGradient, R.color.analysis_sleep_light_line_color));
        dataSets.add(getDataSet(deepSleepEntries, "Deep Sleep", deepGradient, R.color.analysis_sleep_deep_line_color));


        YAxis leftAxis = getAxisLeft();
        leftAxis.setValueFormatter(new YValueFormatter());
        leftAxis.setAxisMaxValue((maxValue - 60) * 1.0f);
        leftAxis.setLabelCount(maxValue / 60, true);
        LineData data = new LineData(dataSets);
        setData(data);
        animateY(2, Easing.EasingOption.EaseInCirc);
        invalidate();
        String[] legend = getLegend().getLabels();
        // TODO Figure out to get the color from colors.xml resource.
        getLegend().setCustom(Arrays.asList(Color.rgb(126, 216, 206),
                Color.rgb(179, 126, 189), Color.rgb(229, 228, 226)),
                Arrays.asList(getLegend().getLabels()));
        setOnClickListener(null);
    }

    private LineDataSet getDataSet(List<Entry> entries, String setName, Drawable gradient, @ColorRes int lineColor) {
        LineDataSet set = new LineDataSet(entries, setName);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setColors(new int[]{lineColor});
        set.setLineWidth(0.8f);
        set.setDrawCircles(false);
        set.setFillAlpha(60);
        set.setLabel(setName);
        set.setDrawFilled(true);
        set.setDrawValues(false);
        set.setFillDrawable(gradient);
        return set;
    }


    private class XValueFormatter implements AxisValueFormatter {
        private int count = 0;

        private XValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            if (sleepList.isEmpty()) {
                if (count >= maxDayInGraph) {
                    count = 0;
                }
                DateTime time = new DateTime().plusDays(count);
                count += 1;
                return time.toString("dd/MM");
            }

            if (sleepList.size() < 11) {
                if (count >= sleepList.size()) {
                    count = 0;
                }
                SleepData sleepData = sleepList.get(count);
                count++;
                DateTime time = new DateTime(sleepData.getDate());
                return time.toString("dd/MM");
            } else {
                if (count == 0 || count % 5 == 0 || count == (sleepList.size() - 1)) {
                    if (count >= sleepList.size()) {
                        count = 0;
                    }
                    SleepData sleepData = sleepList.get(count);
                    count++;

                    DateTime time = new DateTime(sleepData.getDate());
                    return time.toString("dd/MM");
                } else {
                    if (count >= sleepList.size()) {
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
            return String.valueOf(Math.round(value) / 60 + " hours");
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}