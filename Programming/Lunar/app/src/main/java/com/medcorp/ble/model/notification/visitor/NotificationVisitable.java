package com.medcorp.ble.model.notification.visitor;

/**
 * Created by Karl on 10/2/15.
 */
public interface NotificationVisitable {

    public <T> T accept(NotificationVisitor<T> visitor);
}
