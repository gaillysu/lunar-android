package com.medcorp.lunar.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.medcorp.lunar.ble.model.notification.Notification;
import com.medcorp.lunar.ble.model.notification.OtherAppNotification;
import com.medcorp.lunar.ble.model.notification.SmsNotification;
import com.medcorp.lunar.ble.model.notification.TelephoneNotification;
import com.medcorp.lunar.ble.model.notification.WeChatNotification;
import com.medcorp.lunar.ble.model.notification.WhatsappNotification;
import com.medcorp.lunar.ble.notification.LunarNotificationListener;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.attr.type;

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
    private PopupWindow mPopupWindow;
    private NotificationDataHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LunarNotificationListener.getNotificationAccessPermission(this);
        toolbar.setTitle(R.string.title_notifications);
        helper = new NotificationDataHelper(SettingNotificationActivity.this);
        showPopupWindow(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
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
        //        Intent intent = new Intent(this, EditSettingNotificationActivity.class);
        if (parent.getId() == activeListView.getId()) {
            selectNotification = activeNotificationList.get(position);
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            showPopupWindow(ACTIVITY_FLAG, position);
        }
        if (parent.getId() == inactiveListView.getId()) {
            selectNotification = inactiveNotificationList.get(position);
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            showPopupWindow(INACTIVITY_FLAG, position);
        }


    }

    private void showPopupWindow(final int type, final int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupWindowView = inflater.inflate(R.layout.more_setting_bottom_view, null);
        mPopupWindow = new PopupWindow(popupWindowView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.pop_window_animation_style);
        popupWindowView.findViewById(R.id.more_setting_action_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                showEditColorDialog(type, position);
            }
        });
        popupWindowView.findViewById(R.id.more_setting_action_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                if (type == ACTIVITY_FLAG) {
                    selectNotification.setState(false);
                    helper.saveState(selectNotification);
                }
            }
        });
        if (type != 0) {
            mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    private void showEditColorDialog(final int type, int position) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View inflateView = inflater.inflate(R.layout.cnotification_choose_color_dialog, null);
        final GridView allColors = (GridView) inflateView.findViewById(R.id.notification_choose_color_all_colors_gd);
        final List<LedLamp> allLamp = getModel().getLedDataBase().getAll();
        NevoLed notificationColor = Preferences.getNotificationColor(this, selectNotification, getModel());
        selectLedLamp = (LedLamp) notificationColor;
        for (LedLamp ledLamp : allLamp) {
            if (ledLamp.getColor() == notificationColor.getHexColor()) {
                ledLamp.setSelect(true);
            }
        }
        final ChooseColorAdapter chooseColorAdapter = new ChooseColorAdapter(this, allLamp);
        allColors.setAdapter(chooseColorAdapter);
        allColors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == allLamp.size()) {
                    mDialog.dismiss();
                    addNewColorForNotification();
                } else {
                    selectLedLamp = allLamp.get(position);
                    for (int i = 0; i < allLamp.size(); i++) {
                        if (i == position) {
                            allLamp.get(position).setSelect(true);
                        } else {
                            allLamp.get(i).setSelect(false);
                        }
                    }
                    chooseColorAdapter.notifyDataSetChanged();
                }
            }
        });
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.customView(inflateView, true)
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
                                initData();
                            }
                            if (type == INACTIVITY_FLAG) {
                                selectNotification.setState(true);
                                helper.saveState(selectNotification);
                            }
                        }
                    }
                });
        mDialog = builder.build();
        mDialog.show();
    }

    private void addNewColorForNotification() {

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
        new MaterialDialog.Builder(this).customView(inflateView, true)
                .positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .negativeColor(getResources().getColor(R.color.colorAccent))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String name = textLayout.getEditText().getText().toString();
                        if (!TextUtils.isEmpty(name) && selectedColor != 0) {
                            LedLamp mLedLamp = new LedLamp();
                            mLedLamp.setSelect(false);
                            mLedLamp.setName(name);
                            mLedLamp.setColor(selectedColor);
                            getModel().addLedLamp(mLedLamp);
                        } else {
                            ToastHelper.showShortToast(SettingNotificationActivity.this, getString(R.string.prompt_user_set_color_name));
                        }
                        if (type == INACTIVITY_FLAG) {
                            selectNotification.setState(true);
                            helper.saveState(selectNotification);
                        }
                    }
                }).show();

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
