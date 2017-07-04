package com.medcorp.lunar.fragment;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.medcorp.lunar.R;
import com.medcorp.lunar.event.ChangeGoalEvent;
import com.medcorp.lunar.event.DateSelectChangedEvent;
import com.medcorp.lunar.event.LocationChangedEvent;
import com.medcorp.lunar.event.Timer10sEvent;
import com.medcorp.lunar.event.bluetooth.GetWatchInfoChangedEvent;
import com.medcorp.lunar.event.bluetooth.LittleSyncEvent;
import com.medcorp.lunar.event.bluetooth.OnSyncEvent;
import com.medcorp.lunar.event.bluetooth.PositionAddressChangeEvent;
import com.medcorp.lunar.event.bluetooth.SolarConvertEvent;
import com.medcorp.lunar.fragment.base.BaseFragment;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.SleepData;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.Common;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.SleepDataHandler;
import com.medcorp.lunar.util.SleepDataUtils;
import com.medcorp.lunar.util.TimeUtil;
import com.medcorp.lunar.view.RoundProgressBar;

import net.medcorp.library.worldclock.City;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by Jason on 2016/12/26.
 */

public class MainClockFragment extends BaseFragment {

    @Bind(R.id.lunar_main_clock_home_hour)
    ImageView hourImage;

    @Bind(R.id.lunar_main_clock_home_minute)
    ImageView minImage;

    @Bind(R.id.lunar_main_clock_sleep_time_count)
    TextView lunarSleepTotal;

    @Bind(R.id.lunar_main_clock_home_city_time)
    TextView lunarHomeCityTime;

    @Bind(R.id.lunar_main_clock_home_city_name)
    TextView homeCityName;

    @Bind(R.id.lunar_main_clock_home_country)
    TextView countryName;

    @Bind(R.id.lunar_main_clock_home_city_sunrise_time_tv)
    TextView sunriseOfSunsetTime;

    @Bind(R.id.lunar_main_clock_home_city_name_tv)
    TextView sunriseCityName;

    @Bind(R.id.lunar_main_clock_steps_goal_analysis)
    RoundProgressBar goalProgress;

    @Bind(R.id.lunar_main_clock_steps_count)
    TextView stepsCount;

    @Bind(R.id.steps_of_goal_percentage)
    TextView goalPercentage;

    @Bind(R.id.lunar_main_clock_home_city_sunrise_icon)
    ImageView sunriseOrSunsetIv;

    @Bind(R.id.lunar_main_clock_home_city_sunrise)
    TextView sunriseTv;

    @Bind(R.id.lunar_main_clock_battery_status)
    TextView solarHarvestStatus;

    @Bind(R.id.lunar_main_clock_battery_status_title)
    TextView solarHarvestTitle;
    @Bind(R.id.main_clock_solar_harvesting_duration)
    TextView harvestDuration;
    @Bind(R.id.main_clock_solar_harvesting_percentage)
    TextView harvestPercentage;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private User user;

    private String homeName;
    private String homeCountryName;
    private Address mPositionLocal;
    private SunriseSunsetCalculator calculator;
    private String totalSleepTime;
    private City mDefaultTimeZoneCity;

    private void refreshClock() {
        final Calendar calendar = Calendar.getInstance();
        int mCurHour = calendar.get(Calendar.HOUR);
        int mCurMin = calendar.get(Calendar.MINUTE);
        minImage.setRotation((float) (mCurMin * 6));
        hourImage.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getModel().getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainClockFragmentView = inflater.inflate(R.layout.lunar_main_fragment_adapter_clock_layout, container, false);
        ButterKnife.bind(this, mainClockFragmentView);
        mPositionLocal = Preferences.getLocation(MainClockFragment.this.getContext());
        setHasOptionsMenu(true);
        refreshClock();
        initData();
        return mainClockFragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }

    private void initData() {
        Date date = new Date();
        getModel().getStepsHelper().get(user.getUserID(), date).subscribe(new Consumer<Steps>() {
            @Override
            public void accept(Steps dailySteps) throws Exception {
                mDefaultTimeZoneCity = getDefaultTimeZoneCity();
                Log.i("ja", dailySteps.getSteps() + "");
                if (dailySteps.getSteps() != 0) {
                    stepsCount.setText(dailySteps.getSteps() + "");
                    float percent = (float) dailySteps.getSteps() / (float) dailySteps.getGoal();
                    goalProgress.setProgress(percent * 100 >= 100f ? 100 : (int) (percent * 100));
                    goalPercentage.setText((percent * 100 >= 100f ? 100 : (int) (percent * 100)) + "%" + getString(R.string.lunar_steps_percentage));
                } else {
                    stepsCount.setText("0");
                    goalPercentage.setText("0%" + getString(R.string.lunar_steps_percentage));
                }
            }
        });

        getModel().getSolarDatabaseHelper().get(user.getId(), new Date()).subscribe(new Consumer<Solar>() {
            @Override
            public void accept(Solar solar) throws Exception {
                if (solar.getTotalHarvestingTime() != 0) {
                    harvestDuration.setText(countTime(solar.getTotalHarvestingTime()));
                    float percentage = (float) solar.getTotalHarvestingTime() / (float) solar.getGoal();
                    harvestPercentage.setText((percentage * 100 >= 100f ? 100 : (int) (percentage * 100)) + "%" + getString(R.string.lunar_steps_percentage));
                } else {
                    harvestDuration.setText("0");
                    harvestPercentage.setText("0%" + getString(R.string.lunar_steps_percentage));
                }
            }
        });
        setHomeCityData();
        countSleepTime(date);
        setSunsetOrSunrise();
    }

    private void setSunsetOrSunrise() {
        mDefaultTimeZoneCity = getDefaultTimeZoneCity();
        if (mPositionLocal == null) {
            if (mDefaultTimeZoneCity != null) {
                sunriseCityName.setText(mDefaultTimeZoneCity.getName());
                calculator = computeSunriseTime(mDefaultTimeZoneCity.getLat(),
                        mDefaultTimeZoneCity.getLng(), Calendar.getInstance().getTimeZone().getID());
            } else {
                calculator = null;
                sunriseCityName.setText(getString(R.string.seek_failed));
            }
        } else {
            sunriseCityName.setText(mPositionLocal.getLocality());
            calculator = computeSunriseTime(mPositionLocal.getLatitude(), mPositionLocal.getLongitude()
                    , Calendar.getInstance().getTimeZone().getID());
        }

        if (calculator != null) {
            String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
            String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());

            int sunriseHour = Integer.parseInt(officialSunrise.split(":")[0]);
            int sunriseMin = Integer.parseInt(officialSunrise.split(":")[1]);
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            if (sunriseHour * 60 + sunriseMin > hour * 60 + minute) {
                sunriseOfSunsetTime.setText(sunriseHour + ":" + sunriseMin);
                sunriseOrSunsetIv.setImageDrawable(getResources().getDrawable(R.drawable.sunrise_icon));
                sunriseTv.setText(getString(R.string.lunar_main_clock_home_city_sunrise));
            } else {
                sunriseOfSunsetTime.setText(new Integer(officialSunset.split(":")[0]).intValue() - 12 + ":"
                        + officialSunset.split(":")[1] + getString(R.string.time_able_afternoon));
                sunriseTv.setText(getString(R.string.lunar_main_clock_home_city_sunset));
                sunriseOrSunsetIv.setImageDrawable(getResources().getDrawable(R.drawable.sunset_icon));
            }
        }
    }

    private void setHomeCityData() {
        homeName = Preferences.getPositionCity(MainClockFragment.this.getActivity());
        homeCountryName = Preferences.getPositionCountry(MainClockFragment.this.getActivity());
        if (homeName == null) {
            if (mPositionLocal == null) {
                if (mDefaultTimeZoneCity != null) {
                    homeName = mDefaultTimeZoneCity.getName();
                    homeCountryName = mDefaultTimeZoneCity.getCountry();
                } else {
                    homeCityName.setText(getString(R.string.seek_failed));
                    countryName.setText("");
                }
            } else {
                homeName = mPositionLocal.getLocality();
                homeCountryName = mPositionLocal.getCountryName();
            }
        }

        homeCityName.setText(homeName);
        countryName.setText(homeCountryName);
        setHomeCityTime();
    }

    public void setHomeCityTime() {
        Calendar mCalendar;
        if (Preferences.getPositionCity(MainClockFragment.this.getActivity()) != null) {
            mCalendar = Calendar.getInstance(TimeZone.getTimeZone(
                    Preferences.getHomeTimezoneId(MainClockFragment.this.getActivity())));
        } else {
            mCalendar = Calendar.getInstance();
        }
        String am_pm = mCalendar.get(Calendar.HOUR_OF_DAY) < 12 ?
                getString(R.string.time_able_morning) : getString(R.string.time_able_afternoon);
        String minute = mCalendar.get(Calendar.MINUTE) >= 10 ? mCalendar.get(Calendar.MINUTE) + "" : "0" + mCalendar.get(Calendar.MINUTE);
        lunarHomeCityTime.setText(mCalendar.get(Calendar.HOUR_OF_DAY) + ":" + minute + am_pm);

    }

    private SunriseSunsetCalculator computeSunriseTime(double latitude, double longitude, String zone) {
        com.luckycatlabs.sunrisesunset.dto.Location sunriseLocation =
                new com.luckycatlabs.sunrisesunset.dto.Location(latitude + "", longitude + "");
        return new SunriseSunsetCalculator(sunriseLocation, zone);
    }

    private void countSleepTime(final Date date) {
        getModel().getDailySleep(user.getUserID(), date, new MainSleepFragment.TodaySleepListener() {
            @Override
            public void todaySleep(Sleep[] sleepArray) {
                SleepDataHandler handler = new SleepDataHandler(Arrays.asList(sleepArray));
                List<SleepData> sleepDataList = handler.getSleepData(date);
                if (!sleepDataList.isEmpty()) {
                    SleepData sleepData;
                    if (sleepDataList.size() == 2) {
                        sleepData = SleepDataUtils.mergeYesterdayToday(sleepDataList.get(1), sleepDataList.get(0));
                        totalSleepTime = TimeUtil.formatTime(sleepData.getTotalSleep());
                    } else {
                        sleepData = sleepDataList.get(0);
                        totalSleepTime = TimeUtil.formatTime(sleepData.getTotalSleep());
                    }
                } else {
                    totalSleepTime = new String("00:00");
                }
                lunarSleepTotal.setText(totalSleepTime);
            }
        });
    }

    public interface ObtainGoalListener {
        void obtainGoal(List<StepsGoal> list);
    }

    @Subscribe
    public void onEvent(LocationChangedEvent locationChangedEvent) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    @Subscribe
    public void onEvent(PositionAddressChangeEvent addressDateEvent) {
        mPositionLocal = addressDateEvent.getAddress();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
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
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            Steps steps = getModel().getDailySteps(getModel().getUser().getUserID(), Common.removeTimeFromDate(new Date()));
            if (steps == null) {
                return;
            }
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            });
        }
    }

    @Subscribe
    public void onEvent(final DateSelectChangedEvent event) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    @Subscribe
    public void onEvent(final Timer10sEvent event) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshClock();
                setHomeCityTime();
            }
        });
    }

    @Subscribe
    public void onEvent(ChangeGoalEvent cahngeGoal) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    @Subscribe
    public void onEvent(final OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED || event.getStatus() == OnSyncEvent.SYNC_EVENT.TODAY_SYNC_STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            });
        }
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
    public void onEvent(final SolarConvertEvent event) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                //NOTICE: nevo solar adc threshold is 200，but lunar is 170
                if (event.getPv_adc() >= 170) {
                    solarHarvestStatus.setText(R.string.lunar_home_clock_solar_harvest_charge);
                } else {
                    solarHarvestStatus.setText(R.string.lunar_home_clock_solar_harvest_idle);
                }
                solarHarvestTitle.setText("ADC = " + event.getPv_adc());
            }
        });
    }


    public City getDefaultTimeZoneCity() {
        List<City> cities = getModel().getWorldClockDatabaseHelper().getAll();
        TimeZone timeZone = Calendar.getInstance().getTimeZone();
        String localCityName = timeZone.getID().split("/")[1].replace("_", " ");
        for (City city : cities) {
            if (city.getName().equals(localCityName)) {
                return city;
            }
        }
        return null;
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

