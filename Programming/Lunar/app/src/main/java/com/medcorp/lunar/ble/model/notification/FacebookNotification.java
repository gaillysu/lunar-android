package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.BlueLed;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

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
