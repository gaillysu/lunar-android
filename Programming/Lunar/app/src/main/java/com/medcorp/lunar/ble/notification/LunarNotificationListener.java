package com.medcorp.lunar.ble.notification;

/**
 * Created by gaillysu on 15/4/10.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.lunar.ble.datasource.NotificationDataHelper;
import com.medcorp.lunar.ble.model.notification.CalendarNotification;
import com.medcorp.lunar.ble.model.notification.EmailNotification;
import com.medcorp.lunar.ble.model.notification.FacebookNotification;
import com.medcorp.lunar.ble.model.notification.OtherAppNotification;
import com.medcorp.lunar.ble.model.notification.SmsNotification;
import com.medcorp.lunar.ble.model.notification.TelephoneNotification;
import com.medcorp.lunar.ble.model.notification.WeChatNotification;
import com.medcorp.lunar.ble.model.notification.WhatsappNotification;
import com.medcorp.lunar.ble.model.request.LedLightOnOffRequest;
import com.medcorp.lunar.util.Preferences;

import net.medcorp.library.ble.controller.ConnectionController;
import net.medcorp.library.ble.exception.BaseBLEException;
import net.medcorp.library.ble.notification.NotificationCallback;
import net.medcorp.library.ble.util.Optional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class LunarNotificationListener extends NotificationBaseListenerService implements NotificationCallback {

    static Optional<Date> lastNotification = new Optional<Date>();

    final static int TIME_BETWEEN_TWO_NOTIFS = 5000;

    final static int LIGHTTIMES = 3;

    final String TAG = LunarNotificationListener.class.getName();

    private TelephonyManager mTm;

    private CallStateListener mListener;

    private ApplicationModel mApplicationModel;


    // listen incoming call and then send led command to nevo watch
    class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    NotificationDataHelper helper = new NotificationDataHelper(LunarNotificationListener.this);
                    if(helper.getState(new TelephoneNotification()).isOn()) {
                        sendNotification(Preferences.getNotificationColor(LunarNotificationListener.this, new TelephoneNotification(),mApplicationModel).getHexColor());
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //init PhoneStateListener
        mTm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mListener = new CallStateListener();
        mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        Log.w("Karl","notification service onCreate() invoked");
        mApplicationModel = getModel();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.w("Karl","notification service onListenerConnected() invoked");
    }

    private List<String> getAppListbyType(int type)
    {
        return Arrays.asList(getResources().getStringArray(type));
    }
    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        if(statusBarNotification == null) {
            return;
        }
        //add log to debug notifications
        try {
            Bundle bundle = (Bundle)statusBarNotification.getNotification().getClass().getDeclaredField("extras").get(statusBarNotification.getNotification()); //this.getExtras().get("android.title");
            Log.w("Karl","<<<<<<<<<<new notification>>>>>>>>>category:" + statusBarNotification.getNotification().category + ",package:" +statusBarNotification.getPackageName() + ",title:" + bundle.get("android.title")+",text:" + bundle.get("android.text") + ",subtext:"+bundle.get("android.subText") + ",people:"+bundle.get("android.people"));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Notification notification = statusBarNotification.getNotification();
        NotificationDataHelper helper = new NotificationDataHelper(this);
        if (notification != null) {
            if(getAppListbyType(R.array.CALL_APPS).contains(statusBarNotification.getPackageName())) {
                if(helper.getState(new TelephoneNotification()).isOn()) {
                    sendNotification(Preferences.getNotificationColor(LunarNotificationListener.this, new TelephoneNotification(),mApplicationModel).getHexColor());
                }
            }
            else if(getAppListbyType(R.array.SMS_APPS).contains(statusBarNotification.getPackageName())) {
                if(helper.getState(new SmsNotification()).isOn()) {
                    sendNotification(Preferences.getNotificationColor(this, new SmsNotification(),mApplicationModel).getHexColor());
                }
            } else if(getAppListbyType(R.array.EMAIL_APPS).contains(statusBarNotification.getPackageName())){
                if(helper.getState(new EmailNotification()).isOn()) {
                    sendNotification(Preferences.getNotificationColor(this, new EmailNotification(),mApplicationModel).getHexColor());
                }
            } else if(getAppListbyType(R.array.CALENDAR_APPS).contains(statusBarNotification.getPackageName())){
                if(helper.getState(new CalendarNotification()).isOn()) {
                    sendNotification(Preferences.getNotificationColor(this, new CalendarNotification(),mApplicationModel).getHexColor());
                }
            } else if(statusBarNotification.getPackageName().contains("com.facebook") && getAppListbyType(R.array.SOCIAL_APPS).contains(statusBarNotification.getPackageName())){
                if(helper.getState(new FacebookNotification()).isOn()) {
                    sendNotification(Preferences.getNotificationColor(this, new FacebookNotification(),mApplicationModel).getHexColor());
                }
            } else if(statusBarNotification.getPackageName().contains("com.tencent") && getAppListbyType(R.array.SOCIAL_APPS).contains(statusBarNotification.getPackageName())){
                if(helper.getState(new WeChatNotification()).isOn()) {
                    sendNotification(Preferences.getNotificationColor(this, new WeChatNotification(),mApplicationModel).getHexColor());
                }
            } else if(statusBarNotification.getPackageName().contains("com.whatsapp")&& getAppListbyType(R.array.SOCIAL_APPS).contains(statusBarNotification.getPackageName())){
                if(helper.getState(new WhatsappNotification()).isOn()) {
                    sendNotification(Preferences.getNotificationColor(this, new WhatsappNotification(),mApplicationModel).getHexColor());
                }
            } else {
                Log.v(TAG, "Unknown Notification : "+statusBarNotification.getPackageName());
                String appID = statusBarNotification.getPackageName();
                Set<String> appList = helper.getNotificationAppList();
                if(appList.contains(statusBarNotification.getPackageName())){
                    OtherAppNotification otherAppNotification = new OtherAppNotification(appID);
                    if(helper.getState(otherAppNotification).isOn()) {
                        sendNotification(Preferences.getNotificationColor(this, otherAppNotification,mApplicationModel).getHexColor());
                    }
                }
                else {
                    appList.add(appID);
                    helper.setNotificationAppList(appList);
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
    }

    /**
     * @param count :the flash times, total light on times,should double it, means light on/off follow:1.2s on,0.5s off,1.2s on,0.5s off,1.2s on, then off by Nevo self
     * @param ledColor: which led light on
     */
    private void showNotification(final int count,final int ledColor)
    {
        if(count == 0) return;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ConnectionController.Singleton.getInstance(LunarNotificationListener.this, new GattAttributesDataSourceImpl(LunarNotificationListener.this))
                        .sendRequest(new LedLightOnOffRequest(getApplicationContext(), count%2==0?ledColor:0, count%2==0));
                showNotification(count-1,ledColor);
            }
        },count%2==0?(count==LIGHTTIMES*2?0:500):1200); //first time should do right now, here have 0ms
    }
    void sendNotification(final int ledColor) {

        //We can't accept notifications if we just received one X ms ago
        if(lastNotification.notEmpty() && new Date().getTime()-lastNotification.get().getTime() < TIME_BETWEEN_TWO_NOTIFS) return ;

        lastNotification.set(new Date());

        //when OTA doing,discard the notification
        if(ConnectionController.Singleton.getInstance(this, new GattAttributesDataSourceImpl(this)).inOTAMode()){
            return;
        }

        ConnectionController.Singleton.getInstance(this, new GattAttributesDataSourceImpl(this)).connect();

        showNotification(LIGHTTIMES*2,ledColor);
    }

    public static void getNotificationAccessPermission(final Context ctx) {
        ContentResolver contentResolver = ctx.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = ctx.getPackageName();

        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
        {
            // Let's ask the user to enable notifications
            new MaterialDialog.Builder(ctx)
                    .title(R.string.notification_access_title)
                    .content(R.string.notification_access_message)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                        }
                    }).show();
        }
    }

    @Override
    public void onErrorDetected(BaseBLEException e) {
        Log.w("Karl","Couldn't give notification due to bluetooth problems.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
        Log.w("Karl","notification service onDestroy() invoked");
    }
}
