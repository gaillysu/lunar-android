package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.LightGreenLed;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class QQNotification extends Notification {

    private static final String ON_OFF_TAG = "qqmobile";
    private final String TAG = "qqmobilechoosencolor";


    public QQNotification() {
        super(false);
    }

    public QQNotification(boolean state) {
        super(state);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getOnOffTag() {
        return ON_OFF_TAG;
    }

    @Override
    public int getStringResource() {
        return R.string.notification_qq_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.qq;
    }

    @Override
    public NevoLed getDefaultColor() {
        return new LightGreenLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
