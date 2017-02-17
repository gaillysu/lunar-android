package com.medcorp.ble.dfu;

/**
 * Created by med on 16/10/12.
 */


import android.app.Activity;

import com.medcorp.activity.NotificationActivity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
		/*
		 * As a target activity the NotificationActivity is returned, not the MainActivity. This is because the notification must create a new task:
		 *
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 *
		 * when user press it. Using NotificationActivity we can check whether the new activity is a root activity (that means no other activity was open before)
		 * or that there is other activity already open. In the later case the notificationActivity will just be closed. System will restore the previous activity.
		 * However if the application has been closed during upload and user click the notification a NotificationActivity will be launched as a root activity.
		 * It will create and start the main activity and terminate itself.
		 *
		 * This method may be used to restore the target activity in case the application was closed or is open. It may also be used to recreate an activity
		 * history (see NotificationActivity).
		 */
        return NotificationActivity.class;
    }

    @Override
    protected boolean isDebug() {
        // return BuildConfig.DEBUG;
        return true;
    }
}
