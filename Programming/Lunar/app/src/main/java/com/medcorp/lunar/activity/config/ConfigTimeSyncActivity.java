package com.medcorp.lunar.activity.config;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.bluetooth.DigitalTimeChangedEvent;
import com.medcorp.lunar.util.Preferences;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * Created by Jason on 2017/6/19.
 */

public class ConfigTimeSyncActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.select_sync_time_rg)
    RadioGroup selectRg;
    @Bind(R.id.select_sync_home_time_rb)
    RadioButton homeTime;
    @Bind(R.id.select_sync_local_time_rb)
    RadioButton localTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_time_sync);
        ButterKnife.bind(this);
        initView();
        selectRg.setOnCheckedChangeListener(this);
    }

    private void initView() {
        boolean placeSelect = Preferences.getPlaceSelect(ConfigTimeSyncActivity.this);
        if (placeSelect) {
            homeTime.setChecked(true);
        } else {
            localTime.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.select_sync_home_time_rb:
                Preferences.savePlaceSelect(ConfigTimeSyncActivity.this, true);
                break;
            case R.id.select_sync_local_time_rb:
                Preferences.savePlaceSelect(ConfigTimeSyncActivity.this, false);
                break;
        }
        EventBus.getDefault().post(new DigitalTimeChangedEvent(Preferences.getPlaceSelect(ConfigTimeSyncActivity.this)));
    }

    @OnClick(R.id.config_next_button)
    public void gotoConfigCity(){
        startAndFinishActivity(ConfigCityActivity.class);
    }
}
