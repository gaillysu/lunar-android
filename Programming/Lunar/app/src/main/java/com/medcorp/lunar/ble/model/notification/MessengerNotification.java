package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.DeepSkyBlueLed;
import com.medcorp.lunar.ble.model.color.LightGreenLed;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class MessengerNotification extends Notification {

    private static final String ON_OFF_TAG = "messenger";
    private final String TAG = "messengerchoosencolor";


    public MessengerNotification() {
        super(false);
    }

    public MessengerNotification(boolean state) {
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
        return R.string.notification_messenger_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.messenger;
    }

    @Override
    public NevoLed getDefaultColor() {
        return new DeepSkyBlueLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
