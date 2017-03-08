package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.color.OrangeLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class TelephoneNotification extends Notification {


    private static final String ON_OFF_TAG = "tele";
    private final String TAG = "telechoosencolor";

    public TelephoneNotification() {
        super(false);
    }

    public TelephoneNotification(boolean state) {
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
        return R.string.notification_call_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.call_notification;
    }

    @Override
    public NevoLed getDefaultColor() {
        return new OrangeLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
