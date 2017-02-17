package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

import java.util.UUID;


/**
 * Created by gaillysu on 15/6/8.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class MCU_OTARequest extends BLERequestData {

    public MCU_OTARequest(Context context) {
        super(new GattAttributesDataSourceImpl(context));
    }

    @Override
    public UUID getServiceUUID() {
        return super.getServiceUUID();
    }

    @Override
    public UUID getCharacteristicUUID() {
        return super.getOTACharacteristicUUID();
    }

    @Override
    public UUID getInputCharacteristicUUID() {
        return super.getOTACharacteristicUUID();
    }
}
