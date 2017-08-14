package com.medcorp.lunar.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;


/**
 * Created by Karl on 10/15/15.
 */
public abstract class BaseActivity extends AppCompatActivity{

    private ApplicationModel application;
    public ApplicationModel getModel() {

        if (application == null) {
            application = (ApplicationModel) getApplication();
        }
        return application;
    }

    public void startActivity(Class <?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void startAndFinishActivity(Class <?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }

    public void startAndFinishActivity(Intent intent) {
        startActivity(intent);
        finish();
    }
}
