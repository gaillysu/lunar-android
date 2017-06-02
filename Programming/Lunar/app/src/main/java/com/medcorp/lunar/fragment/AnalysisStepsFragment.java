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
import com.medcorp.lunar.event.ChangeGoalEvent;
import com.medcorp.lunar.fragment.base.BaseFragment;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisStepsLineChart;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    private AnalysisStepsLineChart thisWeekChart;
    private AnalysisStepsLineChart lastWeekChart;
    private AnalysisStepsLineChart lastMonthChart;
    private StepsGoal mActiveStepsGoal;
    private List<Steps> thisWeekData = new ArrayList<>();
    private List<Steps> lastWeekData = new ArrayList<>();
    private List<Steps> lastMonthData = new ArrayList<>();
    private Date mUserSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View stepsView = inflater.inflate(R.layout.analysis_fragment_child_steps_fragment, container, false);
        ButterKnife.bind(this, stepsView);

        String selectDate = Preferences.getSelectDate(this.getContext());
        mUserSelectDate = new Date();
        if (selectDate != null) {
            try {
                mUserSelectDate = new SimpleDateFormat("yy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        initView(inflater);
        initData(mUserSelectDate);
        return stepsView;
    }

    private void initData(Date userSelectDate) {
        thisWeekChart = (AnalysisStepsLineChart) thisWeekView.findViewById(R.id.analysis_step_chart);
        lastWeekChart = (AnalysisStepsLineChart) lastWeekView.findViewById(R.id.analysis_step_chart);
        lastMonthChart = (AnalysisStepsLineChart) lastMonthView.findViewById(R.id.analysis_step_chart);

        final TipsView marker = new TipsView(AnalysisStepsFragment.this.getContext(), R.layout.custom_marker_view);
        /**
         * Added max in 'addData', max is the time spam in days, in 'this week' and
         * 'last week' this is 7 because 7 days is equal to a week.
         * In this month this is 30 (or 31) because there are 30 days in a month.
         *
         */
        getModel().getAllGoal(new MainFragment.ObtainGoalListener() {
            @Override
            public void obtainGoal(List<StepsGoal> list) {
                if (list != null) {
                    for (StepsGoal stepsGoal : list) {
                        if (stepsGoal.isStatus()) {
                            mActiveStepsGoal = stepsGoal;
                            break;
                        }
                    }
                }
            }
        });
        if (mActiveStepsGoal == null) {
            mActiveStepsGoal = new StepsGoal("Unknown", true, 10000);
        }

        setDesText(0);

        getModel().getSteps(getModel().getUser().getUserID(), userSelectDate
                , WeekData.TISHWEEK, new OnStepsGetListener() {
                    @Override
                    public void onStepsGet(List<Steps> stepsList) {
                        thisWeekData = stepsList;
                        thisWeekChart.addData(thisWeekData, mActiveStepsGoal, 7);
                        thisWeekChart.setMarkerView(marker);
                        thisWeekChart.animateY(3000);
                    }
                });

        getModel().getSteps(getModel().getUser().getUserID(), userSelectDate, WeekData.LASTWEEK,
                new OnStepsGetListener() {
                    @Override
                    public void onStepsGet(List<Steps> stepsList) {
                        lastWeekData = stepsList;
                        lastWeekChart.addData(lastWeekData, mActiveStepsGoal, 7);
                        lastWeekChart.setMarkerView(marker);
                        lastWeekChart.animateY(3000);
                    }
                });

        getModel().getSteps(getModel().getUser().getUserID(), userSelectDate, WeekData.LASTMONTH,
                new OnStepsGetListener() {
                    @Override
                    public void onStepsGet(List<Steps> stepsList) {
                        lastMonthData = stepsList;
                        lastMonthChart.addData(lastMonthData, mActiveStepsGoal, 30);
                        lastMonthChart.setMarkerView(marker);
                        lastMonthChart.animateY(3000);
                    }
                });
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
        int userWeight = getModel().getUser().getWeight();
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

    public interface OnStepsGetListener {
        void onStepsGet(List<Steps> stepsList);
    }

    @Subscribe
    public void onEvent(ChangeGoalEvent event) {
        if (event.isChangeGoal()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initData(mUserSelectDate);
                }
            });
        }
    }
}
