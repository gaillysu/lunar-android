package com.medcorp.lunar.ble.controller;

import android.content.Context;

import com.medcorp.lunar.ble.model.goal.NumberOfStepsGoal;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.WatchInformation;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * this class define some functions for communication with Nevo,
 * all UI activity or Fragment should use this interface,
 * Usage in  Activity or Fragment
 *
 * SyncController  sync = SyncController.Factory.getInstance(this)
 *  "this"  object should implement OnSyncControllerListener, mostly "this" object is a activity, it also is
 *  a Fragment. such as  GoalFragment
 *
 *  step1: sync.startConnect(true,this); // step1 should be called when the app start or user press "connect"
 *                                       //only step1 connected, can do step2, otherwise, step2 will return without sending request.
 *  step2: sync.setStepsGoal(new NumberOfStepsGoal(10000))
 *
 *
 * @author Gaillysu
 *
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 *
 */
public interface SyncController {

    /*
    return SyncController 's Context , mostly it is a activity, such as MainActivity or OTA activity
     */
	public Context getContext();

    /*
    start Connect Nevo
    input:forceScan, if true,do scanning before connect (will forget old UDID, and save new UUID when connected)
    if false, connect the saved UDID 's Nevo
     */
	void startConnect(boolean forceScan);

    /*
    return Nevo connect true or false
     */
	public boolean isConnected();

    /*
    set Steps StepsGoal
    inputL goal =  new NumberOfStepsGoal(XXXX)
     */
	public void setGoal( NumberOfStepsGoal goal);

    public void setAlarm( Alarm alarm);
    /*
      return Nevo 's current daily step count and step StepsGoal, refresh mainhome's Clock screen.
     */
    public void getStepsAndGoal();

    public void showMessage(int titleID, int msgID);

    public String getFirmwareVersion();
    public String getSoftwareVersion();
    /**
     get battery level
    */
    public void getBatteryLevel();

    /**
     * when mainActivity goes to background, set true, otherwise set false
     */
	public void setVisible(boolean isVisible);

    /**
     * forget saved Nevo device
     */
    public void forgetDevice();

    /**
     * find Nevo device, when got found out, light on all color LED
     */
    public void findDevice();

    /**
     * start sync history
     * @param syncAll :true--sync all records, MAX 7 days, false-- only sync current day record
     */
    public void  getDailyTrackerInfo(boolean syncAll);

    void sendRequest(final BLERequestData request);

    /**
     *
     * @param init ,if true means by syncController invoked to do init sync, false means invoked by application to set value
     */
    public void setNotification(boolean init);

    public WatchInformation getWatchInformation();

    /**
     * for some customize ROM(xiaomi,meizhu...), the notification listener service perhaps got killed, here force system notification manager to restart it
     * use "adb shell dumpsys notification" to look which service is killed.
     */
    public void startNotificationListener();

    public boolean getHoldRequest();
    public void setHoldRequest(boolean holdRequest);

    public int getBluetoothStatus();
    public void setBleConnectTimeout(int timeoutInminutes);
    public void setChargingNotification(byte chargingThreshold,boolean enablePhoneNotification);
    public void setLeftKeyFunction(int function);
}
