package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 17/6/8.
 */
public class SetChargingNotificationRequest extends BLERequestData {
    public  final static  byte HEADER = 0x47;
    private final byte  chargingThreshold; //0~70
    private final boolean  enablePhoneNotification;

    public SetChargingNotificationRequest(Context context, byte chargingThreshold, boolean enablePhoneNotification) {
        super(new GattAttributesDataSourceImpl(context));
        this.chargingThreshold = chargingThreshold;
        this.enablePhoneNotification = enablePhoneNotification;
    }

    @Override
    public byte[] getRawData() {

        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        int enableNotification = 1;//always keep watch notification on

        if(enablePhoneNotification) {
            enableNotification = enableNotification | 2;
        }
        return new byte[][] {
                {0,HEADER,chargingThreshold,(byte)enableNotification,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0
                },

                {(byte) 0xFF,HEADER,0,0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0
                }
        };
    }

    @Override
    public byte getHeader() {

        return HEADER;
    }
}
