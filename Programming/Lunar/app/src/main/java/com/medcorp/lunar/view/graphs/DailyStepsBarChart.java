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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by karl-john on 18/8/2016.
 */

public class DailyStepsBarChart extends LineChart {

    public DailyStepsBarChart(Context context) {
        super(context);
        initGraph();
    }

    public DailyStepsBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public DailyStepsBarChart(Context context, AttributeSet attrs, int defStyle) {
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
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        leftAxis.setAxisMinValue(0.0f);
        leftAxis.setDrawLimitLinesBehindData(true);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLimitLinesBehindData(false);
        rightAxis.setDrawLabels(false);

        XAxis xAxis = getXAxis();
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    public void setDataInChart(int[] ChartData,int goal) {
        List<Entry> yValue = new ArrayList<>();
        int maxValue = 0;
        final int stepsModulo = 200;
        for (int i = 0; i < ChartData.length; i++) {
            BarEntry entry = new BarEntry(i, ChartData[i], i + ":???");
            yValue.add(entry);
            if (ChartData[i] > maxValue) {
                maxValue = ChartData[i];
            }
        }
        int labelCount = 6;
        if (maxValue == 0) {
            maxValue = 500;
            labelCount = 6;
        } else {
            maxValue = maxValue + abs(stepsModulo - (maxValue % stepsModulo));
            if (maxValue < 500) {
                labelCount = (maxValue / 50) + 1;
            } else {
                labelCount = (maxValue / stepsModulo) + 1;
            }
        }
        getAxisLeft().setAxisMaxValue(maxValue);
        getAxisLeft().setLabelCount(labelCount, true);

        LimitLine limitLine = new LimitLine(goal, "Goal");
        limitLine.setLineWidth(1.50f);
        limitLine.setLineColor(getResources().getColor(R.color.colorPrimary));
        limitLine.setTextSize(18f);
        limitLine.setTextColor(getResources().getColor(R.color.colorPrimary));
        LineDataSet set = new LineDataSet(yValue, "");
        set.setColor(getContext().getResources().getColor(R.color.colorPrimary));
        set.setCircleColor(R.color.transparent);
        set.setLineWidth(0.5f);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawCircles(false);
        set.setFillAlpha(128);
        set.setDrawFilled(true);
        set.setDrawValues(false);
        set.setCircleColorHole(Color.BLACK);
        set.setFillColor(getResources().getColor(R.color.colorPrimaryDark));

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.chart_gradient);
        set.setFillDrawable(drawable);
        List<ILineDataSet> dataSets = new ArrayList<>();
        //        getXAxis().setValueFormatter(new XAxisValueFormatter(dailyStepsArray.length, stepsArray));
        dataSets.add(set);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setValueFormatter(new YValueFormatter());
        leftAxis.addLimitLine(limitLine);
        leftAxis.setAxisMaxValue(maxValue * 1.0f);
        LineData data = new LineData(dataSets);
        setData(data);

        animateY(2, Easing.EasingOption.EaseInCirc);
        invalidate();
        setOnClickListener(null);
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

    private class YValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.valueOf(Math.round(value));
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}
