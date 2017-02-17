package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by gaillysu on 15/4/16.
 */
public class TestModeRequest extends BLERequestData {
    private  final byte HEADER ;
    private int mLedpattern;
    private byte key;
    private boolean sensor;
    private boolean vibrator;

    /*
    mode type
     */
    public static final byte MODE_F0 = (byte) 0xF0;
    public static final byte MODE_F1 = (byte) 0xF1;
    public static final byte MODE_F2 = (byte) 0xF2;
    public static final byte MODE_F3 = (byte) 0xF3;
    public static final byte MODE_F4 = (byte) 0xF4;

    /**
     * for F0 cmd
     * @param context
     * @param ledpattern
     * @param motorOnOff
     * @param header
     */
    public TestModeRequest(Context context, int ledpattern, boolean motorOnOff, byte header)
    {
        super(new GattAttributesDataSourceImpl(context));
        HEADER = header;
        vibrator = motorOnOff;
        mLedpattern = ledpattern;
    }

    /**
     * for F1 cmd
     * @param context
     * @param key
     * @param header
     */
    public TestModeRequest(Context context,byte key,byte header)
    {
        super(new GattAttributesDataSourceImpl(context));
        this.key = key;
        HEADER = header;
    }

    /**
     * sensor test for F2 cmd
     * @param context
     * @param sensor
     * @param header
     */
    public TestModeRequest(Context context,boolean sensor,byte header)
    {
        super(new GattAttributesDataSourceImpl(context));
        this.sensor = sensor;
        HEADER = header;
    }

    /**
     * for cmd F3/F4 without parameter
     * @param context
     * @param header
     */
    public TestModeRequest(Context context,byte header)
    {
        super(new GattAttributesDataSourceImpl(context));
        HEADER = header;
    }
    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        if(HEADER == MODE_F0) {
            return new byte[][]{
                    {0, HEADER,
                            (byte) 0xFF, vibrator?(byte) 0x8F : (byte) 0x0F,
                            (byte) ((mLedpattern >> 16) & 0xFF),
                            (byte) ((mLedpattern >> 8) & 0xFF),
                            (byte) (mLedpattern & 0xFF),
                            0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    },

                    {(byte) 0xFF, HEADER, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    }
            };
        }
        if(HEADER == MODE_F1) {
            return new byte[][]{
                    {0, HEADER,key,0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    },

                    {(byte) 0xFF, HEADER, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    }
            };
        }
        if(HEADER == MODE_F2) {
            return new byte[][]{
                    {0, HEADER, (byte) (sensor?1:0),0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    },

                    {(byte) 0xFF, HEADER, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    }
            };
        }
        if(HEADER == MODE_F3 || HEADER == MODE_F4 ) {
            return new byte[][]{
                    {0, HEADER, 0,0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    },

                    {(byte) 0xFF, HEADER, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0
                    }
            };
        }
        return null;
    }

    @Override
    public byte getHeader() {
        return HEADER;
    }
}
