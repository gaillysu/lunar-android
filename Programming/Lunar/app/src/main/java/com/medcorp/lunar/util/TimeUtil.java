package com.medcorp.lunar.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by karl-john on 18/8/2016.
 */

public class TimeUtil {

    public static String formatTime(int walkDuration) {
        StringBuilder activityTime = new StringBuilder();
        if (walkDuration >= 60) {
            if (walkDuration % 60 > 0) {
                activityTime.append((walkDuration / 60)).append("h");
                activityTime.append(walkDuration % 60).append("m");
            }
            else {
                activityTime.append((walkDuration / 60)).append("h");
            }
        } else {
            activityTime.append(walkDuration).append(" min");
        }
        return activityTime.toString();
    }

    public static Date getTime(Date date) {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        return calBeginning.getTime();
    }
}
