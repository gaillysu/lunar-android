package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.AnalysisStepsChartAdapter;
import com.medcorp.lunar.fragment.base.BaseFragment;
import com.medcorp.lunar.model.Goal;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisStepsLineChart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AnalysisStepsFragment extends BaseFragment {

    @Bind(R.id.steps_fragment_average_steps_tv)
    TextView averageStepsText;
    @Bind(R.id.steps_fragment_total_steps_tv)
    TextView totalStepsText;
    @Bind(R.id.steps_fragment_average_calories_tv)
    TextView avgCalories;
    @Bind(R.id.steps_fragment_average_time_tv)
    TextView avgDurationTime;

    @Bind(R.id.steps_fragment_title_tv)
    TextView analysisStepsText;
    @Bind(R.id.analysis_steps_fragment_content_chart_view_pager)
    ViewPager chartViewPager;
    @Bind(R.id.ui_page_control_point)
    LinearLayout uiPageControl;

    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;
    private List<Steps> thisWeekData;
    private List<Steps> lastWeekData;
    private List<Steps> lastMonthData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View stepsView = inflater.inflate(R.layout.analysis_fragment_child_steps_fragment, container, false);
        ButterKnife.bind(this, stepsView);

        String selectDate = Preferences.getSelectDate(this.getContext());
        Date userSelectDate = new Date();
        if (selectDate != null) {
            try {
                userSelectDate = new SimpleDateFormat("yy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        initView(inflater);
        initData(userSelectDate);
        return stepsView;
    }

    private void initData(Date userSelectDate) {
        AnalysisStepsLineChart thisWeekChart = (AnalysisStepsLineChart) thisWeekView.findViewById(R.id.analysis_step_chart);
        AnalysisStepsLineChart lastWeekChart = (AnalysisStepsLineChart) lastWeekView.findViewById(R.id.analysis_step_chart);
        AnalysisStepsLineChart lastMonthChart = (AnalysisStepsLineChart) lastMonthView.findViewById(R.id.analysis_step_chart);

        /**
         * Added max in 'addData', max is the time spam in days, in 'this week' and
         * 'last week' this is 7 because 7 days is equal to a week.
         * In this month this is 30 (or 31) because there are 30 days in a month.
         *
         */
        Goal activeGoal = null;
        for (Goal goal : getModel().getAllGoal()) {
            if (goal.isStatus()) {
                activeGoal = goal;
                break;
            }
        }
        if (activeGoal == null) {
            activeGoal = new Goal(getModel().getAllGoal().size() + 1, "Unknown", true, 1000);
        }
        thisWeekData = getModel().getThisWeekSteps(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        lastWeekData = getModel().getLastWeekSteps(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        lastMonthData = getModel().getLastMonthSteps(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        setDesText(0);
        TipsView marker = new TipsView(AnalysisStepsFragment.this.getContext(), R.layout.custom_marker_view);
        thisWeekChart.addData(thisWeekData, activeGoal, 7);
        thisWeekChart.setMarkerView(marker);
        lastWeekChart.addData(lastWeekData, activeGoal, 7);
        lastWeekChart.setMarkerView(marker);
        lastMonthChart.addData(lastMonthData, activeGoal, 30);
        lastMonthChart.setMarkerView(marker);
        thisWeekChart.animateY(3000);
        lastWeekChart.animateY(3000);
        lastMonthChart.animateY(3000);
    }

    private void initView(LayoutInflater inflater) {
        final List<View> stepsDataList = new ArrayList<>(3);
        thisWeekView = inflater.inflate(R.layout.analysis_steps_chart_fragment_layout, null);
        lastWeekView = inflater.inflate(R.layout.analysis_steps_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_steps_chart_fragment_layout, null);
        stepsDataList.add(thisWeekView);
        stepsDataList.add(lastWeekView);
        stepsDataList.add(lastMonthView);

        for (int i = 0; i < stepsDataList.size(); i++) {
            ImageView imageView = new ImageView(AnalysisStepsFragment.this.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                imageView.setImageResource(R.drawable.ui_page_control_selector);
            } else {
                imageView.setImageResource(R.drawable.ui_page_control_unselector);
                layoutParams.leftMargin = 20;
            }
            uiPageControl.addView(imageView, layoutParams);
        }

        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(stepsDataList);
        chartViewPager.setAdapter(adapter);
        chartViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setDesText(position);
                int childCount = uiPageControl.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ImageView imageView = (ImageView) uiPageControl.getChildAt(i);
                    if (position == i) {
                        imageView.setImageResource(R.drawable.ui_page_control_selector);
                    } else {
                        imageView.setImageResource(R.drawable.ui_page_control_unselector);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setAverageText(int totalSteps, int averageSteps, String averageCalories, int averageDuration, String title) {
        analysisStepsText.setText(title);
        totalStepsText.setText(totalSteps + "");
        averageStepsText.setText(averageSteps + "");
        avgCalories.setText(averageCalories);
        String averageActivityTime = TimeUtil.formatTime(averageDuration);
        avgDurationTime.setText(averageActivityTime);

    }

    private int getAvgDurationTime(List<Steps> thisWeekData) {
        int durationTime = 0;
        for (Steps steps : thisWeekData) {
            durationTime += steps.getWalkDuration() + steps.getRunDistance();
        }
        return durationTime;
    }

    private String getWeekCalories(List<Steps> thisWeekData, int weekCountDay) {
        int userWeight = getModel().getNevoUser().getWeight();
        if (weekCountDay == 30) {
            return (int) ((2.0 * userWeight * 3.5) / 200 * getAvgDurationTime(thisWeekData)) / weekCountDay / 1000 + "";
        } else {
            return (int) ((2.0 * userWeight * 3.5) / 200 * getAvgDurationTime(thisWeekData)) / weekCountDay + "";
        }
    }

    private int getWeekSteps(List<Steps> thisWeekData) {
        int totalSteps = 0;
        for (Steps steps : thisWeekData) {
            totalSteps += steps.getSteps();
        }
        return totalSteps;
    }

    public void setDesText(int position) {
        switch (position) {
            case 0:
                if (thisWeekData.size() != 0) {
                    setAverageText(getWeekSteps(thisWeekData), getWeekSteps(thisWeekData) / 7
                            , getWeekCalories(thisWeekData, 7)
                            , getAvgDurationTime(thisWeekData) / 7
                            , getResources().getString(R.string.analysis_fragment_this_week_steps));
                } else {
                    setAverageText(0, 0, 0 + "", 0, getResources().getString(R.string.analysis_fragment_this_week_steps));
                }
                break;
            case 1:
                if (lastWeekData.size() != 0) {
                    setAverageText(getWeekSteps(lastWeekData), getWeekSteps(lastWeekData) / 7
                            , getWeekCalories(lastWeekData, 7)
                            , getAvgDurationTime(lastWeekData) / 7
                            , getResources().getString(R.string.analysis_fragment_last_week_steps));
                } else {
                    setAverageText(0, 0, 0 + "", 0, getResources().getString(R.string.analysis_fragment_last_week_steps));
                }
                break;
            case 2:
                if (lastMonthData.size() != 0) {
                    setAverageText(getWeekSteps(lastMonthData), getWeekSteps(lastMonthData) / 7
                            , getWeekCalories(lastMonthData, 30)
                            , getAvgDurationTime(lastMonthData) / 30 / 1000
                            , getResources().getString(R.string.analysis_fragment_last_month_solar));
                } else {
                    setAverageText(0, 0, 0 + "", 0, getResources().getString(R.string.analysis_fragment_last_month_solar));
                }
                break;
        }
    }
}
