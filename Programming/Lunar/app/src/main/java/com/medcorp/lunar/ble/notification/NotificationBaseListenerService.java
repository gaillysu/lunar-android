package com.medcorp.lunar.ble.notification;

import android.service.notification.NotificationListenerService;

import com.medcorp.lunar.application.ApplicationModel;

/**
 * Created by Karl on 11/16/15.
 */
public abstract class NotificationBaseListenerService extends NotificationListenerService {
    private ApplicationModel application;

    public ApplicationModel getModel() {

        if (application == null) {
            application = (ApplicationModel) getApplication();
        }
        return application;
    }

}
