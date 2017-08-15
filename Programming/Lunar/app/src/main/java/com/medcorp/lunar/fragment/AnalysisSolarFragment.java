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
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisSolarLineChart;
import com.medcorp.lunar.view.graphs.DailySolarChart;

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
public class AnalysisSolarFragment extends BaseFragment {

    @Bind(R.id.analysis_solar_fragment_view_pager)
    ViewPager solarViewPager;
    @Bind(R.id.analysis_solar_fragment_title_tv)
    TextView solarTitleTextView;
    @Bind(R.id.today_solar_battery_time_tv)
    TextView averageTimeOnBattery;
    @Bind(R.id.today_solar_solar_time_tv)
    TextView averageTimeOnSolar;
    @Bind(R.id.ui_page_control_point)
    LinearLayout uiControl;

    private View todayChartView;
    private View thisWeekView;
    private View lastMonthView;
    private List<View> solarList;
    private AnalysisSolarLineChart thisWeekChart, lastMonthChart;
    private DailySolarChart todayChart;
    private TipsView mMarker;
    private SolarGoal solarGoal;
    private AnalysisStepsChartAdapter mAnalysisAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View solarView = inflater.inflate(R.layout.analysis_fragment_child_solar_fragment, container, false);
        ButterKnife.bind(this, solarView);
        initView(inflater);
        return solarView;
    }


    private void initView(LayoutInflater inflater) {
        solarList = new ArrayList<>(3);
        todayChartView = inflater.inflate(R.layout.analysis_solar_today_chart_fragment_layout, null);
        thisWeekView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout, null);
        todayChart = (DailySolarChart) todayChartView.findViewById(R.id.analysis_solar_today_chart);
        thisWeekChart = (AnalysisSolarLineChart) thisWeekView.findViewById(R.id.analysis_solar_chart);
        lastMonthChart = (AnalysisSolarLineChart) lastMonthView.findViewById(R.id.analysis_solar_chart);
        mMarker = new TipsView(AnalysisSolarFragment.this.getContext(), R.layout.custom_marker_view);
        solarList.add(todayChartView);
        solarList.add(thisWeekView);
        solarList.add(lastMonthView);
        mAnalysisAdapter = new AnalysisStepsChartAdapter(solarList);
        solarViewPager.setAdapter(mAnalysisAdapter);

        setChangeListener();
        addUIControl(solarList);
        initData();
    }

    public void addUIControl(List<View> solarList) {
        for (int i = 0; i < solarList.size(); i++) {
            ImageView imageView = new ImageView(AnalysisSolarFragment.this.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                imageView.setImageResource(R.drawable.ui_page_control_selector);
            } else {
                imageView.setImageResource(R.drawable.ui_page_control_unselector);
                params.leftMargin = 20;
            }
            uiControl.addView(imageView, params);
        }
    }

    private void setChangeListener() {
        solarViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
            @Override
            public void accept(List<SolarGoal> solarGoals) throws Exception {
                if (solarGoals.size() > 0) {
                    for (SolarGoal goal : solarGoals) {
                        if (goal.isStatus()) {
                            solarGoal = goal;
                        }
                    }
                }
            }
        });

        if (solarGoal == null) {
            solarGoal = new SolarGoal("Unknown", 60, true);
        }

        setData(solarViewPager.getCurrentItem());
    }

    public int getAverageTimeOnBattery(List<Solar> list) {
        int sum = 0;
        for (Solar solar : list) {
            sum = solar.getTotalHarvestingTime();
        }
        return sum == 0 ? 0 : sum / list.size();
    }

    public void setData(int position) {
        switch (position) {
            case 0:
                solarTitleTextView.setText(R.string.analysis_fragment_today_chart_title);
                getModel().getUser().subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        getModel().getSolarDatabaseHelper().get(user.getId(), new Date()).subscribe(new Consumer<Solar>() {
                            @Override
                            public void accept(Solar solar) throws Exception {
                                String[] hourlyHarvestingTime = solar.getHourlyHarvestingTime().replace("[", "").replace("]", "").replace(" ", "").split(",");
                                int[] dailySolar = new int[hourlyHarvestingTime.length];
                                for (int i = 0; i < 24; i++) {
                                    dailySolar[i] = new Integer(hourlyHarvestingTime[i]).intValue();
                                }
                                todayChart.setDataInChart(dailySolar, solar.getGoal());
                            }
                        });
                    }
                });

                break;
            case 1:
                getModel().getUser().subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        getModel().getSolarData(user.getId(), new Date(), WeekData.TISHWEEK,
                                new ObtainSolarListener() {
                                    @Override
                                    public void obtainSolarData(List<Solar> thisWeek) {
                                        thisWeekChart.addData(thisWeek, solarGoal, 7);
                                        thisWeekChart.setMarkerView(mMarker);
                                        solarTitleTextView.setText(R.string.analysis_fragment_this_week_chart_title);
                                        averageTimeOnSolar.setText(TimeUtil.formatTime(getAverageTimeOnBattery(thisWeek)));
                                        averageTimeOnBattery.setText(TimeUtil.formatTime(24 * 60 - getAverageTimeOnBattery(thisWeek)));
                                    }
                                });

                    }
                });
                break;

            case 2:
                getModel().getUser().subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        getModel().getSolarData(user.getId(), new Date(), WeekData.LASTMONTH,
                                new ObtainSolarListener() {
                                    @Override
                                    public void obtainSolarData(List<Solar> lastMonth) {
                                        lastMonthChart.addData(lastMonth, solarGoal, 30);
                                        lastMonthChart.setMarkerView(mMarker);
                                        solarTitleTextView.setText(R.string.analysis_fragment_last_month_chart_title);
                                        averageTimeOnSolar.setText(TimeUtil.formatTime(getAverageTimeOnBattery(lastMonth)));
                                        averageTimeOnBattery.setText(TimeUtil.formatTime(24 * 60 - getAverageTimeOnBattery(lastMonth)));
                                    }
                                });
                    }
                });
                break;
        }
    }

    public interface ObtainSolarListener {
        void obtainSolarData(List<Solar> solars);
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
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED |
                event.getStatus() == OnSyncEvent.SYNC_EVENT.TODAY_SYNC_STOPPED) {
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
