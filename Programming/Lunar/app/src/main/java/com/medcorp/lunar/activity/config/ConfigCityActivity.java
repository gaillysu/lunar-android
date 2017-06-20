package com.medcorp.lunar.activity.config;

import android.Manifest;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.EditWorldClockActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.ToastHelper;

import net.medcorp.library.permission.PermissionRequestDialogBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * Created by Jason on 2017/6/19.
 */

public class ConfigCityActivity extends BaseActivity {

    @Bind(R.id.gps_location_address)
    TextView localAddress;
    private Address mPositionLocal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_city_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        initView();
    }

    private void initView() {
        PermissionRequestDialogBuilder builder = new PermissionRequestDialogBuilder(this);
        builder.addPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        builder.addPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        builder.setText(R.string.location_access_content);
        builder.setTitle(R.string.location_access_title);
        builder.askForPermission(this, 1);
        String positionCity = Preferences.getPositionCity(ConfigCityActivity.this);
        mPositionLocal = Preferences.getLocation(ConfigCityActivity.this);
        if (positionCity == null) {
            if (mPositionLocal != null) {
                localAddress.setText(mPositionLocal.getLocality() + ", " + mPositionLocal.getCountryName());
            } else {
                localAddress.setText(getString(R.string.config_location_failed));
            }
        } else {
            localAddress.setText(positionCity);
        }
    }

    @OnClick(R.id.config_location_city)
    public void selectLocalCity() {
        startActivity(EditWorldClockActivity.class);
    }

    @OnClick(R.id.config_next_button)
    public void nextStep() {
        String text = localAddress.getText().toString();
        if (text != null && !text.equals(getString(R.string.config_location_failed))) {
            startActivity(ConfigGoalsActivity.class);
        } else {
            ToastHelper.showShortToast(this, getString(R.string.config_location_city_is_null));
        }
    }
}
