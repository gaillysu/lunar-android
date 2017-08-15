package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.SleepData;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.Common;
import com.medcorp.lunar.util.SleepDataHandler;
import com.medcorp.lunar.util.SleepDataUtils;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisSleepLineChart;
import com.medcorp.lunar.view.graphs.SleepTodayChart;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/***
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
    @Bind(R.id.ui_page_control_point)
    LinearLayout uiControl;

    private List<View> sleepList;
    private View thisWeekView;
    private View todaySleepViewChart;
    private View lastMonthView;
    private AnalysisSleepLineChart thisWeekChart, lastMonthChart;
    private SleepTodayChart todaySleepChart;
    private TipsView mMv;
    private SleepGoal mActiveSleepGoal;
    private AnalysisStepsChartAdapter mAnalysisAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.analysis_fragment_child_sleep_fragment, container, false);
        ButterKnife.bind(this, sleepView);
        initView(inflater);
        return sleepView;
    }

    private void initView(LayoutInflater inflater) {
        sleepList = new ArrayList<>(3);
        mMv = new TipsView(AnalysisSleepFragment.this.getContext(), R.layout.custom_marker_view);

        todaySleepViewChart = inflater.inflate(R.layout.analysis_sleep_today_chart, null);
        thisWeekView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);

        todaySleepChart = (SleepTodayChart) todaySleepViewChart.findViewById(R.id.analysis_sleep_today_chart);
        thisWeekChart = (AnalysisSleepLineChart) thisWeekView.findViewById(R.id.analysis_sleep_chart);
        lastMonthChart = (AnalysisSleepLineChart) lastMonthView.findViewById(R.id.analysis_sleep_chart);

        sleepList.add(todaySleepViewChart);
        sleepList.add(thisWeekView);
        sleepList.add(lastMonthView);


        mAnalysisAdapter = new AnalysisStepsChartAdapter(sleepList);
        sleepViewPage.setAdapter(mAnalysisAdapter);
        setChangeListener();
        addUIControl(sleepList);
        initData();
    }

    private void addUIControl(List<View> sleepList) {
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
    }

    private void setChangeListener() {
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
                setData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {

        getModel().getSleepGoalDatabseHelper().getAll().subscribe(new Consumer<List<SleepGoal>>() {
            @Override
            public void accept(List<SleepGoal> sleepGoals) throws Exception {
                if (sleepGoals.size() > 0) {
                    for (SleepGoal sleepGoal : sleepGoals) {
                        if (sleepGoal.isStatus()) {
                            mActiveSleepGoal = sleepGoal;
                            break;
                        }
                    }
                }
            }
        });

        if (mActiveSleepGoal == null) {
            mActiveSleepGoal = new SleepGoal("Unknown", 360, true);
        }
        setData(sleepViewPage.getCurrentItem());
        /**
         * 'Sleep' is not the right way to put it into the chart because one evening and one night is spread
         * through 2 'Sleep' Objects.Therefor we have a solution which is 'SleepData' We have therefor
         * 'SleepDataHandler' to parse a List<Sleep> to List<SleepData>. Although, this needs to be tested.
         * getDummyData is just for the 'dummy data'
         */
    }

    private void setThisWeekData(List<SleepData> thisWeekSleepData) {
        String title = getString(R.string.analysis_fragment_this_week_chart_title);
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

    public void setLastMonthData(List<SleepData> lastMonthSleepData) {
        String lastMonthTitle = getString(R.string.analysis_fragment_last_month_chart_title);
        if (lastMonthSleepData.size() != 0) {
            setAverageText(getTotalSleep(lastMonthSleepData), getTotalSleep(lastMonthSleepData) / lastMonthSleepData.size()
                    , getAverageWake(lastMonthSleepData) / lastMonthSleepData.size(),
                    getTotalDeepSleep(lastMonthSleepData) * 100 / (getTotalSleep(lastMonthSleepData) == 0 ? 1
                            : getTotalSleep(lastMonthSleepData)), lastMonthTitle);
        } else {
            setAverageText(0, 0, 0, 0, lastMonthTitle);
        }
    }

    public void setData(final int position) {
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                switch (position) {
                    case 0:
                        sleepTextView.setText(getString(R.string.analysis_fragment_today_chart_title));
                        getModel().getDailySleep(user.getUserID(), new Date(), new TodaySleepListener() {
                            @Override
                            public void todaySleep(Sleep[] sleeps) {
                                Log.e("jason", "yesterday Sleep : " + sleeps[0].toString());
                                SleepDataHandler handler = new SleepDataHandler(Arrays.asList(sleeps));
                                List<SleepData> sleepDataList = handler.getSleepData(new Date());
                                if (!sleepDataList.isEmpty()) {
                                    SleepData sleepData = null;
                                    if (sleepDataList.size() == 2) {
                                        sleepData = SleepDataUtils.mergeYesterdayToday(sleepDataList.get(1), sleepDataList.get(0));
                                        DateTime sleepStart = new DateTime(sleepData.getSleepStart() == 0 ?
                                                Common.removeTimeFromDate(new Date()).getTime() : sleepData.getSleepStart());
                                        Log.w("Karl", "Yo yo : " + sleepData.getTotalSleep());

                                        averageSleepText.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                                        totalSleepText.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
                                    } else {
                                        sleepData = sleepDataList.get(0);
                                        DateTime sleepStart = new DateTime(sleepData.getSleepStart() == 0 ?
                                                Common.removeTimeFromDate(new Date()).getTime() : sleepData.getSleepStart());

                                        averageSleepText.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                                        totalSleepText.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
                                    }
                                    sleepQualityTv.setText(sleepData.getDeepSleep() * 100 / (sleepData.getTotalSleep() == 0
                                            ? 1 : sleepData.getTotalSleep()) + "%");
                                    todaySleepChart.setDataInChart(sleepData);
                                    todaySleepChart.animateY(3000);
                                    DateTime sleepEnd = new DateTime(sleepData.getSleepEnd() == 0 ?
                                            Common.removeTimeFromDate(new Date()).getTime() : sleepData.getSleepEnd());
                                    averageWake.setText(sleepEnd.toString("HH:mm", Locale.ENGLISH));
                                } else {
                                    setAverageText(0, 0, 0, 0, getString(R.string.analysis_fragment_today_chart_title));
                                }
                            }
                        });
                        break;
                    case 1:
                        getModel().getSleep(user.getUserID(), new Date(), WeekData.TISHWEEK,
                                new ObtainSleepDataListener() {
                                    @Override
                                    public void obtainSleepData(List<SleepData> thisWeekSleepData) {
                                        thisWeekChart.addData(thisWeekSleepData, mActiveSleepGoal, 7);
                                        thisWeekChart.setMarkerView(mMv);
                                        thisWeekChart.animateY(3000);
                                        setThisWeekData(thisWeekSleepData);
                                    }
                                });
                        break;
                    case 2:
                        getModel().getSleep(user.getUserID(), new Date(), WeekData.LASTMONTH
                                , new ObtainSleepDataListener() {
                                    @Override
                                    public void obtainSleepData(List<SleepData> lastMonthSleepData) {
                                        lastMonthChart.addData(lastMonthSleepData, mActiveSleepGoal, 30);
                                        lastMonthChart.setMarkerView(mMv);
                                        lastMonthChart.animateY(3000);
                                        setLastMonthData(lastMonthSleepData);
                                    }
                                });
                        break;
                }
            }
        });
    }

    public interface ObtainSleepDataListener {
        void obtainSleepData(List<SleepData> sleepDatas);
    }

    public interface TodaySleepListener {
        void todaySleep(Sleep[] sleeps);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
                    initData();
                    mAnalysisAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
