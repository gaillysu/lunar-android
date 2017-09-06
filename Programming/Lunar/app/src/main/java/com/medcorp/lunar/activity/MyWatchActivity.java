package com.medcorp.lunar.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.bluetooth.BatteryEvent;
import com.medcorp.lunar.model.MyWatch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * Created by gaillysu on 15/12/28.
 */
public class MyWatchActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.my_watch_version_tv)
    TextView showFirmwerVersion;
    @Bind(R.id.my_device_battery_tv)
    TextView showWatchBattery;
    @Bind(R.id.my_device_version_text)
    TextView showWatchVersion;
    @Bind(R.id.my_watch_update_tv)
    TextView firmwerUpdateInfomation;

    private MyWatch myWatch;
    private final int battery_level = 2; //default is 2,  value is [0,1,2], need get later
    private final boolean available_version = false;//need check later
    private int mCurrentFirmwareVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynevo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        String app_version = "";
        try {
            app_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        showFirmwerVersion.setVisibility(View.GONE);
        myWatch = new MyWatch(getModel().getWatchFirmware(), getModel().getWatchSoftware(), app_version, battery_level, available_version, null);
        initLunarData();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_my_lunar);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getModel().isWatchConnected()) {
            getModel().getBatteryLevelOfWatch();
        }
        checkVersion();
    }

    private void checkVersion() {
        //check build-in firmwares
        //fill  list by build-in files or download files
        if (null == getModel().getWatchSoftware() || null == getModel().getWatchFirmware()) {
            return;
        }
        final int buildingFirmwareVersion = getResources().getInteger(R.integer.launar_version);
        if (mCurrentFirmwareVersion < buildingFirmwareVersion) {
            showFirmwerVersion.setVisibility(View.VISIBLE);
            showFirmwerVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startAndFinishActivity(ReadyUpdateFirmwareActivity.class);
                }
            });
        } else {
            showFirmwerVersion.setVisibility(View.GONE);
            showFirmwerVersion.setText(myWatch.getBleFirmwareVersion());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(final BatteryEvent batteryEvent) {
        //fix crash:  Only the original thread that created a view hierarchy can touch its views.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                myWatch.setBatteryLevel((int) batteryEvent.getBattery().getBatteryLevel());
                initLunarData();
                showWatchBattery.setText(batteryEvent.getBattery().getBatteryCapacity() + " %");
            }
        });
    }

    private void initLunarData() {
        mCurrentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
        firmwerUpdateInfomation.setText(getString(R.string.my_watch_firmwer_version) + " " + mCurrentFirmwareVersion);
        showWatchVersion.setText(myWatch.getAppVersion());
    }
}