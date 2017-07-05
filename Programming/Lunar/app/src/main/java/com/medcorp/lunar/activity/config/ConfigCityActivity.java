package com.medcorp.lunar.activity.config;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.medcorp.lunar.R;
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

    @Bind(R.id.show_location_address)
    TextView localAddress;
    @Bind(R.id.show_select_local_city_country)
    TextView localCountry;

    private Address mPositionLocal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_city_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        PermissionRequestDialogBuilder builder = new PermissionRequestDialogBuilder(this);
        builder.addPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        builder.addPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        builder.setText(R.string.location_access_content);
        builder.setTitle(R.string.location_access_title);
        builder.askForPermission(this, 1);
        String positionCity = Preferences.getPositionCity(this);
        String positionCountry = Preferences.getPositionCountry(this);
        mPositionLocal = Preferences.getLocation(ConfigCityActivity.this);
        if (positionCity == null) {
            if (mPositionLocal != null) {
                localAddress.setText(mPositionLocal.getLocality());
                localCountry.setText(mPositionLocal.getCountryName());
            } else {
                localAddress.setText(getString(R.string.config_location_failed));
            }
        } else {
            localAddress.setText(positionCity);
            localCountry.setText(positionCountry);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x02) {
            localAddress.setText(Preferences.getPositionCity(this));
            localCountry.setText(Preferences.getPositionCountry(this));
        }
    }

    @OnClick(R.id.config_location_city)
    public void selectLocalCity() {
        Intent intent = new Intent(this, SelectLocalCityActivity.class);
        startActivityForResult(intent, 0x01);
    }

    @OnClick(R.id.config_next_button)
    public void nextStep() {
        String text = localAddress.getText().toString();
        if (text != null && !text.equals(getString(R.string.config_location_failed))) {
            startActivity(ConfigGoalsActivity.class);
            finish();
        } else {
            ToastHelper.showShortToast(this, getString(R.string.config_location_city_is_null));
        }
        finish();
    }
}
