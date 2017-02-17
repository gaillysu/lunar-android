package com.medcorp.activity;

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

import com.medcorp.R;
import com.medcorp.base.BaseActivity;
import com.medcorp.ble.datasource.NotificationDataHelper;
import com.medcorp.ble.model.color.BlueLed;
import com.medcorp.ble.model.color.GreenLed;
import com.medcorp.ble.model.color.LightGreenLed;
import com.medcorp.ble.model.color.NevoLed;
import com.medcorp.ble.model.color.OrangeLed;
import com.medcorp.ble.model.color.RedLed;
import com.medcorp.ble.model.color.YellowLed;
import com.medcorp.ble.model.notification.Notification;
import com.medcorp.ble.model.notification.OtherAppNotification;
import com.medcorp.util.Preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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


    private final List<NevoLed> ledList = new ArrayList<>();
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notification = (Notification) getIntent().getExtras().getSerializable(getString(R.string.key_notification));
        watchView.setVisibility(View.GONE);
        lunarWatchIcon.setVisibility(View.VISIBLE);

        helper = new NotificationDataHelper(this);
        ledList.add(new RedLed());
        ledList.add(new BlueLed());
        ledList.add(new LightGreenLed());
        ledList.add(new YellowLed());
        ledList.add(new OrangeLed());
        ledList.add(new GreenLed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        if (notification instanceof OtherAppNotification) {
            title.setText(((OtherAppNotification) notification).getAppName(this));
        } else {
            title.setText(notification.getStringResource());
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
