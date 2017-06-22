package com.medcorp.lunar.activity.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.activity.login.SignupActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.util.Preferences;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason on 2017/6/16.
 */

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.create_account_bt)
    public void startRegister() {
        startActivity(SignupActivity.class);
        finish();
    }

    @OnClick(R.id.login_bt)
    public void startLogin() {
        startActivity(LoginActivity.class);
        finish();
    }

    @OnClick(R.id.login_skip_bt)
    public void skipLogin() {
        Preferences.saveIsFirstLogin(this, false);
        if (getIntent().getBooleanExtra(getString(R.string.open_activity_is_tutorial), true)
                && !getModel().isWatchConnected()) {
            startActivity(TutorialPage1Activity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }
}
