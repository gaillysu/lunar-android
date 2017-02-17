package com.medcorp.ble.model.notification;

import com.medcorp.ble.model.notification.visitor.NotificationVisitor;
import com.medcorp.R;
import com.medcorp.ble.model.color.BlueLed;
import com.medcorp.ble.model.color.NevoLed;

/**
 * Created by Karl on 9/30/15.
 */
public class FacebookNotification extends Notification {

    private static final String ON_OFF_TAG = "facebook";
    private final String TAG = "facechoosencolor";

    public FacebookNotification() {
        super(false);
    }

    public FacebookNotification(boolean state) {
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
        return R.string.notification_facebook_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.facebook_notification;
    }


    @Override
    public NevoLed getDefaultColor() {
        return new BlueLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
