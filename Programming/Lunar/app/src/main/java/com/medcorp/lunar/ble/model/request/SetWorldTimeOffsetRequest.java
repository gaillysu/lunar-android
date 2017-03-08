package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/8/1.
 */
public class SetWorldTimeOffsetRequest extends BLERequestData {
    public  final static  byte HEADER = 0x03;
    private final float  offset;//0 ~~ 23.5, include such as : 8.5, 16.5 etc.

    public SetWorldTimeOffsetRequest(Context context, float offset) {
        super(new GattAttributesDataSourceImpl(context));
        this.offset = offset;
    }

    @Override
    public byte[] getRawData() {

        return null;
    }

    @Override
    public byte[][] getRawDataEx() {

        return new byte[][] {
                {0,HEADER,(byte)offset,(((byte)offset)*1f < offset)?(byte)30:(byte)0,
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
