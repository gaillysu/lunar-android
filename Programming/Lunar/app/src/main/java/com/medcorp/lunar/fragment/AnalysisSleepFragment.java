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
import com.medcorp.lunar.model.SleepData;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisSleepLineChart;

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
public class AnalysisSleepFragment extends BaseFragment {

    @Bind(R.id.steps_fragment_average_steps_tv)
    TextView averageSleepText;
    @Bind(R.id.steps_fragment_total_steps_tv)
    TextView totalSleepText;
    @Bind(R.id.steps_fragment_average_calories_tv)
    TextView averageWake;
    @Bind(R.id.steps_fragment_average_time_tv)
    TextView sleepQualityTv;

    @Bind(R.id.analysis_sleep_fragment_view_page)
    ViewPager sleepViewPage;

    @Bind(R.id.analysis_sleep_fragment_title_tv)
    TextView sleepTextView;

    @Bind(R.id.analysis_fragment_des_total_one)
    TextView totalSleepDes;
    @Bind(R.id.analysis_fragment_des_total_two)
    TextView avgSleepDes;
    @Bind(R.id.analysis_fragment_des_total_three)
    TextView avgWakeDes;
    @Bind(R.id.analysis_fragment_des_total_four)
    TextView sleepQuality;
    @Bind(R.id.ui_page_control_point)
    LinearLayout uiControl;

    private List<View> sleepList;
    private Date userSelectDate;
    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;
    private AnalysisSleepLineChart thisWeekChart, lastWeekChart, lastMonthChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.analysis_fragment_child_sleep_fragment, container, false);
        ButterKnife.bind(this, sleepView);
        String selectDate = Preferences.getSelectDate(this.getContext());
        if (selectDate == null) {
            userSelectDate = new Date();
        } else {
            try {
                userSelectDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        initView(inflater);
        return sleepView;
    }

    private void initView(LayoutInflater inflater) {
        totalSleepDes.setText(getString(R.string.analysis_fragment_des_total_sleep));
        avgSleepDes.setText(getString(R.string.analysis_fragment_des_avg_sleep));
        avgWakeDes.setText(getString(R.string.analysis_fragment_des_avg_wake));
        sleepQuality.setText(getString(R.string.analysis_fragment_des_sleep_quality));

        sleepList = new ArrayList<>(3);

        thisWeekView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);
        lastWeekView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);
        thisWeekChart = (AnalysisSleepLineChart) thisWeekView.findViewById(R.id.analysis_sleep_chart);
        lastWeekChart = (AnalysisSleepLineChart) lastWeekView.findViewById(R.id.analysis_sleep_chart);
        lastMonthChart = (AnalysisSleepLineChart) lastMonthView.findViewById(R.id.analysis_sleep_chart);

        sleepList.add(thisWeekView);
        sleepList.add(lastWeekView);
        sleepList.add(lastMonthView);

        for (int i = 0; i < sleepList.size(); i++) {
            ImageView imageView = new ImageView(AnalysisSleepFragment.this.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                imageView.setImageResource(R.drawable.ui_page_control_selector);
            } else {
                imageView.setImageResource(R.drawable.ui_page_control_unselector);
                layoutParams.leftMargin = 20;
            }
            uiControl.addView(imageView, layoutParams);
        }
        initData(userSelectDate);
    }

    private void initData(Date userSelectDate) {
        final TipsView mv = new TipsView(AnalysisSleepFragment.this.getContext(), R.layout.custom_marker_view);
        getModel().getSleep(getModel().getUser().getNevoUserID(), userSelectDate, WeekData.TISHWEEK,
                new ObtainSleepDataListener() {
                    @Override
                    public void obtainSleepData(List<SleepData> thisWeekSleepData) {
                        thisWeekChart.addData(thisWeekSleepData, 7);
                        thisWeekChart.setMarkerView(mv);
                        thisWeekChart.animateY(3000);
                        setThisWeekData(thisWeekSleepData);
                    }
                });
        getModel().getSleep(getModel().getUser().getNevoUserID(), userSelectDate, WeekData.LASTWEEK,
                new ObtainSleepDataListener() {
                    @Override
                    public void obtainSleepData(List<SleepData> lastWeekSleepData) {
                        lastWeekChart.addData(lastWeekSleepData, 7);
                        lastWeekChart.setMarkerView(mv);
                        lastWeekChart.animateY(3000);
                        setLastWeekData(lastWeekSleepData);
                    }
                });

        getModel().getSleep(getModel().getUser().getNevoUserID(), userSelectDate, WeekData.LASTMONTH
                , new ObtainSleepDataListener() {
                    @Override
                    public void obtainSleepData(List<SleepData> lastMonthSleepData) {
                        lastMonthChart.addData(lastMonthSleepData, 30);
                        lastMonthChart.setMarkerView(mv);
                        lastMonthChart.animateY(3000);
                        setLastMonthData(lastMonthSleepData);
                    }
                });
        /**
         * 'Sleep' is not the right way to put it into the chart because one evening and one night is spread
         * through 2 'Sleep' Objects.Therefor we have a solution which is 'SleepData' We have therefor
         * 'SleepDataHandler' to parse a List<Sleep> to List<SleepData>. Although, this needs to be tested.
         * getDummyData is just for the 'dummy data'
         */


        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(sleepList);
        sleepViewPage.setAdapter(adapter);
        sleepViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int childCount = uiControl.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ImageView imageView = (ImageView) uiControl.getChildAt(i);
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

    private void setThisWeekData(List<SleepData> thisWeekSleepData) {
        String title = getString(R.string.analysis_fragment_this_week_steps);
        int TotalSleep = getTotalSleep(thisWeekSleepData);
        int avgSleep = getTotalSleep(thisWeekSleepData) / thisWeekSleepData.size();
        int avgWake = getAverageWake(thisWeekSleepData) / thisWeekSleepData.size();
        int averageQuality = getTotalDeepSleep(thisWeekSleepData) * 100 / (getTotalSleep(thisWeekSleepData) == 0 ? 1
                : getTotalSleep(thisWeekSleepData));
        if (thisWeekSleepData.size() != 0) {
            setAverageText(TotalSleep, avgSleep, avgWake, averageQuality, title);
        } else {
            setAverageText(0, 0, 0, 0, title);
        }

    }

    private void setAverageText(int totalSleep, int averageSleep, int averageWakeValue, int averageQuality, String title) {

        totalSleepText.setText(TimeUtil.formatTime(totalSleep));
        averageSleepText.setText(TimeUtil.formatTime(averageSleep));
        averageWake.setText(TimeUtil.formatTime(averageWakeValue));
        sleepQualityTv.setText(averageQuality + "%");
        sleepTextView.setText(title);
    }

    public int getTotalSleep(List<SleepData> list) {
        int sumSleep = 0;
        for (SleepData sleepData : list) {
            sumSleep += sleepData.getTotalSleep();
        }
        return sumSleep;
    }

    public int getAverageWake(List<SleepData> list) {
        int averageWake = 0;
        for (SleepData sleepData : list) {
            //sleepData.getTotalSleep()-sleepData.getDeepSleep()-sleepData.getLightSleep();
            averageWake += sleepData.getAwake();
        }
        return averageWake;
    }

    public int getTotalDeepSleep(List<SleepData> list) {
        int totalSleep = 0;
        for (SleepData sleep : list) {
            totalSleep += sleep.getDeepSleep();
        }
        return totalSleep;
    }

    public void setLastWeekData(List<SleepData> lastWeekSleepData) {
        String lastTitle = getString(R.string.analysis_fragment_last_week_steps);
        if (lastWeekSleepData.size() != 0) {
            setAverageText(getTotalSleep(lastWeekSleepData), getTotalSleep(lastWeekSleepData) / lastWeekSleepData.size()
                    , getAverageWake(lastWeekSleepData) / lastWeekSleepData.size(),
                    getTotalDeepSleep(lastWeekSleepData) * 100 / (getTotalSleep(lastWeekSleepData) == 0 ? 1
                            : getTotalSleep(lastWeekSleepData)), lastTitle);
        } else {
            setAverageText(0, 0, 0, 0, lastTitle);
        }
    }

    public void setLastMonthData(List<SleepData> lastMonthSleepData) {
        String lastMonthTitle = getString(R.string.analysis_fragment_last_month_solar);
        if (lastMonthSleepData.size() != 0) {
            setAverageText(getTotalSleep(lastMonthSleepData), getTotalSleep(lastMonthSleepData) / lastMonthSleepData.size()
                    , getAverageWake(lastMonthSleepData) / lastMonthSleepData.size(),
                    getTotalDeepSleep(lastMonthSleepData) * 100 / (getTotalSleep(lastMonthSleepData) == 0 ? 1
                            : getTotalSleep(lastMonthSleepData)), lastMonthTitle);
        } else {
            setAverageText(0, 0, 0, 0, lastMonthTitle);
        }
    }

    public interface ObtainSleepDataListener {
        void obtainSleepData(List<SleepData> sleepDatas);
    }
}
