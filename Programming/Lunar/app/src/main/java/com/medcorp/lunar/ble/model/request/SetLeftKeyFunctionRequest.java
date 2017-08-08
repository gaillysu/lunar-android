package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 17/08/07.
 */

public class SetLeftKeyFunctionRequest extends BLERequestData{
    public  final static  byte HEADER = 0x49;
    /** please refer to BLE interface R15
     * Key Func
     0 - find my phone
     1 - music control(play/pause)
     2 - camera control
     */
    private byte function;

    public SetLeftKeyFunctionRequest(Context context, int function) {
        super(new GattAttributesDataSourceImpl(context));
        this.function = (byte) function;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER, function,0,
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
