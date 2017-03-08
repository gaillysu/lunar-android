package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/7/25.
 */
public class SetSunriseAndSunsetTimeRequest extends BLERequestData {
    public  final static  byte HEADER = 0x28;

    private final byte sunriseHour;
    private final byte sunriseMin;
    private final byte sunsetHour;
    private final byte sunsetMin;

    public SetSunriseAndSunsetTimeRequest(Context context, byte sunriseHour, byte sunriseMin, byte sunsetHour, byte sunsetMin) {
        super(new GattAttributesDataSourceImpl(context));
        this.sunriseHour = sunriseHour;
        this.sunriseMin = sunriseMin;
        this.sunsetHour = sunsetHour;
        this.sunsetMin = sunsetMin;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER,sunriseHour,sunriseMin,
                        sunsetHour,sunsetMin,0,0,
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
