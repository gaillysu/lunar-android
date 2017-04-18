package com.medcorp.lunar.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.SettingMenuAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.listener.OnCheckedChangeInListListener;
import com.medcorp.lunar.model.SettingsMenuItem;
import com.medcorp.lunar.util.Preferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Karl on 2/16/16.
 */
public class ConnectToOtherAppsActivity extends BaseActivity implements OnCheckedChangeInListListener {

    @Bind(R.id.activity_connect_to_other_apps_list_view)
    ListView otherAppsListView;

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    private Snackbar snackbar;

    private View rootView;

    private SettingMenuAdapter settingsAdapter;

    private MaterialDialog googleFitLogoutDialog;
    private int validicPositionInList = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_other_apps);
        rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        ButterKnife.bind(this);
        List<SettingsMenuItem> menuList = new ArrayList<>();
        menuList.add(new SettingsMenuItem(getString(R.string.settings_other_apps_google_fit), R.drawable.google_fit_small, Preferences.isGoogleFitSet(this)));
//        menuList.add(new SettingsMenuItem(getString(R.string.settings_other_apps_validic), R.drawable.google_fit_small, getModel().getUser().isConnectValidic()));
        settingsAdapter = new   SettingMenuAdapter(this, menuList, this);
        otherAppsListView.setAdapter(settingsAdapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.settings_other_apps_short);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private MaterialDialog.SingleButtonCallback validicNegativeCallback = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            settingsAdapter.getItem(validicPositionInList).setSwitchStatus(false);
            settingsAdapter.notifyDataSetChanged();
        }
    };


    private MaterialDialog.SingleButtonCallback googleFitPositiveCallback = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
            Preferences.setGoogleFit(ConnectToOtherAppsActivity.this, false);
            getModel().disconnectGoogleFit();
        }
    };

    private MaterialDialog.SingleButtonCallback googleFitNegativeCallback = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
            if (googleFitLogoutDialog != null) {
                if (googleFitLogoutDialog.isShowing()) {
                    googleFitLogoutDialog.dismiss();
                    settingsAdapter.toggleSwitch(0, true);
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getModel().GOOGLE_FIT_OATH_RESULT == requestCode) {
            snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG);
            TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            if (resultCode == Activity.RESULT_OK) {
                getModel().initGoogleFit(this);
                getModel().updateGoogleFit();
                tv.setText(R.string.google_fit_logged_in);
            } else {
                tv.setText(R.string.google_fit_could_not_login);
                settingsAdapter.toggleSwitch(0, false);
            }
            snackbar.show();
        }
    }

    @Override
    public void onCheckedChange(CompoundButton buttonView, boolean isChecked, int position) {

    }
}