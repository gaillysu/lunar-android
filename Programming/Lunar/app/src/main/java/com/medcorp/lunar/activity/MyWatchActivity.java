package com.medcorp.lunar.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.bluetooth.BatteryEvent;
import com.medcorp.lunar.model.MyWatch;
import com.medcorp.lunar.util.Common;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/28.
 */
public class MyWatchActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_mynevo_list_view)
    ListView myNevoListView;
    @Bind(R.id.my_device_watch_version_news_layout_root)
    LinearLayout showMyDeviceNewsLayout;

    @Bind(R.id.my_watch_version_tv)
    TextView showFirmwerVersion;
    @Bind(R.id.my_device_battery_tv)
    TextView showWatchBattery;
    @Bind(R.id.my_device_version_text)
    TextView showWatchVersion;
    @Bind(R.id.my_watch_update_tv)
    TextView firmwerUpdateInfomation;

    private MyWatch mMyWatch;
    private final int battery_level = 2; //default is 2,  value is [0,1,2], need get later
    private final boolean available_version = false;//need check later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynevo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.title_my_nevo);
        String app_version = "";
        try {
            app_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mMyWatch = new MyWatch(getModel().getWatchFirmware(), getModel().getWatchSoftware(), app_version, battery_level, available_version, null);
        myNevoListView.setVisibility(View.GONE);
        showMyDeviceNewsLayout.setVisibility(View.VISIBLE);
        initLunarData();
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
        List<String> firmwareURLs = new ArrayList<>();
        //check build-in firmwares
        //fill  list by build-in files or download files
        if (null == getModel().getWatchSoftware() || null == getModel().getWatchFirmware()) {
            return;
        }

        int currentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
        final int buildingFirmwareVersion = getResources().getInteger(R.integer.launar_version);
        if (currentFirmwareVersion < buildingFirmwareVersion) {
            firmwerUpdateInfomation.setText(getString(R.string.my_watch_firmwer_version) + " "
                    + buildingFirmwareVersion + " " + getString(R.string.my_watch_firmwer_version_describe));

            showFirmwerVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyWatchActivity.this, DfuActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(MyWatchActivity.this.getString(R.string.key_firmwares), (ArrayList<String>) Common.getAllBuildInZipFirmwareURLs(MyWatchActivity.this, getModel().getSyncController().getWatchInfomation().getWatchID()));
                    intent.putExtras(bundle);
                    MyWatchActivity.this.startActivity(intent);
                    MyWatchActivity.this.finish();
                    firmwerUpdateInfomation.setVisibility(View.VISIBLE);
                }
            });
        } else {
            showFirmwerVersion.setText(mMyWatch.getBleFirmwareVersion());
            firmwerUpdateInfomation.setVisibility(View.INVISIBLE);
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
                mMyWatch.setBatteryLevel((int) batteryEvent.getBattery().getBatteryLevel());
                initLunarData();
            }
        });
    }


    private void initLunarData() {

        String str_battery = this.getString(R.string.my_nevo_battery_low);
        if (mMyWatch.getBatteryLevel() == 2) {
            str_battery = this.getString(R.string.my_nevo_battery_full);
        } else if (mMyWatch.getBatteryLevel() == 1) {
            str_battery = this.getString(R.string.my_nevo_battery_half);
        }
        showWatchBattery.setText(str_battery);
        showWatchVersion.setText(mMyWatch.getAppVersion());
    }
}