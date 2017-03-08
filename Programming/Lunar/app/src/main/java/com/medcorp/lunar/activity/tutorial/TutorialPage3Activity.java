package com.medcorp.lunar.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage3Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_3);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_tutorial_3_next_button)
    public void nextButtonClicked(){
        startActivity(new Intent(this, TutorialPage4Activity.class));
        overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
        finish();
    }
}
