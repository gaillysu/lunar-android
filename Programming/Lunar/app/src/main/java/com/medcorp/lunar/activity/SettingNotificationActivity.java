package com.medcorp.lunar.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.medcorp.lunar.R;
import com.medcorp.lunar.adapter.ChooseColorAdapter;
import com.medcorp.lunar.adapter.SettingNotificationArrayAdapter;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.datasource.NotificationDataHelper;
import com.medcorp.lunar.ble.model.color.LedLamp;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.CalendarNotification;
import com.medcorp.lunar.ble.model.notification.EmailNotification;
import com.medcorp.lunar.ble.model.notification.FacebookNotification;
import com.medcorp.lunar.ble.model.notification.InstagramNotification;
import com.medcorp.lunar.ble.model.notification.MessengerNotification;
import com.medcorp.lunar.ble.model.notification.Notification;
import com.medcorp.lunar.ble.model.notification.OtherAppNotification;
import com.medcorp.lunar.ble.model.notification.QQNotification;
import com.medcorp.lunar.ble.model.notification.SmsNotification;
import com.medcorp.lunar.ble.model.notification.TelephoneNotification;
import com.medcorp.lunar.ble.model.notification.TwitterNotification;
import com.medcorp.lunar.ble.model.notification.WeChatNotification;
import com.medcorp.lunar.ble.notification.LunarNotificationListener;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
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
    @Bind(R.id.notification_color_root_view)
    LinearLayout rootView;

    private SettingNotificationArrayAdapter activeNotificationArrayAdapter;
    private SettingNotificationArrayAdapter inactiveNotificationArrayAdapter;
    private List<Notification> activeNotificationList;
    private List<Notification> inactiveNotificationList;
    private LedLamp selectLedLamp;
    private Notification selectNotification;
    private int selectedColor;
    private MaterialDialog mDialog;
    private final static int ACTIVITY_FLAG = 0x01;
    private final static int INACTIVITY_FLAG = 0x02;
    private NotificationDataHelper helper;
    private MaterialDialog addNewColorDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LunarNotificationListener.getNotificationAccessPermission(this);
        helper = new NotificationDataHelper(SettingNotificationActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_notifications);
    }

    private void initData() {
        activeNotificationList = new ArrayList<>();
        inactiveNotificationList = new ArrayList<>();

        List<Notification> allNotifications = new ArrayList<>();
        NotificationDataHelper dataHelper = new NotificationDataHelper(this);
        //add fixed Apps: total 10
        Notification applicationNotification = new CalendarNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new SmsNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new WeChatNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new QQNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new TelephoneNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new TwitterNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new FacebookNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new EmailNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new InstagramNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new MessengerNotification();
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
            lineView.setVisibility(View.GONE);
        } else {
            active.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
        }
        if (inactiveNotificationList.size() == 0) {
            inactive.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
        } else {
            inactive.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (parent.getId() == activeListView.getId()) {
            selectNotification = activeNotificationList.get(position);
            showBottomDialog(ACTIVITY_FLAG, position);
        }
        if (parent.getId() == inactiveListView.getId()) {
            selectNotification = inactiveNotificationList.get(position);
            showBottomDialog(INACTIVITY_FLAG, position);
        }
    }

    private void showBottomDialog(final int type, final int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.setting_notification_bottom_dialog_layout, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(contentView);
        if (type == INACTIVITY_FLAG) {
            contentView.findViewById(R.id.setting_notification_item_active_bt).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.setting_notification_item_inactive_bt).setVisibility(View.GONE);
        } else if (type == ACTIVITY_FLAG) {
            contentView.findViewById(R.id.setting_notification_item_active_bt).setVisibility(View.GONE);
            contentView.findViewById(R.id.setting_notification_item_inactive_bt).setVisibility(View.VISIBLE);
        }
        if(!(selectNotification instanceof OtherAppNotification)) {
            contentView.findViewById(R.id.setting_notification_item_delete_bt).setVisibility(View.GONE);
        }
        dialog.show();
        contentView.findViewById(R.id.setting_notification_item_active_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectNotification.setState(true);
                helper.saveState(selectNotification);
                inactiveNotificationList.remove(position);
                activeNotificationList.add(selectNotification);
                activeNotificationArrayAdapter.notifyDataSetChanged();
                inactiveNotificationArrayAdapter.notifyDataSetChanged();
            }
        });

        contentView.findViewById(R.id.setting_notification_item_inactive_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectNotification.setState(false);
                helper.saveState(selectNotification);
                activeNotificationList.remove(position);
                activeNotificationArrayAdapter.notifyDataSetChanged();
                inactiveNotificationList.add(selectNotification);
                inactiveNotificationArrayAdapter.notifyDataSetChanged();
            }
        });

        contentView.findViewById(R.id.setting_notification_item_edit_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showEditColorDialog(type, position);
            }
        });
        contentView.findViewById(R.id.setting_notification_item_delete_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Set<String> appList = helper.getNotificationAppList();
                if (type == ACTIVITY_FLAG) {
                    activeNotificationList.remove(position);
                    activeNotificationArrayAdapter.notifyDataSetChanged();
                }
                else {
                    inactiveNotificationList.remove(position);
                    inactiveNotificationArrayAdapter.notifyDataSetChanged();
                }
                appList.remove(selectNotification.getTag());
                helper.setNotificationAppList(appList);
            }
        });
    }

    private void showEditColorDialog(final int type, final int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflateView = inflater.inflate(R.layout.cnotification_choose_color_dialog, null);
        final GridView allColors = (GridView) inflateView.findViewById(R.id.notification_choose_color_all_colors_gd);
        final List<LedLamp> allLamp = getModel().getLedDataBase().getAll();
        NevoLed notificationColor = Preferences.getNotificationColor(this, selectNotification, getModel());
        if(notificationColor instanceof LedLamp) {
            selectLedLamp = (LedLamp) notificationColor;
        }
        else {
            selectLedLamp = null;
        }
        for (LedLamp ledLamp : allLamp) {
            if (ledLamp.getColor() == notificationColor.getHexColor()) {
                ledLamp.setSelect(true);
            }
        }
        final ChooseColorAdapter chooseColorAdapter = new ChooseColorAdapter(this, allLamp);
        allColors.setAdapter(chooseColorAdapter);
        allColors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == allLamp.size()) {
                    mDialog.dismiss();
                    addNewColorForNotification(type, position);
                } else {
                    selectLedLamp = allLamp.get(pos);
                    for (int i = 0; i < allLamp.size(); i++) {
                        if (i == pos) {
                            allLamp.get(pos).setSelect(true);
                        } else {
                            allLamp.get(i).setSelect(false);
                        }
                    }
                    chooseColorAdapter.notifyDataSetChanged();
                }
            }
        });
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);

        builder.title(getString(R.string.notification_choose_color_dialog_title))
                .customView(inflateView, true)
                .positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .negativeColor(getResources().getColor(R.color.colorAccent))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (selectLedLamp != null) {
                            Preferences.saveNotificationColor(SettingNotificationActivity.this, selectNotification, selectLedLamp.getColor());
                            if (type == ACTIVITY_FLAG) {
                                activeNotificationArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        mDialog = builder.build();
        mDialog.show();
    }

    private void addNewColorForNotification(final int type, final int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflateView = inflater.inflate(R.layout.add_new_color_to_notification, null);
        ColorPickerView pickerView = (ColorPickerView) inflateView.findViewById(R.id.color_picker_view);
        final TextInputLayout textLayout = (TextInputLayout) inflateView.findViewById(R.id.add_new_color_name_input_layout);
        textLayout.setHint(getString(R.string.add_new_color_name_prompt));

        pickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColor = color;
            }
        });
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(getString(R.string.notification_choose_color_dialog_title))
                .customView(inflateView, true)
                .positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .negativeColor(getResources().getColor(R.color.colorAccent))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addNewColorDialog.dismiss();
                    }
                }).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String name = textLayout.getEditText().getText().toString();
                if (!TextUtils.isEmpty(name) && selectedColor != 0) {
                    addNewColorDialog.dismiss();
                    LedLamp mLedLamp = new LedLamp();
                    mLedLamp.setName(name);
                    mLedLamp.setColor(selectedColor);
                    getModel().addLedLamp(mLedLamp);
                    if (type == ACTIVITY_FLAG) {
                        Preferences.saveNotificationColor(SettingNotificationActivity.this, selectNotification, selectedColor);
                        activeNotificationArrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    ToastHelper.showShortToast(SettingNotificationActivity.this, getString(R.string.prompt_user_set_color_name));
                }
            }
        });
        addNewColorDialog = builder.build();
        addNewColorDialog.show();
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
