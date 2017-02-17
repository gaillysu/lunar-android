package com.medcorp.ble.model.notification.visitor;

import com.medcorp.ble.model.notification.CalendarNotification;
import com.medcorp.ble.model.notification.EmailNotification;
import com.medcorp.ble.model.notification.FacebookNotification;
import com.medcorp.ble.model.notification.Notification;
import com.medcorp.ble.model.notification.SmsNotification;
import com.medcorp.ble.model.notification.TelephoneNotification;
import com.medcorp.ble.model.notification.WeChatNotification;
import com.medcorp.ble.model.notification.WhatsappNotification;

/**
 * Created by Karl on 9/30/15.
 */
public interface NotificationVisitor<T> {

    public T visit(CalendarNotification calendarNotification);
    public T visit(EmailNotification emailNotification);
    public T visit(FacebookNotification facebookNotification);
    public T visit(SmsNotification smsNotification);
    public T visit(TelephoneNotification telephoneNotification);
    public T visit(WeChatNotification weChatNotification);
    public T visit(WhatsappNotification whatsappNotification);
    public T visit(Notification applicationNotification);

}
