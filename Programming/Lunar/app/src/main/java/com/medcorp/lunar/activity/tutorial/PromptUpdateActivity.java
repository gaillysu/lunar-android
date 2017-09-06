package com.medcorp.lunar.activity.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.ReadyUpdateFirmwareActivity;
import com.medcorp.lunar.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason on 2017/9/6.
 */

public class PromptUpdateActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_update_firmware_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.update_firmware_bt)
    public void updateFirmware() {
        startActivity(ReadyUpdateFirmwareActivity.class);
    }

    @OnClick(R.id.not_update_firmware_bt)
    public void nextTimeUpdate() {
        startActivity(MainActivity.class);
    }
}
