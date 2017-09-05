package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.GoldLed;
import com.medcorp.lunar.ble.model.color.LightGreenLed;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class TwitterNotification extends Notification {

    private static final String ON_OFF_TAG = "twitter";
    private final String TAG = "twitterchoosencolor";


    public TwitterNotification() {
        super(false);
    }

    public TwitterNotification(boolean state) {
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
        return R.string.notification_twitter_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.twitter;
    }

    @Override
    public NevoLed getDefaultColor() {
        return new GoldLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
