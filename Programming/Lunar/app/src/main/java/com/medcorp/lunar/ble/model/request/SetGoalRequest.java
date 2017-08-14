package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.lunar.ble.model.goal.NumberOfStepsGoal;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/7/29.
 */
public class SetGoalRequest extends BLERequestData {
    public  final static  byte HEADER = 0x22;
    private NumberOfStepsGoal mGoal = new NumberOfStepsGoal(7000);
    public SetGoalRequest(Context context, NumberOfStepsGoal goal )
    {
        super(new GattAttributesDataSourceImpl(context));
        mGoal = goal;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {

        int goal_dist = 0; //unit ??cm
        int goal_steps = mGoal.getSteps();
        int goal_calories = 0; // unit ??
        int goal_time = 0; //unit ??

        return new byte[][] {
                {0,HEADER,0,0,
                        (byte) (goal_dist&0xFF),
                        (byte) ((goal_dist>>8)&0xFF),
                        (byte) ((goal_dist>>16)&0xFF),
                        (byte) ((goal_dist>>24)&0xFF),
                        (byte) (goal_steps&0xFF),
                        (byte) ((goal_steps>>8)&0xFF),
                        (byte) ((goal_steps>>16)&0xFF),
                        (byte) ((goal_steps>>24)&0xFF),
                        (byte) (goal_calories&0xFF),
                        (byte) ((goal_calories>>8)&0xFF),
                        (byte) ((goal_calories>>16)&0xFF),
                        (byte) ((goal_calories>>24)&0xFF),
                        (byte) (goal_time&0xFF),
                        (byte) ((goal_time>>8)&0xFF),
                        (byte) ((goal_time>>16)&0xFF),
                        (byte) ((goal_time>>24)&0xFF)
                },

                {(byte) 0xFF,HEADER,
                        0,0,0,0,
                        0,0,0,0,0,0,0,0,0,0,0,0,0,0
                }
        };
    }

    @Override
    public byte getHeader() {
        return HEADER;
    }

}

