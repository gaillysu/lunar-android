package com.medcorp.lunar.activity.tutorial;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_2);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_tutorial_2_continue_button)
    public void continueClicked(){
        if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivity(TutorialPage3Activity.class);
            finish();
        }else{
            new MaterialDialog.Builder(this)
                    .content(R.string.tutorial_2_dialog_positive)
                    .positiveText(android.R.string.ok)
                    .negativeText(R.string.tutorial_2_dialog_negative)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                        }
                    }).show();
        }
    }
}
