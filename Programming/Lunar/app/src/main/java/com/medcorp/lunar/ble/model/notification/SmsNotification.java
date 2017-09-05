package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.GreenLed;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.color.RedLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class SmsNotification extends Notification {

    private static final String ON_OFF_TAG = "sms";
    private final String TAG = "smschoosencolor";

    public SmsNotification() {
        super(false);
    }

    public SmsNotification(boolean state) {
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
        return R.string.notification_sms_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.message_notification;
    }

    @Override
    public NevoLed getDefaultColor() {
        return new RedLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
