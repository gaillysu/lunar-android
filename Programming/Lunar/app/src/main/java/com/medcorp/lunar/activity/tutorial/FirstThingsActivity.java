package com.medcorp.lunar.activity.tutorial;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.SetSunriseAndSunsetTimeRequestEvent;
import com.medcorp.lunar.util.Preferences;

import net.medcorp.library.permission.PermissionRequestDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * Created by Jason on 2017/8/31.
 */

public class FirstThingsActivity extends BaseActivity {

    @Bind(R.id.first_things_access_notification)
    ImageButton notificationButton;
    @Bind(R.id.first_things_access_location)
    ImageButton locationButton;
    @Bind(R.id.first_things_access_google_fit)
    ImageButton googleFitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_things_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        PackageManager packageManager = getPackageManager();
        boolean coarseLocation = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION", getPackageName());
        boolean fineLocation = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", getPackageName());
        if (coarseLocation == true && fineLocation == true) {
            locationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ais_error));
        } else {
            locationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
        }

        ContentResolver contentResolver = this.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ais_error));
        } else {
            notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
        }
    }

    @OnClick(R.id.first_things_access_notification)
    public void accessNotifications() {
        getNotificationAccessPermission(this);
    }

    @OnClick(R.id.first_things_access_location)
    public void accessLocation() {
        PermissionRequestDialogBuilder builder = new PermissionRequestDialogBuilder(this);
        builder.addPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        builder.addPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        builder.setText(R.string.location_access_content);
        builder.setTitle(R.string.location_access_title);
        builder.askForPermission(this, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TutorialPage4Activity", "request location permission success.");
                EventBus.getDefault().post(new SetSunriseAndSunsetTimeRequestEvent(SetSunriseAndSunsetTimeRequestEvent.STATUS.START));
            } else {
                Log.w("TutorialPage4Activity", "request location permission failed.");
                new MaterialDialog.Builder(this)
                        .title(android.R.string.dialog_alert_title)
                        .content(R.string.permission_location_message)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
                            }
                        })
                        .negativeText(android.R.string.ok)
                        .cancelable(false)
                        .show();
            }
        }
    }

    @OnClick(R.id.first_things_access_google_fit)
    public void accessGoogleFit() {

    }

    public void getNotificationAccessPermission(final Context ctx) {
        new MaterialDialog.Builder(ctx)
                .title(R.string.notification_access_title)
                .content(R.string.notification_access_message)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 0x06 << 12);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x06 << 12) {
            ContentResolver contentResolver = this.getContentResolver();
            String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
            String packageName = getPackageName();
            if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
                notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ais_error));
            } else {
                notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
            }
        }
    }

    @OnClick(R.id.first_things_start_button)
    public void startUserLunaR() {
        startAndFinishActivity(TutorialPage1Activity.class);
        Preferences.saveIsFirstLogin(this, false);
    }
}
