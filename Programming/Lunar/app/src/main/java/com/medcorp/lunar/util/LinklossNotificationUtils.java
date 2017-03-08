package com.medcorp.lunar.util;


import android.content.Context;
import android.os.Vibrator;


/**
 * Created by med on 16/9/19.
 */
public class LinklossNotificationUtils {

    public static void sendNotification(Context mContext, boolean connected)
    {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        long[] patternConntected = {1,100};
        long[] patternDisconnect = {1,100,100,100};
        if(vibrator.hasVibrator())
        {
            vibrator.cancel();
        }
        if(connected) {
            vibrator.vibrate(patternConntected, -1);
        }
        else {
            vibrator.vibrate(patternDisconnect, -1);
        }
    }
}
