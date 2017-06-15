package com.medcorp.lunar.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.medcorp.lunar.activity.MoreSettingActivity;
import com.medcorp.lunar.activity.MyWatchActivity;
import com.medcorp.lunar.activity.ScanDurationActivity;
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

import net.medcorp.library.ble.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 14/12/15.
 */
public class SettingsFragment extends BaseObservableFragment implements AdapterView.OnItemClickListener, OnCheckedChangeInListListener {

    @Bind(R.id.fragment_setting_list_view)
    ListView settingListView;

    private List<SettingsMenuItem> listMenu;
    private SettingMenuAdapter settingAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        initData();
        settingAdapter = new SettingMenuAdapter(getContext(), listMenu, this);
        settingListView.setAdapter(settingAdapter);
        settingListView.setOnItemClickListener(this);
        setHasOptionsMenu(true);
        return view;
    }

    private void initData() {
        int mSelectIndex = Preferences.getDetectionBattery(SettingsFragment.this.getActivity());
        String value = getResources().getStringArray(R.array.detection_battery)[mSelectIndex];
        Preferences.getBatterySwitch(SettingsFragment.this.getActivity());
        listMenu = new ArrayList<>();
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_link_loss_notification), R.drawable.setting_linkloss, Preferences.getLinklossNotification(getActivity())));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_notifications), R.drawable.setting_notfications));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_my_nevo), R.drawable.setting_mynevo));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_find_my_watch), R.drawable.setting_findmywatch));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_bluetooth_scan), R.drawable.ic_scan_bluetooth));
        listMenu.add(new SettingsMenuItem(getString(R.string.detection_battery),
                getString(R.string.originally_chosen_value)+" : "+value,
                R.drawable.ic_low_detection_alert,Preferences.getBatterySwitch(SettingsFragment.this.getContext())));

        listMenu.add(new SettingsMenuItem(getString(R.string.settings_more), R.drawable.setting_goals));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_other_apps), R.drawable.setting_linkloss));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_support), R.drawable.setting_support));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_forget_watch), R.drawable.setting_forget));
        //listMenu.add(new SettingsMenuItem(getString(R.string.settings_login), R.drawable.setting_mynevo, getModel().getUser().isLogin()));
        if (getModel().getUser().isLogin()) {
            listMenu.add(new SettingsMenuItem(getString(R.string.google_fit_log_out), R.drawable.logout_icon));
        } else {

            listMenu.add(new SettingsMenuItem(getString(R.string.login_page_activity_title), R.drawable.ic_login_setting_page));
        }
        //        listMenu.add(new SettingsMenuItem(getString(R.string.settings_about), R.drawable.setting_about));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            startActivity(SettingNotificationActivity.class);
        } else if (position == 2) {

            if (getModel().isWatchConnected()) {
                startActivity(MyWatchActivity.class);
            } else {
                ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            }

        } else if (position == 3) {

            if (getModel().isWatchConnected()) {
                getModel().blinkWatch();

            } else {

                ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            }

        } else if (position == 4) {
            if (getModel().isWatchConnected()) {
                int currentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
                if (currentFirmwareVersion >= 14) {
                    startActivity(ScanDurationActivity.class);
                } else {
                    askUserIsUpdate();
                }
            } else {
                ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            }
        } else if (position == 5) {
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
                            listMenu.get(5).setSubtitle(getString(R.string.originally_chosen_value) + " : "
                                    + getResources().getStringArray(R.array.detection_battery)[which]);
                            settingAdapter.notifyDataSetChanged();

                            return true;
                        }
                    })
                    .positiveText(R.string.goal_ok)
                    .negativeText(R.string.goal_cancel).contentColorRes(R.color.left_menu_item_text_color)
                    .show();

        } else if (position == 6) {
            startActivity(MoreSettingActivity.class);

        } else if (position == 7) {
            startActivity(ConnectToOtherAppsActivity.class);
        } else if (position == 8) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_url)));
            getActivity().startActivity(intent);

        } else if (position == 9) {

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

        } else if (position == 10) {
            if (!getModel().getUser().isLogin()) {
                getModel().removeUser(getModel().getUser());
                Intent intent = new Intent(SettingsFragment.this.getContext(), LoginActivity.class);
                intent.putExtra("isTutorialPage", false);
                SettingsFragment.this.getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG, true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SettingsFragment.this.getContext(), LoginActivity.class);
                intent.putExtra("isTutorialPage", false);
                getModel().getUser().setIsLogin(false);
                getModel().saveUser(getModel().getUser());
                startActivity(intent);
            }
            SettingsFragment.this.getActivity().finish();
        }
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
        }else if(position == 5){
            Preferences.saveBatterySwitch(getActivity(),isChecked);
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
