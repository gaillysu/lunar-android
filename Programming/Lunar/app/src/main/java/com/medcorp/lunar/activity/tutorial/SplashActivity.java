package com.medcorp.lunar.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.config.ConfigTimeSyncActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.Preferences;

import io.reactivex.functions.Consumer;

/***
 * Created by gaillysu on 16/1/14.
 */
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                if (!user.isLogin()) {
                    if(Preferences.getIsFirstLogin(SplashActivity.this)){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startAndFinishActivity(WelcomeActivity.class);
                            }
                        }, 1500);
                    }else{
                        if (Preferences.isAlreadyConnect(SplashActivity.this)) {
                            if(Preferences.isFirstSettingDefValue(SplashActivity.this)){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startAndFinishActivity(ConfigTimeSyncActivity.class);
                                    }
                                }, 1500);
                            } else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startAndFinishActivity(MainActivity.class);
                                    }
                                }, 1500);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startAndFinishActivity(TutorialPage1Activity.class);
                                }
                            }, 1500);
                        }
                    }
                } else{
                    if (Preferences.isAlreadyConnect(SplashActivity.this)) {
                        if(Preferences.isFirstSettingDefValue(SplashActivity.this)){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startAndFinishActivity(ConfigTimeSyncActivity.class);
                                }
                            }, 1500);
                        } else{
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startAndFinishActivity(MainActivity.class);
                                }
                            }, 1500);
                        }
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startAndFinishActivity(TutorialPage1Activity.class);
                            }
                        }, 1500);
                    }
                }
            }
        });
    }
}
