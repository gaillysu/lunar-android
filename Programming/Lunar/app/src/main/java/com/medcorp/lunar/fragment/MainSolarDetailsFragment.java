package com.medcorp.lunar.fragment;

import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.event.bluetooth.BatteryEvent;
import com.medcorp.lunar.event.bluetooth.GetWatchInfoChangedEvent;
import com.medcorp.lunar.event.bluetooth.PositionAddressChangeEvent;
import com.medcorp.lunar.event.bluetooth.SolarConvertEvent;
import com.medcorp.lunar.fragment.base.BaseFragment;
import com.medcorp.lunar.model.MyWatch;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.HorizontalProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/***
 * Created by Jason on 2017/6/22.
 */

public class MainSolarDetailsFragment extends BaseFragment {

    @Bind(R.id.show_solar_details_fragment_weather_describe_tv)
    TextView showWeatherCityTv;
    @Bind(R.id.show_solar_details_fragment_weather_tv)
    TextView showWeatherTv;
    @Bind(R.id.main_solar_details_status_tv)
    TextView showSolarStatusTv;
    @Bind(R.id.main_solar_details_battery_percentage_tv)
    TextView showBatteryLevelTv;
    @Bind(R.id.main_solar_details_solar_harvest_time_tv)
    TextView solarHarvestTimeTv;
    @Bind(R.id.main_solar_details_horizontalProgressBar)
    HorizontalProgressBar solarPercentageHPB;
    @Bind(R.id.main_solar_details_last_week_solar_total)
    TextView showLastWeekSolarTotalTv;
    @Bind(R.id.main_solar_details_last_month_solar_total)
    TextView showLastMonthSolarTotalTv;

    private MyWatch mMyWatch;
    private final int battery_level = 2;
    private final boolean available_version = false;//need check later
    private Address mPositionLocal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_solar_details_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getModel().getBatteryLevelOfWatch();
    }

    private void initData() {
        mPositionLocal = Preferences.getLocation(MainSolarDetailsFragment.this.getContext());
        String app_version = "";
        try {
            app_version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mMyWatch = new MyWatch(getModel().getWatchFirmware(), getModel().getWatchSoftware(), app_version, battery_level, available_version, null);
        String str_battery = this.getString(R.string.my_nevo_battery_low);
        if (mMyWatch.getBatteryLevel() == 2) {
            str_battery = this.getString(R.string.my_nevo_battery_full);
        } else if (mMyWatch.getBatteryLevel() == 1) {
            str_battery = this.getString(R.string.my_nevo_battery_half);
        }
        showBatteryLevelTv.setText(str_battery);
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                getModel().getSolarDatabaseHelper().get(user.getId(), new Date()).subscribe(new Consumer<Solar>() {
                    @Override
                    public void accept(Solar solar) throws Exception {
                        if (solar.getTotalHarvestingTime() != 0) {
                            solarHarvestTimeTv.setText(countTime(solar.getTotalHarvestingTime()));
                            float percentage = (float) solar.getTotalHarvestingTime() / (float) solar.getGoal();
                            solarPercentageHPB.setProgress(percentage * 100 >= 100f ? 100 : (int) (percentage * 100));
                        } else {
                            solarHarvestTimeTv.setText("0");
                            solarPercentageHPB.setProgress(0);
                        }
                    }
                });
            }
        });

        if (mPositionLocal != null) {
            showWeatherCityTv.setText(getString(R.string.todays_weather_text) + " " + mPositionLocal.getLocality());
        } else {
            showWeatherCityTv.setText(getString(R.string.todays_weather_text));
        }
        setLastWeekSolarTotal();
        setLastMonthSolarTotal();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }


    private void setLastWeekSolarTotal() {
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                getModel().getSolarData(user.getId(), new Date(), WeekData.LASTWEEK,
                        new AnalysisSolarFragment.ObtainSolarListener() {
                            @Override
                            public void obtainSolarData(List<Solar> lastWeek) {
                                int totalTime = 0;
                                for (Solar solar : lastWeek) {
                                    if (solar.getTotalHarvestingTime() != 0) {
                                        totalTime += solar.getTotalHarvestingTime();
                                    }
                                }
                                showLastWeekSolarTotalTv.setText(countTime(totalTime));
                            }
                        });
            }
        });

    }

    private void setLastMonthSolarTotal() {
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                getModel().getSolarData(user.getId(), new Date(), WeekData.LASTMONTH,
                        new AnalysisSolarFragment.ObtainSolarListener() {
                            @Override
                            public void obtainSolarData(List<Solar> lastWeek) {
                                int totalTime = 0;
                                for (Solar solar : lastWeek) {
                                    if (solar.getTotalHarvestingTime() != 0) {
                                        totalTime += solar.getTotalHarvestingTime();
                                    }
                                }
                                showLastMonthSolarTotalTv.setText(countTime(totalTime));
                            }
                        });
            }
        });
    }

    @Subscribe
    public void onEvent(final SolarConvertEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (event.getPv_adc() >= 170) {
                    showSolarStatusTv.setText(R.string.lunar_home_clock_solar_harvest_charge);
                } else {
                    showSolarStatusTv.setText(R.string.lunar_home_clock_solar_harvest_idle);
                }
            }
        });
    }

    @Subscribe
    public void onEvent(PositionAddressChangeEvent addressDateEvent) {
        mPositionLocal = addressDateEvent.getAddress();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    @Subscribe
    public void onEvent(GetWatchInfoChangedEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    @Subscribe
    public void onEvent(final BatteryEvent batteryEvent) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mMyWatch.setBatteryLevel((int) batteryEvent.getBattery().getBatteryLevel());
                initData();
            }
        });
    }

    private String countTime(int goalDuration) {
        StringBuffer sb = new StringBuffer();
        if (goalDuration > 60) {
            sb.append(goalDuration / 60 + getString(R.string.sleep_unit_hour)
                    + (goalDuration % 60 != 0 ? goalDuration % 60 + getString(R.string.sleep_unit_minute) : ""));
        } else if (goalDuration == 60) {
            sb.append(goalDuration / 60 + getString(R.string.sleep_unit_hour));
        } else {
            sb.append(goalDuration + getString(R.string.sleep_unit_minute));
        }
        return sb.toString();
    }
}

