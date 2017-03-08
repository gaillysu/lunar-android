package com.medcorp.lunar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.SettingNotificationArrayAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.datasource.NotificationDataHelper;
import com.medcorp.lunar.ble.model.notification.CalendarNotification;
import com.medcorp.lunar.ble.model.notification.EmailNotification;
import com.medcorp.lunar.ble.model.notification.FacebookNotification;
import com.medcorp.lunar.ble.model.notification.Notification;
import com.medcorp.lunar.ble.model.notification.OtherAppNotification;
import com.medcorp.lunar.ble.model.notification.SmsNotification;
import com.medcorp.lunar.ble.model.notification.TelephoneNotification;
import com.medcorp.lunar.ble.model.notification.WeChatNotification;
import com.medcorp.lunar.ble.model.notification.WhatsappNotification;
import com.medcorp.lunar.ble.notification.NevoNotificationListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/31.
 */
public class SettingNotificationActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_setting_notification_active_list_view)
    ListView activeListView;
    @Bind(R.id.activity_setting_notification_inactive_list_view)
    ListView inactiveListView;
    @Bind(R.id.notification_active_title)
    RelativeLayout active;
    @Bind(R.id.inactive_notification_title)
    RelativeLayout inactive;
    @Bind(R.id.split_line_ll)
    View lineView;

    private SettingNotificationArrayAdapter activeNotificationArrayAdapter;
    private SettingNotificationArrayAdapter inactiveNotificationArrayAdapter;

    private List<Notification> activeNotificationList;
    private List<Notification> inactiveNotificationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.title_notifications);

       NevoNotificationListener.getNotificationAccessPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        activeNotificationList = new ArrayList<>();
        inactiveNotificationList = new ArrayList<>();

        List<Notification> allNotifications = new ArrayList<>();
        NotificationDataHelper dataHelper = new NotificationDataHelper(this);

        Notification applicationNotification = new TelephoneNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new SmsNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new EmailNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new FacebookNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new CalendarNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new WeChatNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new WhatsappNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));

        for (Notification notification : allNotifications) {
            if (notification.isOn()) {
                activeNotificationList.add(notification);
            } else {
                inactiveNotificationList.add(notification);
            }
        }
        //add others Apps
        NotificationDataHelper notificationDataHelper = new NotificationDataHelper(this);
        Set<String> appList = notificationDataHelper.getNotificationAppList();
        appList.retainAll(NotificationDataHelper.getAllPackages(this));
        for (String appID : appList) {
            OtherAppNotification otherAppNotification = new OtherAppNotification(appID);
            if (notificationDataHelper.getState(otherAppNotification).isOn()) {
                activeNotificationList.add(otherAppNotification);
            } else {
                inactiveNotificationList.add(otherAppNotification);
            }
        }
        activeNotificationArrayAdapter = new SettingNotificationArrayAdapter(this, activeNotificationList, getModel());
        activeListView.setAdapter(activeNotificationArrayAdapter);
        activeListView.setOnItemClickListener(this);

        inactiveNotificationArrayAdapter = new SettingNotificationArrayAdapter(this, inactiveNotificationList, getModel());
        inactiveListView.setAdapter(inactiveNotificationArrayAdapter);
        inactiveListView.setOnItemClickListener(this);

        if (activeNotificationList.size() == 0) {
            active.setVisibility(View.GONE);
        } else {
            active.setVisibility(View.VISIBLE);
        }
        if (inactiveNotificationList.size() == 0) {
            inactive.setVisibility(View.GONE);
        } else {
            inactive.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditSettingNotificationActivity.class);
        Notification applicationNotification = null;

        if (parent.getId() == activeListView.getId()) {
            applicationNotification = activeNotificationList.get(position);
        }

        if (parent.getId() == inactiveListView.getId()) {
            applicationNotification = inactiveNotificationList.get(position);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_notification), (Serializable) applicationNotification);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
