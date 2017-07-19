package com.medcorp.lunar.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.ConnectToOtherAppsActivity;
import com.medcorp.lunar.activity.GoalsSettingActivity;
import com.medcorp.lunar.activity.MyWatchActivity;
import com.medcorp.lunar.activity.SettingNotificationActivity;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.activity.tutorial.TutorialPage1Activity;
import com.medcorp.lunar.adapter.SettingMenuAdapter;
import com.medcorp.lunar.event.bluetooth.FindWatchEvent;
import com.medcorp.lunar.fragment.base.BaseObservableFragment;
import com.medcorp.lunar.listener.OnCheckedChangeInListListener;
import com.medcorp.lunar.model.SettingsMenuItem;
import com.medcorp.lunar.util.LinklossNotificationUtils;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.ToastHelper;
import com.xw.repo.BubbleSeekBar;

import net.medcorp.library.ble.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * Created by karl-john on 14/12/15.
 */
public class SettingsFragment extends BaseObservableFragment implements OnCheckedChangeInListListener {

    @Bind(R.id.fragment_setting_app_list_view)
    ListView settingAppListView;
    @Bind(R.id.fragment_setting_local_list_view)
    ListView settingLocalListView;
    @Bind(R.id.fragment_setting_device_list_view)
    ListView settingDeviceLIstView;

    private List<SettingsMenuItem> appListMenu;
    private List<SettingsMenuItem> deviceListMenu;
    private List<SettingsMenuItem> localListMenu;
    private SettingMenuAdapter mSettingDeviceAdapter;
    private int scanTime;
    private SettingMenuAdapter mSettingAppAdapter;
    private SettingMenuAdapter mSettingLocalAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initAppData();
        initDeviceData();
        initLocalData();
        return view;
    }

    private void initAppData() {
        appListMenu = new ArrayList<>();
        String unitSubtitle = Preferences.getUnitSelect(getContext()) ? getString(R.string.user_select_metrics) : getString(R.string.user_select_imperial);
        appListMenu.add(new SettingsMenuItem(getString(R.string.settings_more), getString(R.string.setting_fragment_app_goal_subtitle), R.drawable.setting_goals));
        appListMenu.add(new SettingsMenuItem(getString(R.string.more_setting_unit), unitSubtitle, R.drawable.ic_setting_fragment_unit_icon));

        mSettingAppAdapter = new SettingMenuAdapter(getContext(), appListMenu, this);
        settingAppListView.setAdapter(mSettingAppAdapter);
        settingAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                appListItemClick(position);
            }
        });
    }

    private void initDeviceData() {
        int mSelectIndex = Preferences.getDetectionBattery(SettingsFragment.this.getActivity());
        String value = getResources().getStringArray(R.array.detection_battery)[mSelectIndex];

        Preferences.getBatterySwitch(SettingsFragment.this.getActivity());
        deviceListMenu = new ArrayList<>();
        int scanDuration = Preferences.getScanDuration(getContext());

        String scanDurationSubTitle = null;
        if (scanDuration == 60) {
            scanDurationSubTitle = " " + getString(R.string.scan_duration_item_select_one_hour);
        } else {
            scanDurationSubTitle = scanDuration + " " + getString(R.string.scan_duration_time_unit);
        }
        deviceListMenu.add(new SettingsMenuItem(getString(R.string.settings_my_nevo), R.drawable.setting_mynevo));
        deviceListMenu.add(new SettingsMenuItem(getString(R.string.settings_notifications), getString(R.string.setting_fragment_notification_subtitle),
                R.drawable.setting_notfications));

        deviceListMenu.add(new SettingsMenuItem(getString(R.string.detection_battery),
                getString(R.string.originally_chosen_value) + " : " + value,
                R.drawable.ic_low_detection_alert,
                Preferences.getBatterySwitch(SettingsFragment.this.getContext())));

        deviceListMenu.add(new SettingsMenuItem(getString(R.string.settings_bluetooth_scan),
                scanDurationSubTitle, R.drawable.ic_scan_bluetooth));
        deviceListMenu.add(new SettingsMenuItem(getString(R.string.settings_forget_watch), R.drawable.setting_forget));
        mSettingDeviceAdapter = new SettingMenuAdapter(getContext(), deviceListMenu, this);
        settingDeviceLIstView.setAdapter(mSettingDeviceAdapter);
        settingDeviceLIstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deviceListItemClick(position);
            }
        });
    }

    private void initLocalData() {
        localListMenu = new ArrayList<>();
        localListMenu.add(new SettingsMenuItem(getString(R.string.menu_drawer_find)
                , getString(R.string.find_watch_subtitle), R.drawable.ic_left_menu_find));
        localListMenu.add(new SettingsMenuItem(getString(R.string.settings_link_loss_notification),
                getString(R.string.loss_notification_subtitle), R.drawable.setting_linkloss,
                Preferences.getLinklossNotification(getActivity())));
        localListMenu.add(new SettingsMenuItem(getString(R.string.settings_other_apps), R.drawable.setting_linkloss));
        localListMenu.add(new SettingsMenuItem(getString(R.string.settings_support), R.drawable.setting_support));
        //listMenu.add(new SettingsMenuItem(getString(R.string.settings_login), R.drawable.setting_mynevo, getModel().getUser().isLogin()));
        if (getModel().getUser().isLogin()) {
            localListMenu.add(new SettingsMenuItem(getString(R.string.google_fit_log_out), R.drawable.logout_icon));
        } else {
            localListMenu.add(new SettingsMenuItem(getString(R.string.login_page_activity_title), R.drawable.ic_login_setting_page));
        }

        mSettingLocalAdapter = new SettingMenuAdapter(getContext(), localListMenu, this);
        settingLocalListView.setAdapter(mSettingLocalAdapter);
        settingLocalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                localListItemClick(position);
            }
        });
    }

    private void appListItemClick(int position) {
        switch (position) {
            case 0:
                startActivity(GoalsSettingActivity.class);
                break;
            case 1:
                boolean unitSelect = Preferences.getUnitSelect(getContext());
                new MaterialDialog.Builder(getContext())
                        .title(R.string.more_setting_unit)
                        .itemsColor(getResources().getColor(R.color.edit_alarm_item_text_color))
                        .items(getResources().getStringArray(R.array.config_unit))
                        .itemsCallbackSingleChoice(unitSelect ? 0 : 1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which == 0) {
                                    appListMenu.get(1).setSubtitle(getString(R.string.user_select_metrics));
                                    Preferences.saveUnitSelect(getContext(), true);
                                } else {
                                    appListMenu.get(1).setSubtitle(getString(R.string.user_select_imperial));
                                    Preferences.saveUnitSelect(getContext(), false);
                                }
                                mSettingAppAdapter.notifyDataSetChanged();
                                return true;
                            }
                        })
                        .positiveText(R.string.goal_ok)
                        .negativeText(R.string.goal_cancel)
                        .negativeColor(getResources().getColor(R.color.left_menu_item_text_color))
                        .show();
                break;
        }
    }

    private void deviceListItemClick(final int position) {
        switch (position) {
            case 0:
                if (getModel().isWatchConnected()) {
                    startActivity(MyWatchActivity.class);
                } else {
                    ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
                }
                break;
            case 1:
                startActivity(SettingNotificationActivity.class);
                break;
            case 2:
                int selectIndex = Preferences.getDetectionBattery(SettingsFragment.this.getActivity());
                new MaterialDialog.Builder(getContext())
                        .title(R.string.low_detection_battery_title)
                        .itemsColor(getResources().getColor(R.color.edit_alarm_item_text_color))
                        .content(getString(R.string.set_detection_battery_alerts))
                        .items(getResources().getStringArray(R.array.detection_battery))
                        .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Preferences.saveDetectionBattery(SettingsFragment.this.getActivity(), which);
                                deviceListMenu.get(1).setSubtitle(getString(R.string.originally_chosen_value) + " : "
                                        + getResources().getStringArray(R.array.detection_battery)[which]);
                                mSettingDeviceAdapter.notifyDataSetChanged();
                                return true;
                            }
                        })
                        .positiveText(R.string.goal_ok)
                        .negativeText(R.string.goal_cancel).contentColorRes(R.color.left_menu_item_text_color)
                        .show();
                break;
            case 3:
                if (getModel().isWatchConnected()) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View dialogView = inflater.inflate(R.layout.scan_duration_dialog_view, null);
                    final BubbleSeekBar seekBar = (BubbleSeekBar) dialogView.findViewById(R.id.scan_duration_time_seek_bar);
                    seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                        @Override
                        public void onProgressChanged(int progress, float progressFloat) {
                            scanTime = progress;
                        }

                        @Override
                        public void getProgressOnActionUp(int progress, float progressFloat) {

                        }

                        @Override
                        public void getProgressOnFinally(int progress, float progressFloat) {

                        }
                    });
                    int currentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
                    seekBar.setProgress(Preferences.getScanDuration(getContext()));
                    if (currentFirmwareVersion >= 14)
                        new MaterialDialog.Builder(getContext())
                                .customView(dialogView, true)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Preferences.saveScanDuration(getContext(), scanTime);
                                        getModel().getSyncController().setBleConnectTimeout(scanTime);
                                        String scanDurationSubTitle = null;
                                        if (scanTime == 60) {
                                            scanDurationSubTitle = " " + getString(R.string.scan_duration_item_select_one_hour);
                                        } else {
                                            scanDurationSubTitle = scanTime + " " + getString(R.string.scan_duration_time_unit);
                                        }
                                        deviceListMenu.get(2).setSubtitle(scanDurationSubTitle);
                                        mSettingDeviceAdapter.notifyDataSetChanged();
                                    }
                                }).positiveText(R.string.goal_ok)
                                .negativeText(R.string.goal_cancel)
                                .negativeColor(getResources().getColor(R.color.left_menu_item_text_color))
                                .show();
                    else {
                        askUserIsUpdate();
                    }
                } else {
                    ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
                }
                break;
            case 4:
                new MaterialDialog.Builder(getContext())
                        .content(R.string.settings_sure)
                        .negativeText(android.R.string.no)
                        .positiveText(android.R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                getModel().forgetDevice();
                                startActivity(TutorialPage1Activity.class);
                                SettingsFragment.this.getActivity().finish();
                            }
                        })
                        .cancelable(false)
                        .show();
                break;
        }
    }


    private void localListItemClick(int position) {
        switch (position) {
            case 0:
                if (getModel().isWatchConnected()) {
                    getModel().blinkWatch();
                } else {
                    ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
                }
                break;
            case 2:
                startActivity(ConnectToOtherAppsActivity.class);
                break;
            case 3:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_url)));
                getActivity().startActivity(intent);
                break;
            case 4:
                if (!getModel().getUser().isLogin()) {
                    getModel().removeUser(getModel().getUser());
                    Intent newIntent = new Intent(SettingsFragment.this.getContext(), LoginActivity.class);
                    SettingsFragment.this.getContext().getSharedPreferences(Constants.PREF_NAME, 0)
                            .edit().putBoolean(Constants.FIRST_FLAG, true);
                    startActivity(newIntent);
                } else {
                    Intent newIntent = new Intent(SettingsFragment.this.getContext(), LoginActivity.class);
                    getModel().getUser().setIsLogin(false);
                    getModel().saveUser(getModel().getUser());
                    startActivity(newIntent);
                }
                SettingsFragment.this.getActivity().finish();
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }

    private void askUserIsUpdate() {
        new MaterialDialog.Builder(getContext())
                .content(R.string.prompt_user_have_new_version)
                .negativeText(android.R.string.no)
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        startActivity(MyWatchActivity.class);
                    }
                })
                .cancelable(false)
                .show();
    }

    @Override
    public void onCheckedChange(CompoundButton buttonView, boolean isChecked, int position) {
        if (position == 0) {
            Preferences.saveLinklossNotification(getActivity(), isChecked);
        } else if (position == 5) {
            Preferences.saveBatterySwitch(getActivity(), isChecked);
            int[] vaule = {5, 10, 15, 20, 30};
            getModel().getSyncController().setChargingNotification((byte) vaule[Preferences.getDetectionBattery
                    (SettingsFragment.this.getContext())], isChecked);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(final FindWatchEvent event) {
        if (event.isSuccess()) {
            //when find watch, vibrate cell phone once that means finding out
            LinklossNotificationUtils.sendNotification(getActivity(), true);
        }
    }
}
