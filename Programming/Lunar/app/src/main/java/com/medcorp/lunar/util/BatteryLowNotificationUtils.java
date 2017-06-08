package com.medcorp.lunar.util;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import com.medcorp.lunar.R;


/**
 * Created by med on 17/6/8.
 */
public class BatteryLowNotificationUtils {

    public static void sendNotification(Context context)
    {
        NotificationManager nftm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String  title = context.getResources().getString(R.string.battery_low_notification_title);
        String  message = context.getResources().getString(R.string.battery_low_notification_message);
        Notification notification = new Notification.Builder(context).setContentTitle(title).setContentText(message).build();
        notification.defaults = Notification.DEFAULT_VIBRATE;
        nftm.notify(1, notification);
    }
}
