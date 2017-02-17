package com.medcorp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.medcorp.BuildConfig;
import com.medcorp.R;
import com.medcorp.event.DateSelectChangedEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.event.bluetooth.SolarConvertEvent;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Solar;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;
import com.medcorp.util.TimeUtil;

import net.medcorp.library.ble.util.Optional;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/8/12.
 */
public class MainSolarFragment extends BaseFragment {

    @Bind(R.id.today_solar_battery_time_tv)
    TextView batteryTimeTv;
    @Bind(R.id.today_solar_solar_time_tv)
    TextView solarTimeTv;
    @Bind(R.id.main_fragment_solar_pie_chart)
    PieChart solarPieChart;
    @Bind(R.id.main_fragment_solar_title_tv)
    TextView solarTitle;
    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_solar_fragment_layout, container, false);
        String selectDate = Preferences.getSelectDate(this.getContext());
        if (selectDate == null) {
            userSelectDate = new java.util.Date();
        } else {
            try {
                userSelectDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ButterKnife.bind(this, view);
        initData(userSelectDate);
        return view;
    }

    private void initData(Date userSelectDate) {
        float powerOnBatteryPercent = 100f;
        float powerOnSolarPercent = 0f;
        int powerOnSolarMinutes = 0;
        int powOnBatteryMinutes = 24 * 60; //default battery time,unit in "minutes"
        if (Common.removeTimeFromDate(new Date()).getTime() == Common.removeTimeFromDate(userSelectDate).getTime()) {
            powOnBatteryMinutes = new DateTime().getMinuteOfDay();
        }
        Optional<Solar> solarOptional = getModel().getSolarDatabaseHelper().get(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        if (solarOptional.notEmpty()) {
            powerOnSolarPercent = solarOptional.get().getTotalHarvestingTime() * 100f / (powOnBatteryMinutes);
            powerOnBatteryPercent = 100f - powerOnSolarPercent;
            powerOnSolarMinutes = solarOptional.get().getTotalHarvestingTime();
            powOnBatteryMinutes = powOnBatteryMinutes - powerOnSolarMinutes;
        }
        float[] solarPieChartDate = {powerOnSolarPercent, powerOnBatteryPercent};
        setPieChartData(solarPieChartDate);
        batteryTimeTv.setText(TimeUtil.formatTime(powOnBatteryMinutes));
        solarTimeTv.setText(TimeUtil.formatTime(powerOnSolarMinutes));
    }

    private void setPieChartData(float[] solarPieChartDate) {
        String[] describe = getContext().getResources().getStringArray(R.array.solar_describe_battery_text);
        ArrayList<String> des = new ArrayList<>();
        solarPieChart.setUsePercentValues(true);
        solarPieChart.setDescription("");
        solarPieChart.setDrawHoleEnabled(false);
        solarPieChart.setDrawCenterText(false);

        List<PieEntry> yValue = new ArrayList<>();
        for (int i = 0; i < solarPieChartDate.length; i++) {
            des.add(i, describe[i]);
            yValue.add(new PieEntry(solarPieChartDate[i], describe[i]));
        }

        PieDataSet pieDataSet = new PieDataSet(yValue, "");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(126, 216, 209));
        colors.add(Color.rgb(179, 126, 189));
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(1f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueFormatter(new PercentFormatter());

        pieData.setDataSet(pieDataSet);
        pieData.setValueTextColor(Color.rgb(255, 255, 255));
        pieData.setValueTextSize(25f);

        solarPieChart.setData(pieData);
        solarPieChart.invalidate();
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
    public void onEvent(final DateSelectChangedEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                userSelectDate = event.getDate();
                initData(userSelectDate);
            }
        });
    }

    @Subscribe
    public void onEvent(final OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED || event.getStatus() == OnSyncEvent.SYNC_EVENT.TODAY_SYNC_STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    initData(userSelectDate);
                }
            });
        }
    }

    @Subscribe
    public void onEvent(final SolarConvertEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    solarTitle.setText("Solar adc:" + event.getPv_adc());
                }
            }
        });
    }
}
