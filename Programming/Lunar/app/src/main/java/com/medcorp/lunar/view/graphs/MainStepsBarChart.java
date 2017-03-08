package com.medcorp.lunar.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.medcorp.lunar.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by karl-john on 18/8/2016.
 *
 */

public class MainStepsBarChart extends BarChart implements AxisValueFormatter{
    private int[] steps = new int[0];
    public MainStepsBarChart(Context context) {
        super(context);
        initGraph();
    }

    public MainStepsBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public MainStepsBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGraph();
    }

    public void initGraph(){
        final float widthLines = 1.0f;
        getLegend().setEnabled(false);
//        setOnChartValueSelectedListener(this);
        setPinchZoom(false);
        setDrawValueAboveBar(false);
        setDoubleTapToZoomEnabled(false);
        setDragEnabled(false);
        setSelected(false);
        setScaleEnabled(false);
        setDescription("");
        setDrawGridBackground(true);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.window_background_color));
        setGridBackgroundColor(ContextCompat.getColor(getContext(), R.color.window_background_color));
        setDrawingCacheBackgroundColor(ContextCompat.getColor(getContext(),R.color.graph_grid_line_color));
        setBorderWidth(widthLines);
        setBorderColor(Color.BLACK);
        setDrawBorders(true);


        YAxis leftAxis = getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.graph_grid_line_color));
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setAxisLineWidth(widthLines);
        leftAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        leftAxis.setGridLineWidth(widthLines);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinValue(0.0f);
        leftAxis.setDrawLabels(true);
        leftAxis.mAxisMinimum = 0.0f;
        leftAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%d",(long)value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.graph_grid_line_color));
        rightAxis.setAxisLineColor(Color.BLACK);
        rightAxis.setDrawLimitLinesBehindData(true);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setGridLineWidth(widthLines);

        XAxis xAxis = getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(getResources().getColor(R.color.graph_text_color));
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setGridLineWidth(widthLines);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(6);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setValueFormatter(this);

    }

    public void setDataInChart(int[] stepsArray) {
        if (stepsArray.length != 24){
            int[] stepsArrayCopy = stepsArray;
            stepsArray = new int[24];
            for (int i = 0; i < 24; i++){
                if (i > stepsArrayCopy.length -1 ){
                    stepsArray[i] = 0;
                }else{
                    stepsArray[i] = stepsArrayCopy[i];
                }
            }
        }
        List<BarEntry> yValue = new ArrayList<>();
        int maxValue = 0;
        final int stepsModulo = 200;
        for (int i = 0; i < stepsArray.length; i++) {
            BarEntry entry = new BarEntry(i, stepsArray[i],i + ":???");
            yValue.add(entry);
            if (stepsArray[i] > maxValue) {
                maxValue = stepsArray[i];
            }
        }
        int labelCount = 6;
        if (maxValue == 0) {
            maxValue = 500;
            labelCount = 6;
        } else {
            maxValue = maxValue + abs(stepsModulo - (maxValue % stepsModulo));
            if (maxValue < 500){
                labelCount = (maxValue/50) +1;
            }else{
                labelCount = (maxValue/stepsModulo) +1;
            }
            labelCount = 6;
        }
        getAxisLeft().setAxisMaxValue(maxValue);
        getAxisLeft().setLabelCount(labelCount,true);

        setScaleMinima((.14f), 1f);
        BarDataSet dataSet = new BarDataSet(yValue, "");

        dataSet.setDrawValues(false);
        dataSet.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorPrimary)});
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.95f);
        setData(data);
        invalidate();
    }

    public int getTotalSteps(){
        int totalSteps = 0;
        for (int i = 0; i < steps.length; i++){
            totalSteps += steps[i];
        }
        return totalSteps;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return (long)value + ":00";
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
