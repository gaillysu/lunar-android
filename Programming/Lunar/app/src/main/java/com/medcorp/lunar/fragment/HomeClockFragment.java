package com.medcorp.lunar.fragment;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.EditWorldClockActivity;
import com.medcorp.lunar.event.bluetooth.HomeTimeEvent;
import com.medcorp.lunar.event.bluetooth.PositionAddressChangeEvent;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.util.Preferences;

import net.medcorp.library.worldclock.City;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/10/24.
 */

public class HomeClockFragment extends BaseObservableFragment {

    @Bind(R.id.show_day_weekday_tv)
    TextView showLocationDate;
    @Bind(R.id.hour_clock_iv)
    ImageView hourClock;
    @Bind(R.id.minutes_clock_iv)
    ImageView minuteClock;
    @Bind(R.id.home_time_day_tv)
    TextView homeDay;
    @Bind(R.id.world_clock_fragment_show_location_city_tv)
    TextView showLocationCityInfo;

    private List<City> cities;
    private String localCityName;
    private TimeZone timeZone;
    private String homeCityName;
    private String homeCountryName;
    private Calendar mHomeCalendar;
    private Address mPositionLocal;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sunrise_sunset_activity, container, false);
        ButterKnife.bind(this, view);
        mPositionLocal = Preferences.getLocation(HomeClockFragment.this.getContext());
        cities = getModel().getWorldClockDatabaseHelper().getAll();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        refreshClock();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView() {
        homeCityName = Preferences.getPositionCity(HomeClockFragment.this.getContext());
        homeCountryName = Preferences.getPositionCountry(HomeClockFragment.this.getContext());

        Calendar calendar = Calendar.getInstance();
        timeZone = calendar.getTimeZone();
        if (homeCityName == null) {
            if (mPositionLocal == null ) {
                localCityName = timeZone.getID().split("/")[1].replace("_", " ");
            } else {
                localCityName = mPositionLocal.getLocality() + ", " + mPositionLocal.getCountryName();
            }
        }

        if (homeCityName != null) {
            showLocationCityInfo.setText(homeCityName + "," + homeCountryName);
            mHomeCalendar = Calendar.getInstance(TimeZone.getTimeZone(Preferences.
                    getHomeTimezoneId(HomeClockFragment.this.getActivity())));
        } else {
            mHomeCalendar = calendar;
            showLocationCityInfo.setText(localCityName);
        }
        syncWatch(mHomeCalendar);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_goal_menu:
                startActivity(EditWorldClockActivity.class);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(PositionAddressChangeEvent addressDateEvent) {
        mPositionLocal = addressDateEvent.getAddress();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });
    }

    private void refreshClock() {
        setHomeDay(mHomeCalendar);
        int mCurHour = mHomeCalendar.get(Calendar.HOUR);
        int mCurMin = mHomeCalendar.get(Calendar.MINUTE);
        minuteClock.setRotation((float) (mCurMin * 6));
        hourClock.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));

        showLocationDate.setText(mHomeCalendar.get(Calendar.DAY_OF_MONTH) + " "
                + new SimpleDateFormat("MMM").format(mHomeCalendar.getTime()) + " ,"
                + mHomeCalendar.get(Calendar.YEAR));
    }


    private void syncWatch(Calendar mCalendar) {

        byte homeTimeHour = (byte) mCalendar.get(Calendar.HOUR);
        byte homeTimeMinute = (byte) mCalendar.get(Calendar.MINUTE);
        EventBus.getDefault().post(new HomeTimeEvent(homeTimeHour, homeTimeMinute));
    }

    public void setHomeDay(Calendar mCalendar) {
        Calendar localCalendar = Calendar.getInstance();
        int localDayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH);
        int homeDayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        if (localDayOfMonth == homeDayOfMonth) {
            homeDay.setText(R.string.sunset_activity_title_tv);
        } else {
            int timeDifference = homeDayOfMonth - localDayOfMonth;
            if (timeDifference == 1) {
                homeDay.setText(R.string.sunset_activity_title_tomorrow);
            } else if (timeDifference == -1) {
                homeDay.setText(R.string.sunset_activity_title_yesterday);
            } else if (timeDifference > 1) {
                homeDay.setText(R.string.sunset_activity_title_yesterday);
            } else if (timeDifference < -1) {
                homeDay.setText(R.string.sunset_activity_title_tomorrow);
            }
        }
    }
}
