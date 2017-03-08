package com.medcorp.lunar.ble.model.request;


import android.content.Context;

/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
// IMPORTANT: extends  MCU_OTARequest
public  class OTAStartRequest extends MCU_OTARequest {

    public  final static  byte HEADER = 0x72;

    public OTAStartRequest(Context context) {
        super(context);
    }

    @Override
    public byte[] getRawData() {
           return null;
    }

    @Override
    public byte[][] getRawDataEx() {

        return new byte[][] {
                {0,HEADER,
                        (byte) (0xA0),
                        (byte) (0x8A),
                        (byte) (0x7D),
                        (byte) (0xDE),
                        0,0,
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
        //no used value
        return HEADER;
    }

}
