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
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.TipsView;
import com.medcorp.lunar.view.graphs.AnalysisSolarLineChart;

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

    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;
    private List<View> solarList;
    private Date userSelectDate;
    private AnalysisSolarLineChart thisWeekChart,lastWeekChart,lastMonthChart;

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
        thisWeekView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout, null);
        lastWeekView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout, null);
        solarList.add(thisWeekView);
        solarList.add(lastWeekView);
        solarList.add(lastMonthView);

        for(int i = 0; i<solarList.size(); i++){
            ImageView imageView  = new  ImageView(AnalysisSolarFragment.this.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            if(i == 0){
                imageView.setImageResource(R.drawable.ui_page_control_selector);
            }else{
                imageView.setImageResource(R.drawable.ui_page_control_unselector);
                params.leftMargin = 20;
            }
            uiControl.addView(imageView,params);
        }

        initData(userSelectDate);
    }

    private void initData(Date userSelectDate) {

        thisWeekChart = (AnalysisSolarLineChart) thisWeekView.findViewById(R.id.analysis_solar_chart);
        lastWeekChart = (AnalysisSolarLineChart) lastWeekView.findViewById(R.id.analysis_solar_chart);
        lastMonthChart = (AnalysisSolarLineChart) lastMonthView.findViewById(R.id.analysis_solar_chart);
        final TipsView marker = new TipsView(AnalysisSolarFragment.this.getContext(),R.layout.custom_marker_view);

        getModel().getSolarData(getModel().getUser().getId(), userSelectDate,WeekData.TISHWEEK,
                new ObtainSolarListener() {
                    @Override
                    public void obtainSolarData(List<Solar> thisWeek) {
                        thisWeekChart.addData(thisWeek, 7);
                        thisWeekChart.setMarkerView(marker);
                        solarTitleTextView.setText(R.string.analysis_fragment_this_week_steps);
                        averageTimeOnSolar.setText(TimeUtil.formatTime(getAverageTimeOnBattery(thisWeek)));
                        averageTimeOnBattery.setText(TimeUtil.formatTime(24*60-getAverageTimeOnBattery(thisWeek)));
                    }
                });
        getModel().getSolarData(getModel().getUser().getId(), userSelectDate, WeekData.LASTWEEK,
                new ObtainSolarListener() {
            @Override
            public void obtainSolarData(List<Solar> lastWeek) {
                lastWeekChart.addData(lastWeek, 7);
                lastWeekChart.setMarkerView(marker);
                solarTitleTextView.setText(R.string.analysis_fragment_last_week_steps);
                averageTimeOnSolar.setText(TimeUtil.formatTime(getAverageTimeOnBattery(lastWeek)));
                averageTimeOnBattery.setText(TimeUtil.formatTime(24*60-getAverageTimeOnBattery(lastWeek)));
            }
        });
        getModel().getSolarData(getModel().getUser().getId(), userSelectDate, WeekData.LASTMONTH,
                new ObtainSolarListener() {
                    @Override
                    public void obtainSolarData(List<Solar> lastMonth) {
                        lastMonthChart.addData(lastMonth, 30);
                        lastMonthChart.setMarkerView(marker);
                        solarTitleTextView.setText(R.string.analysis_fragment_last_month_solar);
                        averageTimeOnSolar.setText(TimeUtil.formatTime(getAverageTimeOnBattery(lastMonth)));
                        averageTimeOnBattery.setText(TimeUtil.formatTime(24*60-getAverageTimeOnBattery(lastMonth)));
                    }
                });

        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(solarList);
        solarViewPager.setAdapter(adapter);
        solarViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                int childCount = uiControl.getChildCount();
                for(int i = 0; i<childCount; i++){
                    ImageView imageView = (ImageView) uiControl.getChildAt(i);
                    if(position == i){
                        imageView.setImageResource(R.drawable.ui_page_control_selector);
                    }else{
                        imageView.setImageResource(R.drawable.ui_page_control_unselector);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public int getAverageTimeOnBattery(List<Solar> list){
        int sum = 0;
        for(Solar solar : list){
            sum = solar.getTotalHarvestingTime();
        }
        return sum == 0?0:sum/list.size();
    }

    public interface ObtainSolarListener{
        void obtainSolarData(List<Solar> solars);
    }
}
