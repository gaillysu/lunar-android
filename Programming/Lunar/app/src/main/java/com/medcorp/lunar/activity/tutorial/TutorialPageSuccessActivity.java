package com.medcorp.lunar.activity.tutorial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.config.ConfigTimeSyncActivity;
import com.medcorp.lunar.base.BaseActivity;

import net.medcorp.library.ble.util.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * Created by Karl on 1/19/16.
 */
public class TutorialPageSuccessActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page_success);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        SharedPreferences.Editor sharedPreferences = getSharedPreferences(Constants.PREF_NAME, 0).edit();
        sharedPreferences.putBoolean(Constants.FIRST_FLAG, false);
        sharedPreferences.commit();

    }



    @OnClick(R.id.activity_tutorial_success_next_button)
    public void nextClicked() {
        startActivity(ConfigTimeSyncActivity.class);
        finish();
    }
}