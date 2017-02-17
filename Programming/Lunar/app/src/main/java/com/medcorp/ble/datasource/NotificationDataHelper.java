package com.medcorp.ble.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import com.medcorp.ble.model.notification.Notification;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Karl on 10/6/15.
 */
public class NotificationDataHelper {

    SharedPreferences pref;
    private final static String APPLIST = "applist";

    public NotificationDataHelper(Context context) {
        pref =PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveState(Notification applicationNotification){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(applicationNotification.getOnOffTag(), applicationNotification.isOn());
        editor.commit();
    }

    public Notification getState(Notification applicationNotification){
        applicationNotification.setState(pref.getBoolean(applicationNotification.getOnOffTag(),false));
        return applicationNotification;
    }

    public void setNotificationAppList(Set<String> appList)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(APPLIST,appList);
        editor.commit();
    }
    public Set<String> getNotificationAppList()
    {
        Set<String> appList = pref.getStringSet(APPLIST,new HashSet<String>());
        return appList;
    }

    public static Set<String> getAllPackages(final Context context) {
        final HashSet<String> set = new HashSet<String>();
        final Iterator<PackageInfo> iterator = context.getPackageManager().getInstalledPackages(0).iterator();
        while (iterator.hasNext()) {
            set.add(iterator.next().packageName);
        }
        return set;
    }
}