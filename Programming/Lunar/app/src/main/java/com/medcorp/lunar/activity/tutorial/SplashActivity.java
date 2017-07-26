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
                                startActivity(WelcomeActivity.class);
                                finish();
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        }, 1500);
                    }else{
                        if (Preferences.isAlreadyConnect(SplashActivity.this)) {
                            if(Preferences.isFirstSettingDefValue(SplashActivity.this)){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SplashActivity.this, ConfigTimeSyncActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                    }
                                }, 1500);
                            } else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                    }
                                }, 1500);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(TutorialPage1Activity.class);
                                    finish();
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
                                    startActivity(new Intent(SplashActivity.this, ConfigTimeSyncActivity.class));
                                    finish();
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                            }, 1500);
                        } else{
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                            }, 1500);
                        }
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(TutorialPage1Activity.class);
                                finish();
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        }, 1500);
                    }
                }
            }
        });


//
//        if (!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true)
//                && Preferences.isFirstSettingDefValue(this)) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(SplashActivity.this, ConfigTimeSyncActivity.class));
//                    finish();
//                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                }
//            }, 1500);
//        }
//
//        if (!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true)
//                && !Preferences.isFirstSettingDefValue(this)) {
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                    finish();
//                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                }
//            }, 1500);
//        }
//
//        if (getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true)
//                && !Preferences.getIsFirstLogin(this)) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(SplashActivity.this, TutorialPage1Activity.class));
//                    finish();
//                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                }
//            }, 1500);
//        }
    }
}
