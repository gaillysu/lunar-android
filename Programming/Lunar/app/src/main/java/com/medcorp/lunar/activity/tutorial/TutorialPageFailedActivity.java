package com.medcorp.lunar.activity.tutorial;

import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Karl on 1/19/16.
 */
public class TutorialPageFailedActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_failed);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_tutorial_retry_button)
    public void retryClicked(){
        finish();
    }
}
