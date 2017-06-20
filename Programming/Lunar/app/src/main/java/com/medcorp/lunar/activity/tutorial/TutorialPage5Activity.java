package com.medcorp.lunar.activity.tutorial;

import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.view.RoundProgressBar;

import net.medcorp.library.ble.event.BLEConnectionStateChangedEvent;
import net.medcorp.library.ble.event.BLESearchEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;


/***
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage5Activity extends BaseActivity {

    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    private int searchIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_5);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getModel().isWatchConnected())
        {
            startConnectedActivity();
        }
        else
        {
            initSearchScreen();
            getModel().startConnectToWatch(true);
        }
    }

    private void initSearchScreen()
    {
        searchIndex = 0;
        roundProgressBar.setProgress(2);
    }
    private void startConnectedActivity()
    {
        startActivity(TutorialPageSuccessActivity.class);
        finish();
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
    public void onEvent(BLESearchEvent event){
        switch (event.getSearchEvent()) {
            case ON_SEARCH_FAILURE:   runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(TutorialPageFailedActivity.class);
                }
            });
                break;
            case ON_SEARCHING:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchIndex = (searchIndex + 1) % 20;
                        roundProgressBar.setProgress(searchIndex * 5);
                    }
                });
                break;
        }
    }

    @Subscribe
    public void onEvent(BLEConnectionStateChangedEvent event){
        if (event.isConnected()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startConnectedActivity();
                }
            });
        }
    }
}
