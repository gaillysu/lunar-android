package com.medcorp.lunar.ble.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.lunar.ble.model.packet.BatteryLevelPacket;
import com.medcorp.lunar.ble.model.packet.ChargingNotificationPacket;
import com.medcorp.lunar.ble.model.packet.DailyStepsPacket;
import com.medcorp.lunar.ble.model.packet.DailyTrackerInfoPacket;
import com.medcorp.lunar.ble.model.packet.DailyTrackerPacket;
import com.medcorp.lunar.ble.model.packet.NewApplicationArrivedPacket;
import com.medcorp.lunar.ble.model.packet.Packet;
import com.medcorp.lunar.ble.model.packet.WatchInfoPacket;
import com.medcorp.lunar.ble.model.request.AddApplicationRequest;
import com.medcorp.lunar.ble.model.request.FindPhoneRequest;
import com.medcorp.lunar.ble.model.request.FindWatchRequest;
import com.medcorp.lunar.ble.model.request.GetBatteryLevelRequest;
import com.medcorp.lunar.ble.model.request.GetStepsGoalRequest;
import com.medcorp.lunar.ble.model.request.ReadDailyTrackerInfoRequest;
import com.medcorp.lunar.ble.model.request.ReadDailyTrackerRequest;
import com.medcorp.lunar.ble.model.request.ReadWatchInfoRequest;
import com.medcorp.lunar.ble.model.request.SetAlarmWithTypeRequest;
import com.medcorp.lunar.ble.model.request.SetBleConnectTimeoutRequest;
import com.medcorp.lunar.ble.model.request.SetChargingNotificationRequest;
import com.medcorp.lunar.ble.model.request.SetGoalRequest;
import com.medcorp.lunar.ble.model.request.SetNotificationRequest;
import com.medcorp.lunar.ble.model.request.SetProfileRequest;
import com.medcorp.lunar.ble.model.request.SetRtcRequest;
import com.medcorp.lunar.ble.model.request.SetSunriseAndSunsetTimeRequest;
import com.medcorp.lunar.ble.model.request.SetWorldTimeOffsetRequest;
import com.medcorp.lunar.ble.model.request.TestModeRequest;
import com.medcorp.lunar.ble.model.request.WriteSettingRequest;
import com.medcorp.lunar.ble.notification.LunarNotificationListener;
import com.medcorp.lunar.database.dao.IDailyHistory;
import com.medcorp.lunar.event.LocationChangedEvent;
import com.medcorp.lunar.event.SetSunriseAndSunsetTimeRequestEvent;
import com.medcorp.lunar.event.Timer10sEvent;
import com.medcorp.lunar.event.bluetooth.BatteryEvent;
import com.medcorp.lunar.event.bluetooth.DigitalTimeChangedEvent;
import com.medcorp.lunar.event.bluetooth.FindWatchEvent;
import com.medcorp.lunar.event.bluetooth.GetWatchInfoChangedEvent;
import com.medcorp.lunar.event.bluetooth.HomeTimeEvent;
import com.medcorp.lunar.event.bluetooth.InitializeEvent;
import com.medcorp.lunar.event.bluetooth.LittleSyncEvent;
import com.medcorp.lunar.event.bluetooth.OnSyncEvent;
import com.medcorp.lunar.event.bluetooth.RequestResponseEvent;
import com.medcorp.lunar.event.bluetooth.SolarConvertEvent;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.Battery;
import com.medcorp.lunar.model.DailyHistory;
import com.medcorp.lunar.model.GoalBase;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.SleepGoal;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.SolarGoal;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.WatchInfomation;
import com.medcorp.lunar.util.BatteryLowNotificationUtils;
import com.medcorp.lunar.util.Common;
import com.medcorp.lunar.util.LinklossNotificationUtils;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.SoundPlayer;

import net.medcorp.library.ble.controller.ConnectionController;
import net.medcorp.library.ble.event.BLEConnectionStateChangedEvent;
import net.medcorp.library.ble.event.BLEPairStateChangedEvent;
import net.medcorp.library.ble.event.BLEResponseDataEvent;
import net.medcorp.library.ble.exception.BLEConnectTimeoutException;
import net.medcorp.library.ble.exception.BLENotSupportedException;
import net.medcorp.library.ble.exception.BLEUnstableException;
import net.medcorp.library.ble.exception.BaseBLEException;
import net.medcorp.library.ble.exception.BluetoothDisabledException;
import net.medcorp.library.ble.exception.QuickBTSendTimeoutException;
import net.medcorp.library.ble.exception.QuickBTUnBindException;
import net.medcorp.library.ble.exception.visitor.BLEExceptionVisitor;
import net.medcorp.library.ble.kernel.MEDBT;
import net.medcorp.library.ble.model.request.BLERequestData;
import net.medcorp.library.ble.model.response.BLEResponseData;
import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.Constants;
import net.medcorp.library.ble.util.HexUtils;
import net.medcorp.library.ble.util.Optional;
import net.medcorp.library.ble.util.QueuedMainThreadHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.functions.Consumer;
import io.realm.Realm;

public class SyncControllerImpl implements SyncController, BLEExceptionVisitor<Void> {
    private final static String TAG = "SyncControllerImpl";

    private Context mContext;

    private static final int SYNC_INTERVAL = 1 * 30 * 60 * 1000; //every half hour , do sync when connected again

    private ConnectionController connectionController;
    private static TimeZone localTimeZone = TimeZone.getDefault();

    private List<MEDRawData> packetsBuffer = new ArrayList<MEDRawData>();

    private List<DailyHistory> savedDailyHistory = new ArrayList<DailyHistory>();
    private int mCurrentDay = 0;
    private int mTimeOutcount = 0;
    private long mLastPressAkey = 0;
    private boolean mSyncAllFlag = false;
    private boolean initAlarm = true;
    private boolean initNotification = true;
    private boolean isHoldRequest = false;
    private boolean isPendingsunriseAndsunset = true;
    private final Object lockObject = new Object();
    private boolean setSunriseAndSunsetSuccess = false;
    //IMPORT!!!!, every get connected, will do sync profile data and activity data with Nevo
    //it perhaps long time(sync activity data perhaps need long time, MAX total 7 days)
    //so before sync finished, disable setStepsGoal/setAlarm/getGoalSteps
    //make sure  the whole received packets
    private Realm realm;
    //start a timer to do little sync, refresh dashboard @LittleSyncEvent
    private long LITTLE_SYNC_INTERVAL = 10000L; //10s
    private Timer autoSyncTimer = null;

    private void startTimer() {
        if (autoSyncTimer != null)
            autoSyncTimer.cancel();
        autoSyncTimer = new Timer();
        realm = Realm.getDefaultInstance();
        autoSyncTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //here do little sync to refesh current steps
                getStepsAndGoal();
                //for new Lunar UI, query adc every 10s to show solar charging status
                sendRequest(new TestModeRequest(mContext, TestModeRequest.MODE_F4));

                //send a 10s event to refresh clock
                EventBus.getDefault().post(new Timer10sEvent());
            }
        }, LITTLE_SYNC_INTERVAL, LITTLE_SYNC_INTERVAL);
    }

    public SyncControllerImpl(Context context) {
        mContext = context;
        connectionController = ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context));
        Intent intent = new Intent(mContext, LocalService.class);
        mContext.getApplicationContext().bindService(intent, mCurrentServiceConnection, Activity.BIND_AUTO_CREATE);
        //force android Notification Manager Service to start LunarNotificationListener
        startNotificationListener();
        EventBus.getDefault().register(this);
        startTimer();
    }

    @Override
    public void startConnect(boolean forceScan) {
        if (forceScan) {
            connectionController.forgetSavedAddress();
        }
        connectionController.scan();
    }

    //Each packets should go through this function. It will ensure that they are properly queued and sent in order.
    @Override
    public void sendRequest(final BLERequestData request) {
        if (connectionController.inOTAMode()) {
            return;
        }
        if (!isConnected()) {
            return;
        }
        //when OTA mode, disable syncController send any request command to BLE
        if (isHoldRequest) {
            return;
        }
        QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).post(new Runnable() {
            @Override
            public void run() {
                Log.i(MEDBT.TAG, request.getClass().getName());
                connectionController.sendRequest(request);
            }
        });

    }

    @Override
    public void setNotification(boolean init) {
        //TODO: lunar notification use med-library ???
    }

    @Override
    public WatchInfomation getWatchInfomation() {
        WatchInfomation watchInfomation = new WatchInfomation();
        watchInfomation.setWatchID((byte) Preferences.getWatchId(mContext));
        watchInfomation.setWatchModel((byte) Preferences.getWatchModel(mContext));
        return watchInfomation;
    }

    @Subscribe
    public void onEvent(BLEResponseDataEvent eventData) {
        BLEResponseData data = eventData.getData();
        if (data.getType().equals(MEDRawData.TYPE)) {
            final MEDRawData lunarData = (MEDRawData) data;
            packetsBuffer.add(lunarData);

            if ((byte) 0xFF == lunarData.getRawData()[0]) {
                Packet packet = new Packet(packetsBuffer);
                //if packets invaild, discard them, and reset buffer
                if (!packet.isVaildPackets()) {
                    Log.e("Nevo Error", "InVaild Packets Received!");
                    packetsBuffer.clear();
                    QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();
                    return;
                }

                if ((byte) SetRtcRequest.HEADER == lunarData.getRawData()[1]) {
                    //setp2:start set user profile
                    sendRequest(new SetProfileRequest(mContext, ((ApplicationModel) mContext).getUser()));
                } else if ((byte) SetProfileRequest.HEADER == lunarData.getRawData()[1]) {
                    //step3:WriteSetting
                    sendRequest(new WriteSettingRequest(mContext));
                } else if ((byte) WriteSettingRequest.HEADER == lunarData.getRawData()[1]) {
                    EventBus.getDefault().post(new InitializeEvent(InitializeEvent.INITIALIZE_STATUS.END));
                    //because we have a "SyncController" queue to send request, so here we can send many requests one by one.
                    //step4: sync notifications, app-->watch
                    setNotification(true);
                    //step5:sync alarms, app-->watch
                    //firstly clean all alarms
                    resetAllAlarms();
                    ((ApplicationModel) mContext).getAllAlarm(new SyncAlarmToWatchListener() {
                        @Override
                        public void syncAlarmToWatch(List<Alarm> alarms) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            for (Alarm alarm : alarms) {
                                if ((alarm.getWeekDay() & 0x80) == 0x80) {
                                    //NOTICE: here we only disable it in the watch, but keep its status(on or off) in the app.
                                    if (alarm.getAlarmNumber() >= 13) {
                                        //discard today 's sleep alarm when it comes in active mode (BT connected)
                                        if ((alarm.getWeekDay() & 0x0F) == calendar.get(Calendar.DAY_OF_WEEK)
                                                && (alarm.getHour() < calendar.get(Calendar.HOUR_OF_DAY) || (alarm.getHour() == calendar.get(Calendar.HOUR_OF_DAY) && alarm.getMinute() < calendar.get(Calendar.MINUTE)))) {
                                            continue;
                                        }
                                        //discard yesterday 's sleep alarm when it comes in active mode (BT connected)
                                        else if ((((alarm.getWeekDay() & 0x0F) + 1) == calendar.get(Calendar.DAY_OF_WEEK))
                                                || ((alarm.getWeekDay() & 0x0F) == 7 && calendar.get(Calendar.DAY_OF_WEEK) == 1)) {
                                            continue;
                                        }
                                    }
                                    setAlarm(alarm);
                                }
                            }
                            //step6: start sync data, nevo-->phone
                            syncActivityData();
                        }
                    });
                } else if ((byte) ReadWatchInfoRequest.HEADER == lunarData.getRawData()[1]) {
                    //get watch ID and model type
                    WatchInfoPacket watchInfoPacket = new WatchInfoPacket(packet.getPackets());
                    //save watch infomation into preference
                    Preferences.setWatchId(mContext, watchInfoPacket.getWatchID());
                    Preferences.setWatchModel(mContext, watchInfoPacket.getWatchModel());
                    EventBus.getDefault().post(new GetWatchInfoChangedEvent(getWatchInfomation()));
                } else if ((byte) ReadDailyTrackerInfoRequest.HEADER == lunarData.getRawData()[1]) {
                    if (!mSyncAllFlag) {
                        DailyTrackerInfoPacket infopacket = new DailyTrackerInfoPacket(packet.getPackets());
                        mCurrentDay = 0;
                        savedDailyHistory = infopacket.getDailyTrackerInfo();
                        if (!savedDailyHistory.isEmpty()) {
                            mSyncAllFlag = true;
                            getDailyTracker(mCurrentDay);
                        }
                    }
                } else if ((byte) ReadDailyTrackerRequest.HEADER == lunarData.getRawData()[1]) {
                    DailyTrackerPacket thispacket = new DailyTrackerPacket(packet.getPackets());

                    if (savedDailyHistory.isEmpty()) {
                        mCurrentDay = 0;
                        savedDailyHistory.add(mCurrentDay, new DailyHistory(thispacket.getDate()));
                    } else {
                        savedDailyHistory.get(mCurrentDay).setDate(thispacket.getDate());
                    }
                    savedDailyHistory.get(mCurrentDay).setTotalSteps(thispacket.getDailySteps());
                    savedDailyHistory.get(mCurrentDay).setHourlySteps(thispacket.getHourlySteps());
                    //                        Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Steps:" + savedDailyHistory.get(mCurrentDay).getHourlySteps().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalSleepTime(thispacket.getTotalSleepTime());
                    savedDailyHistory.get(mCurrentDay).setHourlySleepTime(thispacket.getHourlySleepTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Sleep time:" + savedDailyHistory.get(mCurrentDay).getTotalSleepTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Sleep time:" + savedDailyHistory.get(mCurrentDay).getHourlySleepTime().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalWakeTime(thispacket.getTotalWakeTime());
                    savedDailyHistory.get(mCurrentDay).setHourlyWakeTime(thispacket.getHourlyWakeTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily Wake time:" + savedDailyHistory.get(mCurrentDay).getTotalWakeTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Wake time:" + savedDailyHistory.get(mCurrentDay).getHourlyWakeTime().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalLightTime(thispacket.getTotalLightTime());
                    savedDailyHistory.get(mCurrentDay).setHourlyLightTime(thispacket.getHourlyLightTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily light time:" + savedDailyHistory.get(mCurrentDay).getTotalLightTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly light time:" + savedDailyHistory.get(mCurrentDay).getHourlyLightTime().toString());


                    savedDailyHistory.get(mCurrentDay).setTotalDeepTime(thispacket.getTotalDeepTime());
                    savedDailyHistory.get(mCurrentDay).setHourlDeepTime(thispacket.getHourlDeepTime());

                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Daily deep time:" + savedDailyHistory.get(mCurrentDay).getTotalDeepTime());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly deep time:" + savedDailyHistory.get(mCurrentDay).getHourlDeepTime().toString());

                    savedDailyHistory.get(mCurrentDay).setTotalDist(thispacket.getTotalDist());
                    savedDailyHistory.get(mCurrentDay).setHourlyDist(thispacket.getHourlyDist());
                    savedDailyHistory.get(mCurrentDay).setTotalCalories(thispacket.getTotalCalories());
                    savedDailyHistory.get(mCurrentDay).setHourlyCalories(thispacket.getHourlyCalories());

                    IDailyHistory history = new IDailyHistory(savedDailyHistory.get(mCurrentDay));
                    //DatabaseHelper.getInstance(mContext).SaveDailyHistory(history);
                    //Log.i(TAG, savedDailyHistory.get(mCurrentDay).getDate().toString() + " successfully saved to database, created = " + history.getCreated());
                    //update steps/sleep tables
                    Steps steps = new Steps(history.getCreated());
                    steps.setDate(Common.removeTimeFromDate(savedDailyHistory.get(mCurrentDay).getDate()).getTime());
                    steps.setUserID(((ApplicationModel) mContext).getUser().getUserID());
                    steps.setSteps(history.getSteps());
                    steps.setCalories((int) history.getCalories());
                    steps.setDistance((int) history.getDistance());
                    steps.setHourlyCalories(history.getHourlycalories());
                    steps.setHourlyDistance(history.getHourlydistance());
                    steps.setHourlySteps(history.getHourlysteps());

                    steps.setGoal(thispacket.getStepsGoal());
                    steps.setWalkSteps(thispacket.getDailyWalkSteps());
                    steps.setRunSteps(thispacket.getDailyRunSteps());
                    steps.setWalkDistance(thispacket.getDailyWalkDistance());
                    steps.setRunDistance(thispacket.getDailyRunDistance());
                    steps.setWalkDuration(thispacket.getDailyWalkDuration());
                    steps.setRunDuration(thispacket.getDailyRunDuration());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "total Steps:" + steps.getSteps());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "Hourly Steps:" + steps.getHourlySteps());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "total active time: " + (steps.getWalkDuration() + steps.getRunDuration()) + ",walk time: " + steps.getWalkDuration() + ",run time: " + steps.getRunDuration());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "total distance: " + steps.getDistance() + ",walk distance: " + steps.getWalkDistance() + ",run distance: " + steps.getRunDistance());
                    Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "total calories: " + steps.getCalories());
                    try {
                        steps.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(steps.getDate()))).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //update  the day 's "steps" table
                    //why here add if(), because sometimes 0x24 cmd returns duplicated date(this should be a bug of firmware), the next record will override the previous record with the same date
                    if (steps.getSteps() > 0) {
                        ((ApplicationModel) mContext).saveDailySteps(steps);
                    }
                    //for maintaining data consistency,here save sleep
                    final Sleep sleep = new Sleep(history.getCreated());
                    sleep.setDate(Common.removeTimeFromDate(savedDailyHistory.get(mCurrentDay).getDate()).getTime());
                    sleep.setHourlySleep(history.getHourlySleepTime());
                    sleep.setHourlyWake(history.getHourlyWakeTime());
                    sleep.setHourlyLight(history.getHourlyLightTime());
                    sleep.setHourlyDeep(history.getHourlDeepTime());
                    sleep.setTotalSleepTime(history.getTotalSleepTime());
                    sleep.setTotalWakeTime(history.getTotalWakeTime());
                    sleep.setTotalLightTime(history.getTotalLightTime());
                    sleep.setTotalDeepTime(history.getTotalDeepTime());
                    //firstly reset sleep start/end time is 0, it means the day hasn't been calculate sleep analysis.
                    sleep.setStart(0);
                    sleep.setEnd(0);
                    ((ApplicationModel) mContext).getSleepGoalDatabseHelper().getSelectedGoal().subscribe(new Consumer<SleepGoal>() {
                        @Override
                        public void accept(SleepGoal sleepGoal) throws Exception {
                            sleep.setGoal(sleepGoal.getGoalDuration());
                        }
                    });

                    sleep.setUserID(((ApplicationModel) mContext).getUser().getUserID());
                    try {
                        sleep.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(sleep.getDate()))).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //why here add if(), because sometimes 0x24 cmd returns duplicated date(this should be a bug of firmware), the next record will override the previous record with the same date
                    if (sleep.getTotalSleepTime() > 0) {
                        ((ApplicationModel) mContext).saveDailySleep(sleep);
                    }
                    //end update
                    //here save solar time to local database when watch ID>1
                    if (getWatchInfomation().getWatchID() > 1) {
                        final Solar solar = new Solar(new Date(history.getCreated()));
                        solar.setDate((Common.removeTimeFromDate(solar.getCreatedDate())).getTime());
                        solar.setUserId(Integer.parseInt(((ApplicationModel) mContext).getUser().getUserID()));
                        solar.setTotalHarvestingTime(thispacket.getSolarHarvestingTime());
                        solar.setHourlyHarvestingTime(thispacket.getHourlyHarvestTime().toString());
                        ((ApplicationModel) mContext).getSolarGoalDatabaseHelper().getSelectedGoal().subscribe(new Consumer<SolarGoal>() {
                            @Override
                            public void accept(SolarGoal solarGoal) throws Exception {
                                solar.setGoal(solarGoal.getTime());
                            }
                        });
                        Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "hourly solar time:" + solar.getHourlyHarvestingTime());
                        Log.i(savedDailyHistory.get(mCurrentDay).getDate().toString(), "total solar time:" + solar.getTotalHarvestingTime());
                        ((ApplicationModel) mContext).getSolarDatabaseHelper().update(solar);
                    }
                    mCurrentDay++;
                    if (mCurrentDay < savedDailyHistory.size() && mSyncAllFlag) {
                        getDailyTracker(mCurrentDay);
                    } else {
                        mCurrentDay = 0;
                        //DatabaseHelper.outPutDatabase(mContext);
                        syncFinished();
                    }
                } else if ((byte) TestModeRequest.MODE_F4 == packet.getHeader()) {
                    int battery_adc = HexUtils.bytesToInt(new byte[]{packet.getPackets().get(0).getRawData()[2], packet.getPackets().get(0).getRawData()[3]});
                    int pv_adc = HexUtils.bytesToInt(new byte[]{packet.getPackets().get(0).getRawData()[4], packet.getPackets().get(0).getRawData()[5]});
                    float battery_voltage = ((battery_adc + 1) * 360f / 1024) / 100;
                    float pv_voltage = ((pv_adc + 1) * 360f / 1024) / 100;
                    Log.i(TAG, "battery_adc= " + battery_adc + ",battery_voltage= " + battery_voltage + ",pv_adc= " + pv_adc + ",pv_voltage= " + pv_voltage);
                    EventBus.getDefault().post(new SolarConvertEvent(pv_adc));
                } else if ((byte) FindPhoneRequest.HEADER == lunarData.getRawData()[1]) {
                    if (mLocalService != null) {
                        mLocalService.findCellPhone();
                        //let all LED light on, means that find CellPhone is successful.
                        sendRequest(new TestModeRequest(mContext, 0xFFFFFF, false, TestModeRequest.MODE_F0));
                    }
                } else if ((byte) GetStepsGoalRequest.HEADER == lunarData.getRawData()[1]) {
                    //save current day's step count to "Steps" table
                    final DailyStepsPacket stepPacket = new DailyStepsPacket(packet.getPackets());
                    Log.i(TAG, "little sync,Date:" + stepPacket.getDailyDate().toString() + ",steps:" + stepPacket.getDailySteps() + ",goal:" + stepPacket.getDailyStepsGoal());
                    Steps steps = ((ApplicationModel) mContext).getDailySteps(((ApplicationModel) mContext)
                            .getUser().getUserID(), Common.removeTimeFromDate(new Date()));
                    steps.setCreatedDate(new Date().getTime());
                    steps.setDate(Common.removeTimeFromDate(new Date()).getTime());
                    steps.setSteps(stepPacket.getDailySteps());
                    steps.setGoal(stepPacket.getDailyStepsGoal());
                    steps.setUserID(((ApplicationModel) mContext).getUser().getUserID());
                    //I can't calculator these value from this packet, they should come from CMD 0x25 cmd
                    ((ApplicationModel) mContext).saveDailySteps(steps);
                    //end save
                } else if ((byte) NewApplicationArrivedPacket.HEADER == lunarData.getRawData()[1]) {
                    NewApplicationArrivedPacket newApplicationArrivedPacket = new NewApplicationArrivedPacket(packet.getPackets());
                    Log.i(TAG, "new Application arrived,total:" + newApplicationArrivedPacket.getTotalApplications());
                    Log.i(TAG, "new Application arrived,ID: " + newApplicationArrivedPacket.getApplicationInfomation().getData());
                    //TODO send AddApplicationRequest ???
                    sendRequest(new AddApplicationRequest(mContext, newApplicationArrivedPacket.getApplicationInfomation()));
                }
                //process done(such as save local db), then notify top layer to get or refresh screen
                if (packet.getHeader() == (byte) GetStepsGoalRequest.HEADER) {
                    EventBus.getDefault().post(new LittleSyncEvent(true));
                } else if ((byte) GetBatteryLevelRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new BatteryEvent(new Battery(new BatteryLevelPacket(packet.getPackets()).getBatteryLevel(),new BatteryLevelPacket(packet.getPackets()).getBatteryCapacity())));
                } else if ((byte) FindWatchRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new FindWatchEvent(true));
                } else if ((byte) SetAlarmWithTypeRequest.HEADER == packet.getHeader()
                        || (byte) SetNotificationRequest.HEADER == packet.getHeader()
                        || (byte) SetGoalRequest.HEADER == packet.getHeader()) {
                    EventBus.getDefault().post(new RequestResponseEvent(true));
                } else if (packet.getHeader() == (byte) SetSunriseAndSunsetTimeRequest.HEADER) {
                    setSunriseAndSunsetSuccess = true;
                    // Notify waiting thread
                    synchronized (lockObject) {
                        lockObject.notifyAll();
                    }
                    EventBus.getDefault().post(new SetSunriseAndSunsetTimeRequestEvent(SetSunriseAndSunsetTimeRequestEvent.STATUS.SUCCESS));
                }
                else if ((byte) ChargingNotificationPacket.HEADER == packet.getHeader()) {
                    BatteryLowNotificationUtils.sendNotification(mContext);
                }
                packetsBuffer.clear();
                QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();
            }
        }
    }

    @Subscribe
    public void onEvent(BLEConnectionStateChangedEvent stateChangedEvent) {

        if (stateChangedEvent.isConnected()) {
            mTimeOutcount = 0;
            //step1:setRTC, should defer about 2s for waiting the Callback characteristic enable Notify
            //and wait reading FW version done , then do setRTC.
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    packetsBuffer.clear();
                    //step0:firstly read watch infomation, for Lunar and nevo Solar, we use watch ID to show solar power screen
                    sendRequest(new ReadWatchInfoRequest(mContext));
                    //it is dangerous when every BLE connection invoke setRtc(), perhaps it will reset some data, so we should invoke setRTC() once time(only get paired with the watch)
                    //                    if (mLocalService.getPairedWatch() != null && mLocalService.getPairedWatch().equals(connectionController.getSaveAddress())) {
                    //                        mLocalService.setPairedWatch(null);
                    //                        Log.w(TAG, "SET RTC");
                    //                        //step 1: setRTC every connection
                    //                        setRtc();
                    //                    } else {
                    //                        //directly do steps 2 without setRTC
                    //                        //setp2:start set user profile
                    //                        sendRequest(new SetProfileRequest(mContext, ((ApplicationModel) mContext).getUser()));
                    //                    }
                    setRtc();
                    setWorldClockOffset();
                    if (isPendingsunriseAndsunset) {
                        //when get local location, will calculate sunrise and sunset time and send it to watch
                        EventBus.getDefault().post(new SetSunriseAndSunsetTimeRequestEvent(SetSunriseAndSunsetTimeRequestEvent.STATUS.START));
                    }
                }
            }, 2000);
        } else {
            QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).clear();
            packetsBuffer.clear();
            mSyncAllFlag = false;
            mCurrentDay = 0;
        }
        if (Preferences.getLinklossNotification(mContext)) {
            LinklossNotificationUtils.sendNotification(mContext, stateChangedEvent.isConnected());
        }
    }

    @Subscribe
    public void onEvent(LocationChangedEvent event) {
        com.luckycatlabs.sunrisesunset.dto.Location sunriseLocation =
                new com.luckycatlabs.sunrisesunset.dto.Location(event.getLocation().getLatitude() + "", event.getLocation().getLongitude() + "");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(sunriseLocation, localTimeZone.getID());
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        byte sunriseHour = (byte) Integer.parseInt(officialSunrise.split(":")[0]);
        byte sunriseMin = (byte) Integer.parseInt(officialSunrise.split(":")[1]);
        byte sunsetHour = (byte) Integer.parseInt(officialSunset.split(":")[0]);
        byte sunsetMin = (byte) Integer.parseInt(officialSunset.split(":")[1]);
        if (isConnected()) {
            setSunriseAndSunsetSuccess = false;
            sendRequest(new SetSunriseAndSunsetTimeRequest(mContext, sunriseHour, sunriseMin, sunsetHour, sunsetMin));
            //wait for maximum 2s, if time out-->send failed event
            synchronized (lockObject) {
                try {
                    lockObject.wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!setSunriseAndSunsetSuccess) {
                isPendingsunriseAndsunset = true;
                EventBus.getDefault().post(new SetSunriseAndSunsetTimeRequestEvent(SetSunriseAndSunsetTimeRequestEvent.STATUS.FAILED));
            } else {
                isPendingsunriseAndsunset = false;
            }
        } else {
            //pending it and send it when got connected next time.
            isPendingsunriseAndsunset = true;
        }
    }

    @Subscribe
    public void onEvent(BLEPairStateChangedEvent stateChangedEvent) {
        if (stateChangedEvent.getPairState() == BluetoothDevice.BOND_BONDED) {
            mLocalService.setPairedWatch(stateChangedEvent.getAddress());
        } else if (stateChangedEvent.getPairState() == BluetoothDevice.BOND_NONE) {
            mLocalService.setPairedWatch(null);
        }
    }

    @Subscribe
    public void onEvent(HomeTimeEvent homeTimeEvent) {
        setWorldClockOffset();
    }

    @Subscribe
    public void onEvent(DigitalTimeChangedEvent digitalTimeChangedEvent) {
        setWorldClockOffset();
    }

    /**
     * This function will synchronise activity data with the watch.
     * It is a long process and hence shouldn't be done too often, so we save the date of previous sync.
     * The watch should be emptied after all data have been saved.
     */
    private void syncActivityData() {
        long lastSync = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getLong(Constants.LAST_SYNC, 0);
        String lastTimeZone = mContext.getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.LAST_SYNC_TIME_ZONE, "");
        if (Calendar.getInstance().getTimeInMillis() - lastSync > SYNC_INTERVAL
                || !TimeZone.getDefault().getID().equals(lastTimeZone)) {
            Log.i(TAG, "*** Sync started ! ***");
            EventBus.getDefault().post(new OnSyncEvent(OnSyncEvent.SYNC_EVENT.STARTED));

            getDailyTrackerInfo(true);
        } else {
            Log.i(TAG, "*** Sync step count and goal ***");
            getStepsAndGoal();
        }
    }

    private void syncFinished() {
        Log.i(TAG, "*** Sync finished ***");
        if (mSyncAllFlag) {
            mSyncAllFlag = false;
            EventBus.getDefault().post(new OnSyncEvent(OnSyncEvent.SYNC_EVENT.STOPPED));
        } else {
            EventBus.getDefault().post(new OnSyncEvent(OnSyncEvent.SYNC_EVENT.TODAY_SYNC_STOPPED));
        }
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, Calendar.getInstance().getTimeInMillis()).commit();
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putString(Constants.LAST_SYNC_TIME_ZONE, TimeZone.getDefault().getID()).commit();
        //tell history to refresh
    }

    private void setWorldClockOffset() {
        float timeZoneOffset = 0f;
        //when user select home city Time
        if (Preferences.getPlaceSelect(mContext)) {
            TimeZone timezone = TimeZone.getTimeZone(Preferences.getHomeTimezoneId(mContext));
            float timeHomeZoneOffset = timezone.getRawOffset() / 3600f / 1000f;

            TimeZone localTimezone = TimeZone.getDefault();
            float timeLocalZoneOffset = localTimezone.getRawOffset() / 3600f / 1000f;

            if (timeLocalZoneOffset > timeHomeZoneOffset) {
                timeZoneOffset = 24f - (timeLocalZoneOffset - timeHomeZoneOffset);
            } else {
                timeZoneOffset = timeHomeZoneOffset - timeLocalZoneOffset;
            }
        }
        Log.i(TAG, "@HomeTimeEvent,set world time offset,offset:" + timeZoneOffset);
        sendRequest(new SetWorldTimeOffsetRequest(mContext, timeZoneOffset));
    }

    private void setRtc() {
        sendRequest(new SetRtcRequest(mContext));
    }

    @Override
    public void getDailyTrackerInfo(boolean syncAll) {
        if (syncAll) {
            sendRequest(new ReadDailyTrackerInfoRequest(mContext));
        } else if (!mSyncAllFlag) {
            getDailyTracker(0);
        }
    }

    private void getDailyTracker(int trackerNo) {
        sendRequest(new ReadDailyTrackerRequest(mContext, trackerNo));
    }

    @Override
    public void setGoal(GoalBase goal) {
        sendRequest(new SetGoalRequest(mContext, goal));
    }

    @Override
    public void setAlarm(Alarm alarm) {
        sendRequest(new SetAlarmWithTypeRequest(mContext, alarm));
    }

    private void resetAllAlarms() {
        //pls refer to R12 command 0x41, use 0x225588bb to reset all alarms
        Alarm resetAlarm = new Alarm(0xbb, 0x88, (byte)0x22, "", (byte)0, (byte)0x55);
        setAlarm(resetAlarm);
    }
    @Override
    public void getStepsAndGoal() {
        sendRequest(new GetStepsGoalRequest(mContext));
    }

    @Override
    public void getBatteryLevel() {
        sendRequest(new GetBatteryLevelRequest(mContext));
    }

    /**
     * @ledpattern, define Led light pattern, 0 means off all led, 0xFFFFFF means light on all led( include color and white)
     * 0x7FF means light on all white led (bit0~bit10), 0x3F0000 means light on all color led (bit16~bit21)
     * other value, light on the related led
     * @motorOnOff, vibrator true or flase
     */
    @Override
    public void findDevice() {
        sendRequest(new FindWatchRequest(mContext));
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public boolean isConnected() {
        return connectionController.isConnected();
    }

    @Override
    public String getFirmwareVersion() {
        return connectionController.getBluetoothVersion();
    }

    @Override
    public String getSoftwareVersion() {
        return connectionController.getSoftwareVersion();
    }

    @Override
    public void forgetDevice() {
        //step1:disconnect
        if (connectionController.isConnected()) {
            connectionController.disconnect();
        }
        //step2:unpair this watch from system bluetooth setting
        //connectionController.unPairDevice(connectionController.getSaveAddress());
        mLocalService.setPairedWatch(null);
        //step3:reset MAC address and firstly run flag and big sync stamp
        connectionController.forgetSavedAddress();
        getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG, true).commit();
        //when forget the watch, force a big sync when got connected again
        getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putLong(Constants.LAST_SYNC, 0).commit();
        //reset watch ID and watch model to nevo
        Preferences.setWatchId(mContext, 1);
    }

    @Override
    public void startNotificationListener() {
        //force android NotificationManagerService to rebind user NotificationListenerService
        // http://www.zhihu.com/question/33540416/answer/113706620
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(mContext, LunarNotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(mContext, LunarNotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    @Override
    public void setHoldRequest(boolean holdRequest) {
        isHoldRequest = holdRequest;
    }

    @Override
    public boolean getHoldRequest() {
        return isHoldRequest;
    }

    /**
     * ============The following block is used only to have a safe context in order to display a popup============
     */
    @Override
    public void showMessage(final int titleID, final int msgID) {
        if (mLocalService != null && mVisible) {
            mLocalService.PopupMessage(titleID, msgID);
        }
    }

    private boolean mVisible = false;

    @Override
    public void setVisible(boolean isVisible) {
        mVisible = isVisible;
    }

    //below code added to popup a dialog whenever nevo app runs background or foreground
    private LocalService.LocalBinder mLocalService = null;

    private ServiceConnection mCurrentServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, name + " Service disconnected");
            mLocalService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, name + " Service connected");
            mLocalService = (LocalService.LocalBinder) service;
        }
    };

    @Override
    public Void visit(QuickBTUnBindException e) {
        return null;
    }

    @Override
    public Void visit(BLEConnectTimeoutException e) {
        mTimeOutcount = mTimeOutcount + 1;
        //when reconnect is more than 3, popup message to user to reopen bluetooth or restart smartphone
        if (mTimeOutcount == 3) {
            mTimeOutcount = 0;
            showMessage(R.string.ble_connection_timeout_title, R.string.ble_connect_timeout);
        }
        return null;
    }

    @Override
    public Void visit(final BLENotSupportedException e) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.ble_not_supported, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e2) {

        }
        return null;
    }

    @Override
    public Void visit(final BLEUnstableException e) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.ble_unstable, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e3) {
        }
        return null;
    }

    @Override
    public Void visit(final BluetoothDisabledException e) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.ble_deactivated, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e1) {
        }
        return null;
    }

    @Override
    public Void visit(QuickBTSendTimeoutException e) {
        return null;
    }

    @Override
    public Void visit(BaseBLEException e) {
        return null;
    }

    /*inner class , static type, @link:http://stackoverflow.com/questions/10305261/broadcastreceiver-cant-instantiate-class-no-empty-constructor */
    static public class LocalService extends Service {
        private AlertDialog mAlertDialog = null;
        private Optional<String> pairedBleAddress = new Optional<String>();
        BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.i("LocalService", "Screen On");
                    if (!ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context)).isConnected()) {
                        ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context)).scan();
                    }
                }
                if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                    Log.i("LocalService", "timezone got changed," + TimeZone.getDefault().getDisplayName() + "," + TimeZone.getDefault().getID());
                    localTimeZone = TimeZone.getDefault();
                    //Timezone changed,reconnect watch and will setRTC again by current tomezone time
                    ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context)).disconnect();
                    ConnectionController.Singleton.getInstance(context, new GattAttributesDataSourceImpl(context)).scan();
                }
                if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
                    Log.i("LocalService", "date got changed. set sunrise and sunset");
                    EventBus.getDefault().post(new SetSunriseAndSunsetTimeRequestEvent(SetSunriseAndSunsetTimeRequestEvent.STATUS.START));
                }
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();
            registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED));
            registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_DATE_CHANGED));
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(myReceiver);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return new LocalBinder();
        }

        private void PopupMessage(final int titleID, final int msgID) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void run() {
                    if (mAlertDialog != null && mAlertDialog.isShowing()) {
                        return;
                    }
                    AlertDialog.Builder ab = new AlertDialog.Builder(LocalService.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setPositiveButton(getResources().getString(R.string.ble_connection_timeout_help), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Uri uri = Uri.parse(getResources().getString(R.string.ble_connect_timeout_url));
                                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(it);

                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.ble_connection_timeout_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(titleID)
                            .setMessage(msgID);

                    mAlertDialog = ab.create();
                    mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    mAlertDialog.setCancelable(false);
                    mAlertDialog.show();
                }
            });
        }

        public class LocalBinder extends Binder {

            public void PopupMessage(int titleID, int msgID) {
                LocalService.this.PopupMessage(titleID, msgID);
            }

            public void findCellPhone() {
                LocalService.this.findCellPhone();
            }

            public String getPairedWatch() {
                return LocalService.this.pairedBleAddress.notEmpty() ? LocalService.this.pairedBleAddress.get() : null;
            }

            public void setPairedWatch(String address) {
                LocalService.this.pairedBleAddress.set(address);
            }
        }

        private void findCellPhone() {
            Vibrator vibrator = (Vibrator) LocalService.this.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1, 2000, 1000, 2000, 1000, 2000, 1000};
            if (vibrator.hasVibrator()) {
                vibrator.cancel();
            }
            vibrator.vibrate(pattern, -1);

            PowerManager pm = (PowerManager) LocalService.this.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();

            //play ring bell to alert user that phone is here
            new SoundPlayer(this).startPlayer(R.raw.bell);
        }

    }

    @Override
    public int getBluetoothStatus() {
        return connectionController.getBluetoothStatus();
    }

    @Override
    public void setBleConnectTimeout(int timeoutInminutes) {
        sendRequest(new SetBleConnectTimeoutRequest(mContext,timeoutInminutes));
    }

    @Override
    public void setChargingNotification(byte chargingThreshold, boolean enablePhoneNotification) {
        sendRequest(new SetChargingNotificationRequest(mContext,chargingThreshold,enablePhoneNotification));
    }

    public interface SyncAlarmToWatchListener {
        void syncAlarmToWatch(List<Alarm> alarms);
    }
}
