package com.medcorp.lunar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;

import com.medcorp.lunar.R;

import java.io.FileNotFoundException;

/**
 * Created by Jason on 2016/11/7.
 */

public class PublicUtils {

    public static Bitmap drawCircleView(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        Bitmap bm = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //这里需要先画出一个圆
        canvas.drawCircle(100, 100, 100, paint);
        //圆画好之后将画笔重置一下
        paint.reset();
        //设置图像合成模式，该模式为只在源图像和目标图像相交的地方绘制源图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bm;
    }

    public static Bitmap getBitmap(Uri imageUri, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            bitmap = null;
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String obtainString(Context context, String name, int time) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(": ");
        builder.append(countTime(context, time));
        return builder.toString();

    }

    public static String countTime(Context context, int goalDuration) {
        StringBuffer sb = new StringBuffer();
        if (goalDuration > 60) {
            sb.append(goalDuration / 60 + context.getString(R.string.sleep_unit_hour)
                    + (goalDuration % 60 != 0 ? goalDuration % 60 + context.getString(R.string.sleep_unit_minute) : ""));
        } else if (goalDuration == 60) {
            sb.append(goalDuration / 60 + context.getString(R.string.sleep_unit_hour));
        } else {
            sb.append(goalDuration + context.getString(R.string.sleep_unit_minute));
        }
        return sb.toString();
    }

    public static int[] countTime(int sleepGoal, int hourOfDay, int minuteOfHour, int weekday) {
        int[] countTime = new int[3];
        int hour = sleepGoal / 60;
        int minute = sleepGoal % 60;
        hourOfDay -= hour;
        minuteOfHour -= minute;
        if (minuteOfHour < 0) {
            if (hourOfDay <= 0) {
                hourOfDay = 24 + hourOfDay;
                weekday -= 1;
                minuteOfHour += 60;
            } else {
                hourOfDay -= 1;
                minuteOfHour += 60;
            }
        } else {
            if (hourOfDay <= 0) {
                hourOfDay = 24 + hourOfDay;
                weekday -= 1;
                minuteOfHour += 60;
            } else {
                hourOfDay -= 1;
                minuteOfHour += 60;
            }
        }

        if (weekday == -1) {
            weekday = 6;
        }
        countTime[0] = hourOfDay;
        countTime[1] = minuteOfHour;
        countTime[2] = weekday;
        return countTime;
    }
}
