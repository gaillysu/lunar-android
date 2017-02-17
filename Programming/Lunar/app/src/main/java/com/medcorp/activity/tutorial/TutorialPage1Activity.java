package com.medcorp.activity.tutorial;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.base.BaseActivity;
import com.medcorp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage1Activity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_1);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_tutorial_1_activate_button)
    public void activateClicked(){
        if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivity(TutorialPage3Activity.class);
        }else {
            startActivity(TutorialPage2Activity.class);
        }
        finish();
    }
}
