package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.AnalysisStepsChartAdapter;
import com.medcorp.lunar.event.bluetooth.OnSyncEvent;
import com.medcorp.lunar.fragment.base.BaseFragment;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisStepsLineChart;
import com.medcorp.lunar.view.graphs.DailyStepsBarChart;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/***
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

    private View todayWeek, thisWeekView, lastMonthView;
    private DailyStepsBarChart todayStepsChart;
    private AnalysisStepsLineChart thisWeekChart, lastMonthChart;
    private StepsGoal mActiveStepsGoal;
    private List<Steps> thisWeekData = new ArrayList<>();
    private List<Steps> lastWeekData = new ArrayList<>();
    private List<Steps> lastMonthData = new ArrayList<>();
    private TipsView marker;
    private int userWeight;
    private AnalysisStepsChartAdapter mAnalysisAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View stepsView = inflater.inflate(R.layout.analysis_fragment_child_steps_fragment, container, false);
        ButterKnife.bind(this, stepsView);

        initView(inflater);
        return stepsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        /**
         * Added max in 'addData', max is the time spam in days, in 'this week' and
         * 'last week' this is 7 because 7 days is equal to a week.
         * In this month this is 30 (or 31) because there are 30 days in a month.
         */
        setDesText(chartViewPager.getCurrentItem());
        setDataInChart(chartViewPager.getCurrentItem());
    }

    private void initView(LayoutInflater inflater) {
        final List<View> stepsDataList = new ArrayList<>(3);
        todayWeek = inflater.inflate(R.layout.analysis_steps_chart_fragment_layout, null);
        thisWeekView = inflater.inflate(R.layout.analysis_steps_this_week_chart, null);
        lastMonthView = inflater.inflate(R.layout.analysis_steps_this_week_chart, null);
        marker = new TipsView(AnalysisStepsFragment.this.getContext(), R.layout.custom_marker_view);
        todayStepsChart = (DailyStepsBarChart) todayWeek.findViewById(R.id.analysis_step_chart);
        thisWeekChart = (AnalysisStepsLineChart) thisWeekView.findViewById(R.id.analysis_step_this_week_chart_lc);
        lastMonthChart = (AnalysisStepsLineChart) lastMonthView.findViewById(R.id.analysis_step_this_week_chart_lc);
        stepsDataList.add(todayWeek);
        stepsDataList.add(thisWeekView);
        stepsDataList.add(lastMonthView);
        getModel().getAllGoal(new MainClockFragment.ObtainGoalListener() {
            @Override
            public void obtainGoal(List<StepsGoal> list) {
                if (list != null) {
                    for (StepsGoal stepsGoal : list) {
                        if (stepsGoal.isStatus()) {
                            mAnalysisAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        });
        addUIControl(stepsDataList);


        if (mActiveStepsGoal == null) {
            mActiveStepsGoal = new StepsGoal("Unknown", true, 10000);
        }
        mAnalysisAdapter = new AnalysisStepsChartAdapter(stepsDataList);
        chartViewPager.setAdapter(mAnalysisAdapter);
        setPageChangeListener();

    }

    private void addUIControl(List<View> stepsDataList) {

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

    }

    private void setPageChangeListener() {
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
                setDataInChart(position);
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
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                userWeight = user.getWeight();
            }
        });
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
                            , getResources().getString(R.string.analysis_fragment_today_chart_title));
                } else {
                    setAverageText(0, 0, 0 + "", 0, getResources().getString(R.string.analysis_fragment_today_chart_title));
                }
                break;
            case 1:
                if (lastWeekData.size() != 0) {
                    setAverageText(getWeekSteps(lastWeekData), getWeekSteps(lastWeekData) / 7
                            , getWeekCalories(lastWeekData, 7)
                            , getAvgDurationTime(lastWeekData) / 7
                            , getResources().getString(R.string.analysis_fragment_today_chart_title));
                } else {
                    setAverageText(0, 0, 0 + "", 0, getResources().getString(R.string.analysis_fragment_this_week_chart_title));
                }
                break;
            case 2:
                if (lastMonthData.size() != 0) {
                    setAverageText(getWeekSteps(lastMonthData), getWeekSteps(lastMonthData) / 7
                            , getWeekCalories(lastMonthData, 30)
                            , getAvgDurationTime(lastMonthData) / 30 / 1000
                            , getResources().getString(R.string.analysis_fragment_last_month_chart_title));
                } else {
                    setAverageText(0, 0, 0 + "", 0, getResources().getString(R.string.analysis_fragment_last_month_chart_title));
                }
                break;
        }
    }

    public void setDataInChart(final int dataInChart) {
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                switch (dataInChart) {
                    case 0:
                        Steps dailySteps = getModel().getDailySteps(user.getUserID(), new Date());
                        String[] hourlySteps = dailySteps.getHourlySteps().replace("[", "").replace("]", "").replace(" ", "").split(",");
                        int[] dailyStepsArray = new int[hourlySteps.length];
                        for (int i = 0; i < 24; i++) {
                            dailyStepsArray[i] = new Integer(hourlySteps[i]).intValue();
                        }
                        todayStepsChart.setDataInChart(dailyStepsArray, dailySteps.getGoal());
                        todayStepsChart.animateY(3000);

                        break;
                    case 1:
                        getModel().getSteps(user.getUserID(), new Date()
                                , WeekData.TISHWEEK, new OnStepsGetListener() {
                                    @Override
                                    public void onStepsGet(List<Steps> stepsList) {
                                        thisWeekData = stepsList;
                                        thisWeekChart.addData(thisWeekData, mActiveStepsGoal, 7);
                                        thisWeekChart.setMarkerView(marker);
                                        thisWeekChart.animateY(3000);
                                    }
                                });
                        break;
                    case 2:
                        getModel().getSteps(user.getUserID(), new Date(), WeekData.LASTMONTH,
                                new OnStepsGetListener() {
                                    @Override
                                    public void onStepsGet(List<Steps> stepsList) {
                                        lastMonthData = stepsList;
                                        lastMonthChart.addData(lastMonthData, mActiveStepsGoal, 30);
                                        lastMonthChart.setMarkerView(marker);
                                        lastMonthChart.animateY(3000);
                                    }
                                });
                        break;
                }
            }
        });
    }

    public interface OnStepsGetListener {
        void onStepsGet(List<Steps> stepsList);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED | event.getStatus()
                == OnSyncEvent.SYNC_EVENT.TODAY_SYNC_STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    setDesText(chartViewPager.getCurrentItem());
                    setDataInChart(chartViewPager.getCurrentItem());
                }
            });
        }
    }
}
