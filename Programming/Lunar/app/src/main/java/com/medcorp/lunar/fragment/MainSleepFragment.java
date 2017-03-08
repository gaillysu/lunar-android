package com.medcorp.lunar.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.event.DateSelectChangedEvent;
import com.medcorp.lunar.fragment.base.BaseFragment;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.SleepData;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.Common;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.SleepDataHandler;
import com.medcorp.lunar.util.SleepDataUtils;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.graphs.SleepTodayChart;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/26.
 *
 */
public class MainSleepFragment extends BaseFragment {


    @Bind(R.id.lunar_sleep_fragment_duration)
    TextView durationTextView;
    @Bind(R.id.lunar_sleep_fragment_quality)
    TextView qualityTextView;
    @Bind(R.id.lunar_sleep_fragment_sleep_time)
    TextView sleepTimeTextView;
    @Bind(R.id.lunar_hjbkarl)
    TextView wakeTimeTextView;

    @Bind(R.id.fragment_sleep_history_linechart)
    SleepTodayChart lineChartSleep;

    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.lunar_main_sleep_fragment_layout, container, false);
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
        initData(userSelectDate);
        return sleepView;
    }

    public void initData(Date date) {
        User user = getModel().getNevoUser();
        Sleep[] sleepArray = getModel().getDailySleep(user.getNevoUserID(), date);
        SleepDataHandler handler = new SleepDataHandler(Arrays.asList(sleepArray));
        List<SleepData> sleepDataList = handler.getSleepData(date);

        if (!sleepDataList.isEmpty()) {
            SleepData sleepData;
            if (sleepDataList.size() == 2) {
                sleepData = SleepDataUtils.mergeYesterdayToday(sleepDataList.get(1), sleepDataList.get(0));
                DateTime sleepStart = new DateTime(sleepData.getSleepStart()==0? Common.removeTimeFromDate(date).getTime():sleepData.getSleepStart());
                Log.w("Karl", "Yo yo : " + sleepData.getTotalSleep());

                sleepTimeTextView.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                durationTextView.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
            } else {
                sleepData = sleepDataList.get(0);
                DateTime sleepStart = new DateTime(sleepData.getSleepStart()==0?Common.removeTimeFromDate(date).getTime():sleepData.getSleepStart());
                sleepTimeTextView.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                durationTextView.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
            }
            qualityTextView.setText(sleepData.getDeepSleep() * 100 / (sleepData.getTotalSleep() == 0 ? 1 : sleepData.getTotalSleep()) + "%");
            lineChartSleep.setDataInChart(sleepData);
            lineChartSleep.animateY(3000);
            DateTime sleepEnd = new DateTime(sleepData.getSleepEnd()==0?Common.removeTimeFromDate(date).getTime():sleepData.getSleepEnd());
            wakeTimeTextView.setText(sleepEnd.toString("HH:mm", Locale.ENGLISH));
        }
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


}