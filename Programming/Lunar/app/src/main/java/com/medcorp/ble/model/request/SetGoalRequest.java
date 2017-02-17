package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.ble.model.goal.NumberOfStepsGoal;
import com.medcorp.model.GoalBase;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/7/29.
 */
public class SetGoalRequest extends BLERequestData {
    public  final static  byte HEADER = 0x22;
    private GoalBase mGoal = new NumberOfStepsGoal(NumberOfStepsGoal.LOW);
    public SetGoalRequest(Context context, GoalBase goal )
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

        byte level = (byte)(mGoal.getGoalIntensity().ordinal());
        byte display = 0;  //default is step goal showing
        int goal_dist = 0; //unit ??cm

        int goal_steps = mGoal.getType().equals(NumberOfStepsGoal.TYPE) ? mGoal.getValue() : 0;

        int goal_carlories = 0; // unit ??
        int goal_time = 0; //unit ??


        return new byte[][] {
                {0,HEADER,level,display,
                        (byte) (goal_dist&0xFF),
                        (byte) ((goal_dist>>8)&0xFF),
                        (byte) ((goal_dist>>16)&0xFF),
                        (byte) ((goal_dist>>24)&0xFF),
                        (byte) (goal_steps&0xFF),
                        (byte) ((goal_steps>>8)&0xFF),
                        (byte) ((goal_steps>>16)&0xFF),
                        (byte) ((goal_steps>>24)&0xFF),
                        (byte) (goal_carlories&0xFF),
                        (byte) ((goal_carlories>>8)&0xFF),
                        (byte) ((goal_carlories>>16)&0xFF),
                        (byte) ((goal_carlories>>24)&0xFF),
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

