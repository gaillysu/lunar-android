package com.medcorp.lunar.ble.model.notification;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import com.medcorp.lunar.ble.model.color.NevoLed;
import com.medcorp.lunar.ble.model.color.OrangeLed;
import com.medcorp.lunar.ble.model.notification.visitor.NotificationVisitor;

import java.util.List;

/**
 * Created by Gailly on 12/07/16.
 * why add this Class, because more and more Apps are required to send notifications to watch, MAX 32 apps.
 */
public class OtherAppNotification extends Notification {

    private final String ON_OFF_TAG ;
    private final String TAG ;

    public OtherAppNotification(String appPackageName) {
        super(false);
        ON_OFF_TAG = appPackageName + "_onoff";
        TAG = appPackageName;
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
        return 0;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public NevoLed getDefaultColor() {
        return new OrangeLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getAppName(Context context) {
        String appName = "unknown";
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);
        for(PackageInfo packageInfo:packageInfoList){
            if(packageInfo.packageName.equals(TAG)){
                appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                break;
            }
        }
        return appName;
    }
    public Drawable getAppIcon(Context context) {
        Drawable appIcon = null;
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);
        for(PackageInfo packageInfo:packageInfoList){
            if(packageInfo.packageName.equals(TAG)){
                appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                break;
            }
        }
        return appIcon;
    }
}
