package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.lunar.ble.model.color.BlueLed;
import com.medcorp.lunar.ble.model.color.GreenLed;
import com.medcorp.lunar.ble.model.color.LightGreenLed;
import com.medcorp.lunar.ble.model.color.OrangeLed;
import com.medcorp.lunar.ble.model.color.RedLed;
import com.medcorp.lunar.ble.model.color.YellowLed;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by gaillysu on 15/4/16.
 */
public class LedLightOnOffRequest extends BLERequestData {
    public  final static  byte HEADER = (byte)0xF0;
    private int mLedpattern;
    private boolean onoff;
    public LedLightOnOffRequest(Context context, int ledpattern, boolean onoff)
    {
        super(new GattAttributesDataSourceImpl(context));
        mLedpattern = convertColor(ledpattern);
        this.onoff = onoff;
    }
    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {0, HEADER,
                        (byte) 0x00, onoff?(byte) 0x88 : (byte) 0x00,
                        (byte) ((mLedpattern >> 16) & 0xFF),
                        (byte) ((mLedpattern >> 8) & 0xFF),
                        (byte) (mLedpattern & 0xFF),
                        0,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0
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

    private int convertColor(int ledpattern) {
        int rgbColor = ledpattern;
        if(ledpattern == RedLed.COLOR){
            rgbColor = 0xFF0000;
        }
        if(ledpattern == GreenLed.COLOR){
            rgbColor = 0x00FF00;
        }
        if(ledpattern == BlueLed.COLOR){
            rgbColor = 0x0000FF;
        }
        if(ledpattern == LightGreenLed.COLOR){
            rgbColor = 0x8DC220;
        }
        if(ledpattern == YellowLed.COLOR){
            rgbColor = 0xFAED00;
        }
        if(ledpattern == OrangeLed.COLOR){
            rgbColor = 0xF29600;
        }
        return rgbColor;
    }
}
