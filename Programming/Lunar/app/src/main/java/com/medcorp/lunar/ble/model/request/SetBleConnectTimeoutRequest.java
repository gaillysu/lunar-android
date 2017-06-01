package com.medcorp.lunar.ble.model.request;

import android.content.Context;
import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 17/5/26.
 */

public class SetBleConnectTimeoutRequest extends BLERequestData{
    public  final static  byte HEADER = 0x46;
    private short timeoutInminutes;//1-1440 minutes , otherwise value not valid

    public SetBleConnectTimeoutRequest(Context context, int timeoutInminutes) {
        super(new GattAttributesDataSourceImpl(context));
        this.timeoutInminutes = (short) timeoutInminutes;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER, (byte) (timeoutInminutes&0xFF),(byte)(timeoutInminutes>>8&0xFF),
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
