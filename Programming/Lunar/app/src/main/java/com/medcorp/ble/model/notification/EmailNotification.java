package com.medcorp.ble.model.notification;

import com.medcorp.ble.model.color.NevoLed;
import com.medcorp.R;
import com.medcorp.ble.model.color.YellowLed;
import com.medcorp.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class EmailNotification extends Notification {

    private static final String ON_OFF_TAG = "email";
    private final String TAG = "emailchoosencolor";

    public EmailNotification() {
        super(false);
    }

    public EmailNotification(boolean state) {
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
        return R.string.notification_email_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.email_notification;
    }


    @Override
    public NevoLed getDefaultColor() {
        return new YellowLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
