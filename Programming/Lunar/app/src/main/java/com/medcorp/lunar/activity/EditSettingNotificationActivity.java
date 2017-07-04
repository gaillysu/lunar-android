package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.datasource.NotificationDataHelper;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.Notification;
import com.medcorp.lunar.ble.model.notification.OtherAppNotification;
import com.medcorp.lunar.util.Preferences;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by gaillysu on 15/12/31.
 */
public class EditSettingNotificationActivity extends BaseActivity {
    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_setting_notification_edit_onoff)
    SwitchCompat onOffSwitch;
    @Bind(R.id.notification_watch_icon)
    ImageView watchView;
    @Bind(R.id.notification_lamp_edit)
    RelativeLayout lunarLedLampGroup;
    @Bind(R.id.notification_activity_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.notification_lunar_lamp_color)
    ImageView lunarLampColorIv;
    @Bind(R.id.notification_name_text_view)
    TextView lampName;
    @Bind(R.id.notification_lunar_watch_icon)
    ImageView lunarWatchIcon;

    private NotificationDataHelper helper;
    private Notification notification;
    private NevoLed selectedLed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        notification = (Notification) getIntent().getExtras().getSerializable(getString(R.string.key_notification));
        watchView.setVisibility(View.GONE);
        lunarWatchIcon.setVisibility(View.VISIBLE);
        helper = new NotificationDataHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notification instanceof OtherAppNotification) {
            toolbar.setTitle(((OtherAppNotification) notification).getAppName(this));
        } else {
            toolbar.setTitle(notification.getStringResource());
        }
        onOffSwitch.setChecked(notification.isOn());
        initView();
    }

    private void initView() {
        selectedLed = Preferences.getNotificationColor(this, notification, getModel());
        lunarLampColorIv.setColorFilter(selectedLed.getHexColor());
        lampName.setText(selectedLed.getTag());

    }

    @OnCheckedChanged(R.id.activity_setting_notification_edit_onoff)
    public void notificationEditTriggered(CompoundButton buttonView, boolean isChecked) {
        notification.setState(isChecked);
        helper.saveState(notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.done_menu:
                Preferences.saveNotificationColor(this, notification, selectedLed.getHexColor());
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.notification_lamp_edit)
    public void openEditNotificationLampColor() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_notification), (Serializable) notification);
        Intent intent = new Intent(this, EditNotificationLampActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
