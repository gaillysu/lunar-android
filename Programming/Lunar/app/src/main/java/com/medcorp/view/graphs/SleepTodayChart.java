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
import com.medcorp.model.SleepData;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by karl-john on 19/8/2016.
 */

public class SleepTodayChart extends LineChart {

    public SleepTodayChart(Context context) {
        super(context);
        initGraph();
    }

    public SleepTodayChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public SleepTodayChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGraph();
    }

    public void initGraph() {
        setContentDescription("");
        setDescription("");
        setNoDataTextDescription("");
        setNoDataText("");
        setDragEnabled(false);
        setScaleEnabled(false);
        setPinchZoom(false);
        setClickable(false);
        setHighlightPerTapEnabled(false);
        setHighlightPerDragEnabled(false);
        dispatchSetSelected(false);
        getLegend().setEnabled(false);


        YAxis leftAxis = getAxisLeft();
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        leftAxis.setAxisMinValue(-0.1f);
        leftAxis.setAxisMaxValue(2.6f);
        leftAxis.setValueFormatter(new YAxisValueFormatter());
        leftAxis.setLabelCount(3);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisLineColor(Color.BLACK);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0.0f);
        rightAxis.setDrawLimitLinesBehindData(false);
        rightAxis.setDrawLabels(false);

        XAxis xAxis = getXAxis();
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    public void setDataInChart(SleepData sleepData) {
        List<Entry> yValue = new ArrayList<>();
        List<Float> intList = new ArrayList<>();
        int[] hourlyWakeTime = sleepData.getHourlyWakeInt();
        int[] hourlyLightSleepTime = sleepData.getHourlyLightInt();
        int[] hourlyDeepSleepTime = sleepData.getHourlyDeepInt();
        int sleptHours = Collections.max(Arrays.asList(hourlyWakeTime.length, hourlyLightSleepTime.length, hourlyDeepSleepTime.length));
        for (int hour = 0; hour < sleptHours; hour++) {
            int awakeMinutes = hourlyWakeTime[hour];
            int lightSleepMinutes = hourlyLightSleepTime[hour];
            int deepSleepMinutes = hourlyDeepSleepTime[hour];
            if (awakeMinutes == 0 && lightSleepMinutes == 0 && deepSleepMinutes == 0) {
                continue;
            }
            float deep = 1.0f * awakeMinutes / (awakeMinutes + lightSleepMinutes + deepSleepMinutes);
            intList.add(2.3f - (deep * 2.0f == 0 ? 0.1f : deep * 2.0f));
        }

        for (int hour = 0; hour < intList.size(); hour++) {
            yValue.add(new Entry(hour, intList.get(hour)));
        }

        LineDataSet set = new LineDataSet(yValue, "");
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(0.5f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setFillAlpha(128);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawFilled(true);
        set.setDrawValues(false);
        set.setCircleColorHole(Color.BLACK);
        set.setFillColor(getResources().getColor(R.color.colorPrimary));
        Drawable lightGradient = ContextCompat.getDrawable(getContext(), R.drawable.analysis_sleep_light_gradient);
        set.setFillDrawable(lightGradient);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        LineData data = new LineData(dataSets);
        setData(data);

        animateY(2, Easing.EasingOption.EaseInCirc);
        invalidate();
        getXAxis().setLabelCount(intList.size() - 1);
        getXAxis().setValueFormatter(new XAxisValueFormatter(intList.size(), new DateTime(sleepData.getSleepStart())));

    }

    public void setEmptyChart() {

    }

    private class YAxisValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            if (value <= 0  &&value>=-0.3) {
                return getContext().getResources().getString(R.string.sleep_awake);
            } else if (value == 0 && value < 0.2) {
                return "";
            } else if (value >= 0.5 && value <= 1.5) {
                return getContext().getResources().getString(R.string.sleep_light_sleep);
            } else if (value >= 1.6 && value <= 2.6) {
                return getContext().getResources().getString(R.string.sleep_deep_sleep);
            }
            return "?";

        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    private class XAxisValueFormatter implements AxisValueFormatter {

        private final int size;
        private final DateTime startDate;

        XAxisValueFormatter(int size, DateTime startDate) {
            this.size = size;
            this.startDate = startDate;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            DateTime newDate = startDate.plusHours((int) (value));
            return String.valueOf(newDate.getHourOfDay()) + ":00";
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}
