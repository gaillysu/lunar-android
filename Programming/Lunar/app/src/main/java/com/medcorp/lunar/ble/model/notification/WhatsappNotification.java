package com.medcorp.lunar.ble.model.notification;


import com.medcorp.lunar.R;
import com.medcorp.lunar.ble.model.color.LightGreenLed;
import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class WhatsappNotification extends Notification {

    private static final String ON_OFF_TAG = "whatsapp";
    private final String TAG = "whatsappchoosencolor";


    public WhatsappNotification() {
        super(false);
    }

    public WhatsappNotification(boolean state) {
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
        return R.string.notification_whatsapp_title;
    }

    @Override
    public int getImageResource() {
        return R.drawable.whatsapp_notification;
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
