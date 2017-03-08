package com.medcorp.lunar.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.util.Preferences;

import net.medcorp.library.ble.util.Constants;

/**
 * Created by gaillysu on 16/1/14.
 *
 */
public class TutorialPageVideoActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_page);
        if(Preferences.getIsFirstLogin(this))
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                  Intent intent = new Intent(TutorialPageVideoActivity.this,LoginActivity.class);
                    intent.putExtra("isTutorialPage",true);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            },1500);
        }
        if (!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(TutorialPageVideoActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            },1500);
        }

        if(getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true) && !Preferences.getIsFirstLogin(this)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(TutorialPageVideoActivity.this, TutorialPage1Activity.class));
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            },1500);
        }
    }
}
