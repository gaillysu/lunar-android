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
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

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
    ImageView notificationButton;
    @Bind(R.id.first_things_access_location)
    ImageView locationButton;
    @Bind(R.id.first_things_access_google_fit)
    ImageView googleFitButton;
    private PackageManager mPackageManager;
    private String mPackageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_things_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        mPackageName = getPackageName();
        mPackageManager = getPackageManager();
        boolean coarseLocation = PackageManager.PERMISSION_GRANTED ==
                mPackageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION", mPackageName);
        boolean fineLocation = PackageManager.PERMISSION_GRANTED ==
                mPackageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", mPackageName);
        if (coarseLocation == true && fineLocation == true) {
            locationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
        } else {
            locationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ais_error));
        }

        ContentResolver contentResolver = this.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(mPackageName)) {
            notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ais_error));
        } else {
            notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
        }

        if (!Preferences.isGoogleFitSet(this)) {
            googleFitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ais_error));
        } else {
            googleFitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
        }
    }

    @OnClick(R.id.first_things_access_notification_rl)
    public void accessNotifications() {
        getNotificationAccessPermission(this);
    }

    @OnClick(R.id.first_things_access_location_rl)
    public void accessLocation() {
        PermissionRequestDialogBuilder builder = new PermissionRequestDialogBuilder(this);
        builder.addPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        builder.addPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        builder.setText(R.string.location_access_content);
        builder.setTitle(R.string.location_access_title);
        builder.askForPermission(this, 0x08);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x08) {
            if (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getBaseContext()
                        , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    new MaterialDialog.Builder(this)
                            .title(android.R.string.dialog_alert_title)
                            .content(R.string.permission_location_message)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    finish();
                                }
                            })
                            .negativeText(android.R.string.ok)
                            .cancelable(false)
                            .show();
                    locationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ais_error));
                } else {
                    EventBus.getDefault().post(new SetSunriseAndSunsetTimeRequestEvent(SetSunriseAndSunsetTimeRequestEvent.STATUS.START));
                    locationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));
                }
            }
        }
    }

    @OnClick(R.id.first_things_access_google_fit_rl)
    public void accessGoogleFit() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.first_access_google_fit))
                .content(R.string.access_google_fit_message)
                .positiveText(R.string.notification_ok)
                .negativeText(R.string.cancel_update)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Preferences.setGoogleFit(FirstThingsActivity.this, true);
                        getModel().initGoogleFit(FirstThingsActivity.this);
                        googleFitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_icon));

                    }
                }).show();
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
                        startActivity(intent);
                    }
                }).show();
    }

    @OnClick(R.id.first_things_start_button)
    public void startUserLunaR() {
        startAndFinishActivity(TutorialPage1Activity.class);
        Preferences.saveIsFirstLogin(this, false);
    }
}
