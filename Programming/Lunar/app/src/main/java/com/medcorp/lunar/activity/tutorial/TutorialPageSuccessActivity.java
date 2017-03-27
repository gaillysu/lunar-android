package com.medcorp.lunar.activity.tutorial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.controller.SyncControllerImpl;
import com.medcorp.lunar.ble.model.color.LedLamp;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.Goal;

import net.medcorp.library.ble.util.Constants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
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
        if (!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(getString(R.string.key_preset), false)) {
            getModel().addGoal(new Goal(0, getString(R.string.startup_goal_light), true, 7000));
            getModel().addGoal(new Goal(1, getString(R.string.startup_goal_moderate), true, 10000));
            getModel().addGoal(new Goal(2, getString(R.string.startup_goal_heavy), true, 20000));
            getModel().addLedLamp(new LedLamp("Rde", getResources().getColor(R.color.red_normal)));
            getModel().addLedLamp(new LedLamp("Blue", getResources().getColor(R.color.blue_normal)));
            getModel().addLedLamp(new LedLamp("Light green", getResources().getColor(R.color.light_green_normal)));
            getModel().addLedLamp(new LedLamp("Orange", getResources().getColor(R.color.orange_normal)));
            getModel().addLedLamp(new LedLamp("Yellow", getResources().getColor(R.color.yellow_normal)));
            getModel().addLedLamp(new LedLamp("Green", getResources().getColor(R.color.green_normal)));
            getModel().getAllAlarm(new SyncControllerImpl.SyncAlarmToWatchListener() {
                @Override
                public void syncAlarmToWatch(List<Alarm> alarms) {
                    if(alarms.size()==0){
                        getModel().addAlarm(new Alarm(0, 21, 0, (byte) (0), getString(R.string.def_alarm_one), (byte) 0, (byte) 7)).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Log.e("jason","闹钟默认"+aBoolean);
                            }
                        });
                        getModel().addAlarm(new Alarm(7, 8, 0, (byte) (0), getString(R.string.def_alarm_two), (byte) 1, (byte) 0)).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Log.e("jaosn","闹钟默认"+aBoolean);
                            }
                        });
                    }
                }
            });
            sharedPreferences.putBoolean(getString(R.string.key_preset), true);
            sharedPreferences.commit();
        }
    }

    @OnClick(R.id.activity_tutorial_success_next_button)
    public void nextClicked() {
        startActivity(MainActivity.class);
        finish();
    }
}
