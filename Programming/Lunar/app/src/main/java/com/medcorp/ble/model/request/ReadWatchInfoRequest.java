package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/7/25.
 */
public class ReadWatchInfoRequest extends BLERequestData {
    public  final static  byte HEADER = 0x27;
    public ReadWatchInfoRequest(Context context) {
        super(new GattAttributesDataSourceImpl(context));
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER,0,0,
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
