package com.medcorp.lunar.activity.tutorial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.model.color.LedLamp;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.BedtimeModel;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.StepsGoal;
import com.medcorp.lunar.util.Preferences;

import net.medcorp.library.ble.util.Constants;

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
            //default steps goal
            getModel().addGoal(new StepsGoal(getString(R.string.startup_goal_light), true, 7000));
            getModel().addGoal(new StepsGoal(getString(R.string.startup_goal_moderate), true, 10000));
            getModel().addGoal(new StepsGoal(getString(R.string.startup_goal_heavy), true, 20000));
            //default Sleep goal
           addSleepDefGoal();
            //default solar goal
            addSolarDefGoal();

            //notification default color
            getModel().addLedLamp(new LedLamp(getString(R.string.led_lamp_color_red), getResources().getColor(R.color.red_normal)));
            getModel().addLedLamp(new LedLamp(getString(R.string.led_lamp_color_blue), getResources().getColor(R.color.blue_normal)));
            getModel().addLedLamp(new LedLamp(getString(R.string.led_lamp_color_light_green), getResources().getColor(R.color.light_green_normal)));
            getModel().addLedLamp(new LedLamp(getString(R.string.led_lamp_color_orange), getResources().getColor(R.color.orange_normal)));
            getModel().addLedLamp(new LedLamp(getString(R.string.led_lamp_color_yellow), getResources().getColor(R.color.yellow_normal)));
            getModel().addLedLamp(new LedLamp(getString(R.string.led_lamp_color_green), getResources().getColor(R.color.green_normal)));
            setDefAlarm();
            setDefBedtime();
            sharedPreferences.putBoolean(getString(R.string.key_preset), true);
            sharedPreferences.commit();
        }

    }

    private void setDefBedtime() {
        BedtimeModel bedtimeDef = new BedtimeModel("bedTime1",480, new byte[]{6}, 8
                , 30, new byte[]{5}, false);
        getModel().getBedTimeDatabaseHelper().add(bedtimeDef).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.i("jason","save def bedtime success");
            }
        });
        BedtimeModel bedtimeDef2 = new BedtimeModel("bedTime2", 480,new byte[]{0}, 9
                , 0, new byte[]{6}, false);
        getModel().getBedTimeDatabaseHelper().add(bedtimeDef2).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.i("jason","save def bedtime success");
            }
        });
    }

    private void setDefAlarm(){
        getModel().addAlarm(new Alarm(21, 0, (byte) (0), getString(R.string.def_alarm_one), (byte) 0, (byte) 13)).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Log.i("jason","save def alarm success");
                }
            }
        });
        getModel().addAlarm(new Alarm(8, 0, (byte) (0), getString(R.string.def_alarm_two), (byte) 1, (byte) 0)).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Log.i("jason","save def alarm success");
                }
            }
        });
    }

    private void addSolarDefGoal() {
        getModel().getSolarGoalDatabaseHelper().add(new SolarGoal(getString(R.string.solar_goal_def_long),480,true)).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Log.i("jason","add def solar goal success");
                }
            }
        });
        getModel().getSolarGoalDatabaseHelper().add(new SolarGoal(getString(R.string.solar_goal_def_short),30,false)).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Log.i("jason","add def sleep goal success");
                }
            }
        });
    }

    private void addSleepDefGoal() {
        SleepGoal defSleepGoal =  new SleepGoal(getString(R.string.sleep_goal_def_long),480,true);
        getModel().getSleepDatabseHelper().add(defSleepGoal).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Log.i("jason","add def sleep success");
                }
            }
        });
        Preferences.saveSleepGoal(this,defSleepGoal);
        getModel().getSleepDatabseHelper().add(new SleepGoal(getString(R.string.sleep_goal_def_noon),90,false)).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Log.i("jason","add def sleep success");
                }
            }
        });
        getModel().getSleepDatabseHelper().add(new SleepGoal(getString(R.string.sleep_goal_def_short),30,false)).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Log.i("jason","add def sleep success");
                }
            }
        });
    }

    @OnClick(R.id.activity_tutorial_success_next_button)
    public void nextClicked() {
        startActivity(MainActivity.class);
        finish();
    }
}