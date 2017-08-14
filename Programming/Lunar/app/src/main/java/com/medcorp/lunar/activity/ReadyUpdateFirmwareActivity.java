package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.util.Common;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * Created by Jason on 2017/7/11.
 */

public class ReadyUpdateFirmwareActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_update_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toolbar.setTitle(R.string.ready_update_firmware_title);
    }

    @OnClick(R.id.start_update_watch_firmware_button)
    public void updateFirmware(){
        Intent intent = new Intent(this, DfuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(this.getString(R.string.key_firmwares)
                , (ArrayList<String>) Common.getAllBuildInZipFirmwareURLs(this
                        , getModel().getSyncController().getWatchInfomation().getWatchID()));
        intent.putExtras(bundle);
        startAndFinishActivity(intent);
    }
}
