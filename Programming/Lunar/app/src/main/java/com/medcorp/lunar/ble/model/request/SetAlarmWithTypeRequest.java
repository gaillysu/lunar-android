package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.lunar.model.Alarm;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/8/1.
 * change history:
 * 1: firmware V5, Ble interface R7 ,use total 14 alarms
 * 2: firmware V6+/R8+, use total 20 alarms
 * 3: current firmware is V14, Ble interface is R12
 */
public class SetAlarmWithTypeRequest extends BLERequestData {
    public  final static  byte HEADER = 0x41;
    public  final static int maxAlarmCount = 20;
    private int mHour;
    private int mMinute;
    private byte alarmWeekDay;//0:disable, 1~7 is Sunday to Saturday,8 - one time alarm,9- daily alarm
    private byte alarmNumber; //0 ~~ 19, 0-12	reqular/wake alarm type, 13-19	sleep start alarm type

    public SetAlarmWithTypeRequest(Context context, Alarm alarm)
    {
        super(new GattAttributesDataSourceImpl(context));
        mHour = alarm.getHour();
        mMinute = alarm.getMinute();
        if(alarm.getAlarmNumber()>maxAlarmCount)
        {
            alarmWeekDay = alarm.getWeekDay();
        }
        else
        {
            alarmWeekDay = (alarm.getWeekDay() & 0x80) == 0x80 ? (byte) (alarm.getWeekDay() & 0x0f) : (byte) 0;
        }
        this.alarmNumber = alarm.getAlarmNumber();
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {

        return new byte[][] {
                {0,HEADER,
                        (byte) (mHour&0xFF),(byte) (mMinute&0xFF),
                        (byte) (alarmNumber&0xFF),alarmWeekDay,
                        0,0,0,0,
                        0,0,
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

