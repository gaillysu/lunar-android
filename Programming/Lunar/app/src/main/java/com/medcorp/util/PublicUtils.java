package com.medcorp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;

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
}
