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
import com.medcorp.lunar.model.ChangeSolarGoalEvent;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisSolarLineChart;
import com.medcorp.lunar.view.graphs.DailyStepsBarChart;

import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private Date userSelectDate;
    private AnalysisSolarLineChart thisWeekChart, lastMonthChart;
    private DailyStepsBarChart todayChart;
    private TipsView mMarker;
    private SolarGoal solarGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View solarView = inflater.inflate(R.layout.analysis_fragment_child_solar_fragment, container, false);
        ButterKnife.bind(this, solarView);

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
        return solarView;
    }


    private void initView(LayoutInflater inflater) {
        solarList = new ArrayList<>(3);
        todayChartView = inflater.inflate(R.layout.analysis_solar_today_chart_fragment_layout, null);
        thisWeekView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout, null);
        solarList.add(todayChartView);
        solarList.add(thisWeekView);
        solarList.add(lastMonthView);

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
        initData();
    }

    private void initData() {

        todayChart = (DailyStepsBarChart) todayChartView.findViewById(R.id.analysis_solar_today_chart);
        thisWeekChart = (AnalysisSolarLineChart) thisWeekView.findViewById(R.id.analysis_solar_chart);
        lastMonthChart = (AnalysisSolarLineChart) lastMonthView.findViewById(R.id.analysis_solar_chart);
        mMarker = new TipsView(AnalysisSolarFragment.this.getContext(), R.layout.custom_marker_view);
        getModel().getSolarGoalDatabaseHelper().getAll().subscribe(new Consumer<List<SolarGoal>>() {
            @Override
            public void accept(List<SolarGoal> solarGoals) throws Exception {
                if(solarGoals.size()>0){
                    for(SolarGoal goal:solarGoals){
                        if(goal.isStatus()){
                            solarGoal = goal;
                        }
                    }
                }
            }
        });

        if(solarGoal==null){
            solarGoal = new SolarGoal("Unknown",60,true);
        }

        setData(solarViewPager.getCurrentItem());
        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(solarList);
        solarViewPager.setAdapter(adapter);
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
                //TODO set data for chart
                break;
            case 1:
                getModel().getSolarData(getModel().getUser().getId(), userSelectDate, WeekData.TISHWEEK,
                        new ObtainSolarListener() {
                            @Override
                            public void obtainSolarData(List<Solar> thisWeek) {
                                thisWeekChart.addData(thisWeek,solarGoal, 7);
                                thisWeekChart.setMarkerView(mMarker);
                                solarTitleTextView.setText(R.string.analysis_fragment_this_week_chart_title);
                                averageTimeOnSolar.setText(TimeUtil.formatTime(getAverageTimeOnBattery(thisWeek)));
                                averageTimeOnBattery.setText(TimeUtil.formatTime(24 * 60 - getAverageTimeOnBattery(thisWeek)));
                            }
                        });
                break;

            case 2:
                getModel().getSolarData(getModel().getUser().getId(), userSelectDate, WeekData.LASTMONTH,
                        new ObtainSolarListener() {
                            @Override
                            public void obtainSolarData(List<Solar> lastMonth) {
                                lastMonthChart.addData(lastMonth,solarGoal, 30);
                                lastMonthChart.setMarkerView(mMarker);
                                solarTitleTextView.setText(R.string.analysis_fragment_last_month_chart_title);
                                averageTimeOnSolar.setText(TimeUtil.formatTime(getAverageTimeOnBattery(lastMonth)));
                                averageTimeOnBattery.setText(TimeUtil.formatTime(24 * 60 - getAverageTimeOnBattery(lastMonth)));
                            }
                        });
                break;
        }
    }

    public interface ObtainSolarListener {
        void obtainSolarData(List<Solar> solars);
    }

    @Subscribe
    public void onEvent(ChangeSolarGoalEvent event) {
        if (event.isChange()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setData(solarViewPager.getCurrentItem());
                }
            });
        }
    }
}
