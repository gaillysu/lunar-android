package com.medcorp.ble.datasource;

import android.content.Context;

import com.medcorp.R;

import net.medcorp.library.ble.datasource.GattAttributesDataSource;

import java.util.UUID;

/**
 * Created by karl-john on 29/2/16.
 */
public class GattAttributesDataSourceImpl implements GattAttributesDataSource{

    private final Context context;

    public GattAttributesDataSourceImpl(Context context) {
        this.context = context;
    }

    @Override
    public UUID getDeviceInfoUDID() {
        return UUID.fromString(context.getString(R.string.DEVICEINFO_UDID));
    }

    @Override
    public UUID getDeviceInfoBluetoothVersion() {
        return UUID.fromString(context.getString(R.string.DEVICEINFO_FIRMWARE_VERSION));
    }

    @Override
    public UUID getDeviceInfoSoftwareVersion() {
        return UUID.fromString(context.getString(R.string.DEVICEINFO_SOFTWARE_VERSION));
    }

    @Override
    public UUID getClientCharacteristicConfig() {
        return UUID.fromString(context.getString(R.string.CLIENT_CHARACTERISTIC_CONFIG));
    }

    @Override
    public UUID getService() {
        return UUID.fromString(context.getString(R.string.NEVO_SERVICE));
    }

    @Override
    public UUID getCallbackCharacteristic() {
        return UUID.fromString(context.getString(R.string.NEVO_CALLBACK_CHARACTERISTIC));
    }

    @Override
    public UUID getInputCharacteristic() {
        return UUID.fromString(context.getString(R.string.NEVO_INPUT_CHARACTERISTIC));
    }

    @Override
    public UUID getOtaCharacteristic() {
        return UUID.fromString(context.getString(R.string.NEVO_OTA_CHARACTERISTIC));
    }

    @Override
    public UUID getNotificationCharacteristic() {
        return UUID.fromString(context.getString(R.string.NEVO_NOTIFICATION_CHARACTERISTIC));
    }

    @Override
    public UUID getOtaService() {
        return UUID.fromString(context.getString(R.string.NEVO_OTA_SERVICE));
    }

    @Override
    public UUID getOtaControlCharacteristic() {
        return UUID.fromString(context.getString(R.string.NEVO_OTA_CONTROL_CHARACTERISTIC));
    }

    @Override
    public UUID getOtaCallbackCharacteristic() {
        return UUID.fromString(context.getString(R.string.NEVO_OTA_CALLBACK_CHARACTERISTIC));
    }
}
