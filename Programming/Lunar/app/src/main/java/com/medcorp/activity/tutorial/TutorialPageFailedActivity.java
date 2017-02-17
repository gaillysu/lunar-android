package com.medcorp.activity.tutorial;

import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.base.BaseActivity;
import com.medcorp.R;

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
