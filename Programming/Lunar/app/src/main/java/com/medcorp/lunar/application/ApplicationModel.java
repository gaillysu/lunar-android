package com.medcorp.lunar.application;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.gson.Gson;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.EditAlarmActivity;
import com.medcorp.lunar.ble.controller.OtaControllerImpl;
import com.medcorp.lunar.ble.controller.SyncController;
import com.medcorp.lunar.ble.controller.SyncControllerImpl;
import com.medcorp.lunar.ble.model.color.LedLamp;
import com.medcorp.lunar.ble.model.goal.NumberOfStepsGoal;
import com.medcorp.lunar.cloud.CloudSyncManager;
import com.medcorp.lunar.database.LunarAllModules;
import com.medcorp.lunar.database.entry.AlarmDatabaseHelper;
import com.medcorp.lunar.database.entry.GoalDatabaseHelper;
import com.medcorp.lunar.database.entry.LedLampDatabase;
import com.medcorp.lunar.database.entry.SleepDatabaseHelper;
import com.medcorp.lunar.database.entry.SleepGoalDatabaseHelper;
import com.medcorp.lunar.database.entry.SolarDatabaseHelper;
import com.medcorp.lunar.database.entry.SolarGoalDatabaseHelper;
import com.medcorp.lunar.database.entry.StepsDatabaseHelper;
import com.medcorp.lunar.database.entry.UserDatabaseHelper;
import com.medcorp.lunar.event.LocationChangedEvent;
import com.medcorp.lunar.event.ReturnUserInfoEvent;
import com.medcorp.lunar.event.SetSunriseAndSunsetTimeRequestEvent;
import com.medcorp.lunar.event.WeChatEvent;
import com.medcorp.lunar.event.bluetooth.LittleSyncEvent;
import com.medcorp.lunar.event.bluetooth.OnSyncEvent;
import com.medcorp.lunar.event.bluetooth.PositionAddressChangeEvent;
import com.medcorp.lunar.event.google.api.GoogleApiClientConnectionFailedEvent;
import com.medcorp.lunar.event.google.api.GoogleApiClientConnectionSuspendedEvent;
import com.medcorp.lunar.event.google.fit.GoogleFitUpdateEvent;
import com.medcorp.lunar.event.med.MedAddRoutineRecordEvent;
import com.medcorp.lunar.event.med.MedAddSleepRecordEvent;
import com.medcorp.lunar.event.med.MedReadMoreRoutineRecordsModelEvent;
import com.medcorp.lunar.event.med.MedReadMoreSleepRecordsModelEvent;
import com.medcorp.lunar.event.validic.ValidicAddRoutineRecordEvent;
import com.medcorp.lunar.event.validic.ValidicAddSleepRecordEvent;
import com.medcorp.lunar.event.validic.ValidicCreateUserEvent;
import com.medcorp.lunar.event.validic.ValidicDeleteRoutineRecordEvent;
import com.medcorp.lunar.event.validic.ValidicDeleteSleepRecordModelEvent;
import com.medcorp.lunar.event.validic.ValidicException;
import com.medcorp.lunar.event.validic.ValidicUpdateRoutineRecordsModelEvent;
import com.medcorp.lunar.fragment.AnalysisSleepFragment;
import com.medcorp.lunar.fragment.AnalysisSolarFragment;
import com.medcorp.lunar.fragment.AnalysisStepsFragment;
import com.medcorp.lunar.fragment.MainFragment;
import com.medcorp.lunar.fragment.MainSleepFragment;
import com.medcorp.lunar.fragment.WeekData;
import com.medcorp.lunar.googlefit.GoogleFitManager;
import com.medcorp.lunar.googlefit.GoogleFitStepsDataHandler;
import com.medcorp.lunar.googlefit.GoogleFitTaskCounter;
import com.medcorp.lunar.googlefit.GoogleHistoryUpdateTask;
import com.medcorp.lunar.location.LocationController;
import com.medcorp.lunar.model.Alarm;
import com.medcorp.lunar.model.Goal;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.SleepData;
import com.medcorp.lunar.model.Solar;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.model.request.RequestWeChatToken;
import com.medcorp.lunar.network.model.response.ObtainMoreSleepResponse;
import com.medcorp.lunar.network.model.response.ObtainMoreStepsResponse;
import com.medcorp.lunar.network.model.response.WeChatUserInfoResponse;
import com.medcorp.lunar.util.CalendarWeekUtils;
import com.medcorp.lunar.util.Common;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.ToastHelper;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.medcorp.library.ble.controller.OtaController;
import net.medcorp.library.worldclock.WorldClockDatabaseHelper;
import net.medcorp.library.worldclock.WorldClockLibraryModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application {


    /*

    BatteryLevelPacket
    DailyStepsPacket
    DailyTrackerInfoPacket
     */

    public final int GOOGLE_FIT_OATH_RESULT = 1001;
    private SyncController syncController;
    private OtaController otaController;
    private StepsDatabaseHelper stepsDatabaseHelper;
    private SleepDatabaseHelper sleepDatabaseHelper;
    private AlarmDatabaseHelper alarmDatabaseHelper;
    private GoalDatabaseHelper goalDatabaseHelper;
    private UserDatabaseHelper userDatabaseHelper;
    private SolarDatabaseHelper solarDatabaseHelper;
    private boolean firmwareUpdateAlertDailog = false;
    //if it is -1, means mcu version hasn't be read
    private int mcuFirmwareVersion = -1;
    private int bleFirmwareVersion = -1;
    private GoogleFitManager googleFitManager;
    private GoogleFitTaskCounter googleFitTaskCounter;
    private CloudSyncManager cloudSyncManager;
    private User nevoUser;
    private WorldClockDatabaseHelper worldClockDatabaseHelper;
    private SleepGoalDatabaseHelper sleepGoalDatabaeHelper;
    private SolarGoalDatabaseHelper solargoalDatabaseHelper;
    private LedLampDatabase ledDataBase;
    private LocationController locationController;
    private IWXAPI mIWXAPI;
    private Steps steps = null;
    private List<Steps> allSteps;
    private boolean responseCode;
    private final String REALM_NAME = "med_lunar.realm";
    private Sleep mSleep;
    private boolean upDateIsSuccess;
    private Goal goal;
    private Sleep mYesterdaySleep;
    private Sleep[] todaySleep;
    private static ApplicationModel mModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mModel = this;
        Fabric.with(this, new Crashlytics());
        EventBus.getDefault().register(this);
        Realm.init(this);
        worldClockDatabaseHelper = new WorldClockDatabaseHelper(this);
        RealmConfiguration lunarConfig = new RealmConfiguration.Builder()
                .name(REALM_NAME)
                .modules(new WorldClockLibraryModule(), new LunarAllModules())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(lunarConfig);
        syncController = new SyncControllerImpl(this);
        otaController = new OtaControllerImpl(this);
        stepsDatabaseHelper = new StepsDatabaseHelper(this);
        sleepDatabaseHelper = new SleepDatabaseHelper(this);
        alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        goalDatabaseHelper = new GoalDatabaseHelper(this);
        userDatabaseHelper = new UserDatabaseHelper(this);
        solarDatabaseHelper = new SolarDatabaseHelper(this);
        sleepGoalDatabaeHelper = new SleepGoalDatabaseHelper(this);
        solargoalDatabaseHelper = new SolarGoalDatabaseHelper(this);
        cloudSyncManager = new CloudSyncManager(this);
        ledDataBase = new LedLampDatabase(this);
        locationController = new LocationController(this);
        mIWXAPI = WXAPIFactory.createWXAPI(this, getString(R.string.we_chat_app_id), true);
        userDatabaseHelper.getLoginUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                nevoUser = user;
            }
        });
        updateGoogleFit();
    }

    @Subscribe
    public void onEvent(OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            updateGoogleFit();
            getNeedSyncSteps(nevoUser.getUserID()).subscribe(new Consumer<List<Steps>>() {
                @Override
                public void accept(final List<Steps> stepses) throws Exception {
                    if (stepses.size() > 0) {
                        getNeedSyncSleep(nevoUser.getUserID()).subscribe(new Consumer<List<Sleep>>() {
                            @Override
                            public void accept(List<Sleep> sleeps) throws Exception {
                                getCloudSyncManager().launchSyncWeekly(nevoUser, stepses, sleeps);
                            }
                        });
                    }
                }
            });
        }
    }

    @Subscribe
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            Steps steps = getDailySteps(nevoUser.getUserID(), Common.removeTimeFromDate(new Date()));
            getCloudSyncManager().launchSyncDaily(nevoUser, steps);

        }
    }

    @Subscribe
    public void onEvent(SetSunriseAndSunsetTimeRequestEvent event) {
        if (event.getStatus() == SetSunriseAndSunsetTimeRequestEvent.STATUS.START) {
            getLocationController().startUpdateLocation();
        } else if (event.getStatus() == SetSunriseAndSunsetTimeRequestEvent.STATUS.SUCCESS) {
            getLocationController().stopLocation();
        } else if (event.getStatus() == SetSunriseAndSunsetTimeRequestEvent.STATUS.FAILED) {
            //TODO how to do it
            Log.w("ApplicationModel", "setSunriseAndSunset got failed.");
        }
    }


    public IWXAPI getWXApi() {
        return mIWXAPI;
    }

    public static ApplicationModel getInstance() {
        return mModel;
    }

    public WorldClockDatabaseHelper getWorldClockDatabaseHelper() {
        return worldClockDatabaseHelper;
    }

    public LocationController getLocationController() {
        return locationController;
    }

    public StepsDatabaseHelper getStepsHelper() {
        return stepsDatabaseHelper;
    }

    public SolarDatabaseHelper getSolarDatabaseHelper() {
        return solarDatabaseHelper;
    }

    public SyncController getSyncController() {
        return syncController;
    }

    public OtaController getOtaController() {
        return otaController;
    }

    public void startConnectToWatch(boolean forceScan) {
        syncController.startConnect(forceScan);
    }

    public boolean isWatchConnected() {
        return syncController.isConnected();
    }

    public void blinkWatch() {
        syncController.findDevice();
    }

    public void getBatteryLevelOfWatch() {
        syncController.getBatteryLevel();
    }

    public String getWatchSoftware() {
        return syncController.getSoftwareVersion();
    }

    public String getWatchFirmware() {
        return syncController.getFirmwareVersion();
    }

    public void setGoal(Goal goal) {
        syncController.setGoal(new NumberOfStepsGoal(goal.getSteps()));
    }

    public void setAlarm(List<Alarm> list) {
        syncController.setAlarm(list, false);
    }

    public void forgetDevice() {
        syncController.forgetDevice();
    }

    public List<Steps> getAllSteps() {
        stepsDatabaseHelper.getAll(nevoUser.getUserID()).subscribe(new Consumer<List<Steps>>() {
            @Override
            public void accept(List<Steps> stepses) throws Exception {
                allSteps = stepses;
            }
        });
        return allSteps;
    }

    public void getAllAlarm(final SyncControllerImpl.SyncAlarmToWatchListener listener) {
        alarmDatabaseHelper.getAll().subscribe(new Consumer<List<Alarm>>() {
            @Override
            public void accept(List<Alarm> alarms) throws Exception {
                if (listener != null && alarms.size() > 0) {
                    listener.syncAlarmToWatch(alarms);
                }
            }
        });
    }

    public void saveUser(final User user) {
        userDatabaseHelper.update(user).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (!aBoolean) {
                    userDatabaseHelper.add(user).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            Log.e("jason", "save user right");
                        }
                    });
                }
            }
        });
    }

    //TODO
    public void saveDailySteps(final Steps steps) {
        stepsDatabaseHelper.update(steps).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if (!b) {
                    stepsDatabaseHelper.addSteps(steps).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                Log.i("jason", "save daily steps success");
                            }
                        }
                    });
                }
            }
        });
    }

    public void removeUser(User user) {
        userDatabaseHelper.remove(user.getUserID(), new Date(user.getCreatedDate()));
    }

    public void getSolarData(int userId, Date date, WeekData weekData, final AnalysisSolarFragment.ObtainSolarListener listener) {

        List<Date> dateStarts = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        long startTime = 0;
        long entTime = 0;
        switch (weekData) {
            case TISHWEEK:
                startTime = calendar.getWeekStartDate().getTime();
                entTime = calendar.getWeekEndDate().getTime();
                break;
            case LASTWEEK:
                startTime = calendar.getLastWeekStart().getTime();
                entTime = calendar.getLastWeekEnd().getTime();
                break;
            case LASTMONTH:
                startTime = calendar.getMonthStartDate().getTime();
                entTime = calendar.getMonthEndDate().getTime();
                break;
        }

        for (long start = startTime; start <= entTime; start += 24 * 60 * 60 * 1000L) {
            Date dateStart = CalendarWeekUtils.getDayStartTime(new Date(start));
            dateStarts.add(dateStart);
        }
        solarDatabaseHelper.getSolarDatas(userId, dateStarts).subscribe(new Consumer<List<Solar>>() {
            @Override
            public void accept(List<Solar> solars) throws Exception {
                if (listener != null) {
                    listener.obtainSolarData(solars);
                }
            }
        });
    }

    public void getSleep(String userId, Date date, WeekData weekData,
                         final AnalysisSleepFragment.ObtainSleepDataListener weekDataListener) {
        List<Date> dateStarts = new ArrayList<>();
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        long startTime = 0;
        long entTime = 0;
        switch (weekData) {
            case TISHWEEK:
                startTime = calendar.getWeekStartDate().getTime();
                entTime = calendar.getWeekEndDate().getTime();
                break;
            case LASTWEEK:
                startTime = calendar.getLastWeekStart().getTime();
                entTime = calendar.getLastWeekEnd().getTime();
                break;
            case LASTMONTH:
                startTime = calendar.getMonthStartDate().getTime();
                entTime = calendar.getMonthEndDate().getTime();
                break;
        }

        for (long start = startTime; start <= entTime; start += 24 * 60 * 60 * 1000L) {
            Date dateStart = CalendarWeekUtils.getDayStartTime(new Date(start));
            dateStarts.add(dateStart);
        }

        sleepDatabaseHelper.getWeekSleep(userId, dateStarts).subscribe(new Consumer<List<SleepData>>() {
            @Override
            public void accept(List<SleepData> sleepDatas) throws Exception {
                if (weekDataListener != null) {
                    weekDataListener.obtainSleepData(sleepDatas);
                }
            }
        });
    }

    public void getSteps(String userId, Date date, WeekData weekData,
                         final AnalysisStepsFragment.OnStepsGetListener listener) {
        CalendarWeekUtils calendar = new CalendarWeekUtils(date);
        List<Date> dateStarts = new ArrayList<>();
        long startTime = 0;
        long entTime = 0;
        switch (weekData) {
            case TISHWEEK:
                startTime = calendar.getWeekStartDate().getTime();
                entTime = calendar.getWeekEndDate().getTime();
                break;
            case LASTWEEK:
                startTime = calendar.getLastWeekStart().getTime();
                entTime = calendar.getLastWeekEnd().getTime();
                break;
            case LASTMONTH:
                startTime = calendar.getMonthStartDate().getTime();
                entTime = calendar.getMonthEndDate().getTime();
                break;
        }

        for (long start = startTime; start <= entTime; start += 24 * 60 * 60 * 1000L) {
            Date dateStart = CalendarWeekUtils.getDayStartTime(new Date(start));
            dateStarts.add(dateStart);
        }
        stepsDatabaseHelper.getDailySteps(userId, dateStarts).subscribe(new Consumer<List<Steps>>() {
            @Override
            public void accept(List<Steps> step) throws Exception {
                if (null != listener) {
                    listener.onStepsGet(step);
                }
            }
        });
    }

    public Steps getDailySteps(String userId, Date date) {
        Date dateStart = CalendarWeekUtils.getDayStartTime(date);
        stepsDatabaseHelper.get(userId, dateStart).subscribe(new Consumer<Steps>() {
            @Override
            public void accept(Steps step) throws Exception {
                steps = step;
            }
        });
        if (this.steps != null) {
            return steps;
        } else {
            steps = new Steps(date.getTime());
            steps.setDate(date.getTime());
            steps.setCreatedDate(date.getTime());
            return steps;
        }
    }

    public void getDailySleep(final String userId, final Date todayDate, final MainSleepFragment.TodaySleepListener listener) {
        final Date yesterdayDate = new Date(todayDate.getTime() - 24 * 60 * 60 * 1000l);
        sleepDatabaseHelper.get(userId, todayDate).subscribe(new Consumer<Sleep>() {
            @Override
            public void accept(Sleep sleep) throws Exception {
                mSleep = sleep;
                sleepDatabaseHelper.get(userId, yesterdayDate).subscribe(new Consumer<Sleep>() {
                    @Override
                    public void accept(Sleep sleep) throws Exception {
                        mYesterdaySleep = sleep;
                        //use yesterday and today data to analysis sleep,pls refer to SleepDataHandler class
                        if (mYesterdaySleep != null && mSleep != null) {
                            todaySleep = new Sleep[]{mSleep, mYesterdaySleep};
                        }
                        //use today data to analysis sleep
                        if (mSleep != null && mYesterdaySleep == null) {
                            todaySleep = new Sleep[]{mSleep};
                        }
                        //use yesterday data (after 18:00) to analysis sleep
                        if (mYesterdaySleep != null && mSleep == null) {
                            todaySleep = new Sleep[]{mYesterdaySleep};
                        }
                        if (mSleep == null && mYesterdaySleep == null) {
                            Sleep noDataSleep = new Sleep(todayDate.getTime());
                            noDataSleep.setDate(Common.removeTimeFromDate(todayDate).getTime());
                            todaySleep = new Sleep[]{noDataSleep};
                        }
                        if (listener != null) {
                            listener.todaySleep(todaySleep);
                        }
                    }
                });
            }
        });
    }

    public void saveDailySleep(final Sleep sleep) {
        sleepDatabaseHelper.update(sleep).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (!aBoolean) {
                    sleepDatabaseHelper.add(sleep).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            Log.e("jason", "save yesterday sleep" + sleep.toString());
                        }
                    });
                }
            }
        });
    }

    public Observable<List<Steps>> getNeedSyncSteps(String userId) {
        return stepsDatabaseHelper.getNeedSyncSteps(userId);
    }

    public boolean isFoundInLocalSteps(int activity_id) {
        return stepsDatabaseHelper.isFoundInLocalSteps(activity_id);
    }

    public boolean isFoundInLocalSteps(Date date, String userID) {
        return stepsDatabaseHelper.isFoundInLocalSteps(date, userID);
    }

    public boolean isFoundInLocalSleep(int activity_id) {
        sleepDatabaseHelper.isFoundInLocalSleep(activity_id).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                upDateIsSuccess = aBoolean;
            }
        });

        return upDateIsSuccess;
    }

    public boolean isFoundInLocalSleep(Date date, String userID) {
        return sleepDatabaseHelper.isFoundInLocalSleep(date, userID);
    }

    public void saveStepsFromMed(ObtainMoreStepsResponse.StepsBean routine, Date createDate) {
        Steps steps = new Steps(createDate.getTime());
        steps.setDate(Common.removeTimeFromDate(createDate).getTime());
        try {
            JSONArray hourlyArray = new JSONArray(routine.getSteps());
            int totalSteps = 0;
            for (int i = 0; i < hourlyArray.length(); i++) {
                totalSteps += hourlyArray.optInt(i);
            }
            steps.setHourlySteps(routine.getSteps());
            steps.setSteps(totalSteps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        steps.setDistance((int) routine.getDistance());
        steps.setCalories(routine.getCalories());
        steps.setWalkDuration(routine.getActive_time());
        steps.setUserID(routine.getUid() + "");
        steps.setCloudRecordID(routine.getId() + "");
        steps.setGoal(10000);
        saveDailySteps(steps);
    }

    public void saveSleepFromMed(ObtainMoreSleepResponse.SleepBean dailySleep, Date createDate) {
        Sleep sleep = new Sleep(createDate.getTime());
        sleep.setDate(Common.removeTimeFromDate(createDate).getTime());

        sleep.setHourlyWake(dailySleep.getWake_time());
        sleep.setHourlyLight(dailySleep.getLight_sleep());
        sleep.setHourlyDeep(dailySleep.getDeep_sleep());

        int lightSleep = 0;
        int deepSleep = 0;
        int wake = 0;
        List<Integer> hourlySleepList = new ArrayList<>();
        try {
            JSONArray hourlyWake = new JSONArray(sleep.getHourlyWake());
            for (int i = 0; i < hourlyWake.length(); i++) {
                wake += Integer.parseInt(hourlyWake.getString(i));
                hourlySleepList.add(Integer.parseInt(hourlyWake.getString(i)));
            }

            JSONArray hourlyLight = new JSONArray(sleep.getHourlyLight());
            for (int i = 0; i < hourlyLight.length(); i++) {
                lightSleep += Integer.parseInt(hourlyLight.getString(i));
                hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyLight.getString(i)));
            }

            JSONArray hourlyDeep = new JSONArray(sleep.getHourlyDeep());
            for (int i = 0; i < hourlyDeep.length(); i++) {
                deepSleep += Integer.parseInt(hourlyDeep.getString(i));
                hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyDeep.getString(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sleep.setHourlySleep(hourlySleepList.toString());
        sleep.setTotalSleepTime(wake + deepSleep + lightSleep);
        sleep.setTotalWakeTime(wake);
        sleep.setTotalLightTime(lightSleep);
        sleep.setTotalDeepTime(deepSleep);
        sleep.setStart(0);
        sleep.setEnd(0);
        sleep.setUserID(getUser().getUserID());
        //we must set CloudRecordID here, avoid doing sync repeatly
        sleep.setCloudRecordID(dailySleep.getId() + "");
        try {
            sleep.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(sleep.getDate()))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveDailySleep(sleep);
    }

    public Observable<List<Sleep>> getNeedSyncSleep(String userid) {
        return sleepDatabaseHelper.getNeedSyncSleep(userid);
    }

    public Observable<Boolean> addAlarm(Alarm alarm) {
        return alarmDatabaseHelper.add(alarm);
    }

    public Observable<Boolean> updateAlarm(Alarm alarm) {
        return alarmDatabaseHelper.update(alarm);
    }

    public void getAlarmById(int id, final EditAlarmActivity.ObtainAlarmListener listener) {
        alarmDatabaseHelper.get(id).subscribe(new Consumer<Alarm>() {
            @Override
            public void accept(Alarm alarm) throws Exception {
                if (listener != null) {
                    listener.obtainAlarm(alarm);
                }
            }
        });
    }

    public Observable<Boolean> deleteAlarm(Alarm alarm) {
        return alarmDatabaseHelper.remove(alarm.getId());
    }

    public AlarmDatabaseHelper getAlarmDatabaseHelper() {
        return alarmDatabaseHelper;
    }

    public void getAllGoal(final MainFragment.ObtainGoalListener listener) {
        goalDatabaseHelper.getAll().subscribe(new Consumer<List<Goal>>() {
            @Override
            public void accept(List<Goal> goals) throws Exception {
                if (listener != null) {
                    listener.obtainGoal(goals);
                }
            }
        });
    }

    public void addGoal(Goal goal) {
        goalDatabaseHelper.add(goal).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    Log.i("jason", "append success");
                }
            }
        });
    }

    public boolean updateGoal(Goal goal) {
        goalDatabaseHelper.update(goal).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                upDateIsSuccess = aBoolean;
            }
        });
        return upDateIsSuccess;
    }

    public Goal getGoalById(int id) {
        goalDatabaseHelper.get(id).subscribe(new Consumer<Goal>() {
            @Override
            public void accept(Goal g) throws Exception {
                goal = g;
            }
        });
        return goal;
    }

    /**
     * user LedLamp Database
     *
     * @return
     */
    public List<LedLamp> getAllLedLamp() {
        return ledDataBase.getAll();
    }

    public LedLamp getSelectLamp(String name, int color) {
        return ledDataBase.get(name, color) == null ? null : ledDataBase.get(name, color);
    }

    public boolean addLedLamp(LedLamp ledLamp) {
        return ledDataBase.add(ledLamp);
    }

    public boolean upDataLedLamp(LedLamp ledLamp) {
        return ledDataBase.update(ledLamp);
    }

    public boolean removeLedLamp(String name, int color) {
        return ledDataBase.remove(name, color);
    }

    public void getPositionLocal(final Location mLocation) {
        if (mLocation == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder ge = new Geocoder(ApplicationModel.this);
                    List<Address> addList = ge.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    if (addList != null && addList.size() > 0) {
                        Preferences.saveLocation(ApplicationModel.this, addList.get(0));
                        EventBus.getDefault().post(new PositionAddressChangeEvent(addList.get(0)));
                        if (Preferences.getPositionCity(ApplicationModel.this) == null) {
                            Preferences.savePositionCountry(ApplicationModel.this, addList.get(0).getCountryName());
                            Preferences.savePositionCity(ApplicationModel.this, addList.get(0).getLocality());
                            Preferences.saveHomeCityCalender(ApplicationModel.this, Calendar.getInstance().getTimeZone().getID());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public LedLamp getUserSelectLedLamp(int color) {
        LedLamp ledlamp = new LedLamp();
        ledlamp.setColor(getResources().getColor(R.color.red_normal));
        ledlamp.setName(getString(R.string.notification_def_name));

        List<LedLamp> allLedLamp = getAllLedLamp();
        for (LedLamp lamp : allLedLamp) {
            if (lamp.getColor() == color) {
                return lamp;
            }
        }
        return ledlamp;
    }

    public boolean isBluetoothOn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            return true;
        }
        return false;
    }

    public void initGoogleFit(AppCompatActivity appCompatActivity) {
        if (Preferences.isGoogleFitSet(this)) {
            googleFitTaskCounter = new GoogleFitTaskCounter(3);
            googleFitManager = new GoogleFitManager(this);
            if (appCompatActivity != null) {
                googleFitManager.setActivityForResults(appCompatActivity);
            }
            googleFitManager.connect();
        }
    }

    public void disconnectGoogleFit() {
        if (googleFitManager != null) {
            googleFitManager.disconnect();
        }
    }

    @Subscribe
    public void onEvent(LocationChangedEvent locationChangedEvent) {
        getPositionLocal(locationChangedEvent.getLocation());
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectionFailedEvent event) {
        if (event.getConnectionResult().getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                event.getConnectionResult().getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                if (googleFitManager.getActivity() != null) {
                    event.getConnectionResult().startResolutionForResult(googleFitManager.getActivity(), GOOGLE_FIT_OATH_RESULT);
                }
            } catch (IntentSender.SendIntentException e) {
                ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_could_not_login);
            }
        } else {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_connecting);
        }
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectionSuspendedEvent event) {
        if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_network_lost);
        } else if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_service_disconnected);
        } else {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_unknown_network);
        }
    }

    @Subscribe
    public void onEvent(GoogleFitUpdateEvent event) {
        if (event.isSuccess()) {
            googleFitTaskCounter.incrementSuccessAndFinish();
            if (googleFitTaskCounter.allSucces()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Updated Google Fit");
                googleFitTaskCounter.reset();
            }
        } else {
            googleFitTaskCounter.incrementFinish();
            if (googleFitTaskCounter.areTasksDone()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Couldn't updated Google Fit");
                googleFitTaskCounter.reset();
            }
        }
    }

    public void updateGoogleFit() {
        if (Preferences.isGoogleFitSet(this)) {
            initGoogleFit(null);
            GoogleFitStepsDataHandler dataHandler = new GoogleFitStepsDataHandler(getAllSteps(), ApplicationModel.this);
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getStepsDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getCaloriesDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getDistanceDataSet());
        }
    }

    public CloudSyncManager getCloudSyncManager() {
        return cloudSyncManager;
    }

    public LedLampDatabase getLedDataBase() {
        return ledDataBase;
    }

    public User getUser() {
        return nevoUser;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Subscribe
    public void onValidicAddRoutineRecordEvent(ValidicAddRoutineRecordEvent
                                                       validicAddRoutineRecordEvent) {
        saveDailySteps(validicAddRoutineRecordEvent.getSteps());

    }

    @Subscribe
    public void onValidicAddSleepRecordEvent(ValidicAddSleepRecordEvent
                                                     validicAddSleepRecordEvent) {
        saveDailySleep(validicAddSleepRecordEvent.getSleep());
    }

    @Subscribe
    public void onMedAddRoutineRecordEvent(MedAddRoutineRecordEvent medAddRoutineRecordEvent) {
        saveDailySteps(medAddRoutineRecordEvent.getSteps());

    }

    @Subscribe
    public void onMedAddSleepRecordEvent(MedAddSleepRecordEvent medAddSleepRecordEvent) {
        saveDailySleep(medAddSleepRecordEvent.getSleep());
    }

    @Subscribe
    public void onValidicCreateUserEvent(ValidicCreateUserEvent validicCreateUserEvent) {
        saveUser(validicCreateUserEvent.getUser());
        getSyncController().getDailyTrackerInfo(true);
        getNeedSyncSteps(nevoUser.getUserID()).subscribe(new Consumer<List<Steps>>() {
            @Override
            public void accept(final List<Steps> stepses) throws Exception {
                getNeedSyncSleep(nevoUser.getUserID()).subscribe(new Consumer<List<Sleep>>() {
                    @Override
                    public void accept(List<Sleep> sleeps) throws Exception {
                        getCloudSyncManager().launchSyncAll(nevoUser, stepses, sleeps);
                    }
                });
            }
        });
    }

    @Subscribe
    public void onValidicDeleteSleepRecordModelEvent(ValidicDeleteSleepRecordModelEvent
                                                             validicDeleteSleepRecordModelEvent) {
        sleepDatabaseHelper.remove(validicDeleteSleepRecordModelEvent.getUserId() + "", validicDeleteSleepRecordModelEvent.getDate());
    }

    @Subscribe
    public void onValidicException(ValidicException validicException) {
        Log.w("Karl", "Exception occured!");
        validicException.getException().printStackTrace();
    }

    @Subscribe
    public void onMedReadMoreRoutineRecordsModelEvent(MedReadMoreRoutineRecordsModelEvent
                                                              medReadMoreRoutineRecordsModelEvent) {

        if (medReadMoreRoutineRecordsModelEvent.getMedReadMoreRoutineRecordsModel().getSteps() == null
                || medReadMoreRoutineRecordsModelEvent.getMedReadMoreRoutineRecordsModel().getSteps().size() == 0) {
            return;
        }
        for (ObtainMoreStepsResponse.StepsBean routine : medReadMoreRoutineRecordsModelEvent.getMedReadMoreRoutineRecordsModel().getSteps()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(routine.getDate().getDate());
                // if not exist in local Steps table, save it
                if (!isFoundInLocalSteps(date, routine.getUid() + "")) {
                    saveStepsFromMed(routine, date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onMedReadMoreSleepRecordsModelEvent(MedReadMoreSleepRecordsModelEvent
                                                            medReadMoreSleepRecordsModelEvent) {

        if (medReadMoreSleepRecordsModelEvent.getMedReadMoreSleepRecordsModel().getSleep() == null
                || medReadMoreSleepRecordsModelEvent.getMedReadMoreSleepRecordsModel().getSleep().size() == 0) {
            return;
        }
        for (ObtainMoreSleepResponse.SleepBean sleep :
                medReadMoreSleepRecordsModelEvent.getMedReadMoreSleepRecordsModel().getSleep()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sleep.getDate().getDate());
                // if not exist in local Sleep table, save it
                if (!isFoundInLocalSleep(date, sleep.getUid() + "")) {
                    saveSleepFromMed(sleep, date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onValidicUpdateRoutineRecordsModelEvent(ValidicUpdateRoutineRecordsModelEvent
                                                                validicUpdateRoutineRecordsModelEvent) {
        saveDailySteps(validicUpdateRoutineRecordsModelEvent.getSteps());

    }

    @Subscribe
    public void onValidicDeleteRoutineRecordEvent(ValidicDeleteRoutineRecordEvent
                                                          validicDeleteRoutineRecordEvent) {
        stepsDatabaseHelper.remove(validicDeleteRoutineRecordEvent.getUserId() + "", validicDeleteRoutineRecordEvent.getDate());
    }

    public void getWeChatToken(String code) {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + getString(R.string.we_chat_app_id) + "&secret=" + getString(R.string.wechat_app_secret)
                + "&code=" + code + "&grant_type=authorization_code";
        Log.i("jason", url);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception, IOException {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    e.onNext(response.body().string());
                } else {
                    e.onNext("");
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (s != null) {
                            Gson gson = new Gson();
                            RequestWeChatToken token = gson.fromJson(s, RequestWeChatToken.class);
                            Log.i("jason", token.toString());
                            getUserInfo(token);
                        } else {
                            EventBus.getDefault().post(new WeChatEvent());
                        }
                    }
                });
    }

    private void getUserInfo(RequestWeChatToken token) {

        String accessToken = token.getAccess_token();
        String openId = token.getOpenid();
        final String uri = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
        Log.i("jason", uri);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(uri).build();
                Response execute = null;
                try {
                    execute = client.newCall(request).execute();
                    if (execute.isSuccessful()) {
                        String response = execute.body().string();
                        e.onNext(response);
                    } else {
                        e.onNext("");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        if (response != null) {
                            Log.i("jason", "user info ::::" + response);
                            Gson gson = new Gson();
                            WeChatUserInfoResponse mUserInfo = gson.fromJson(response, WeChatUserInfoResponse.class);
                            EventBus.getDefault().post(new ReturnUserInfoEvent(mUserInfo));
                        } else {
                            EventBus.getDefault().post(new WeChatEvent());
                        }
                    }
                });
    }

    public SolarGoalDatabaseHelper getSolarGoalDatabaseHelper(){
        return solargoalDatabaseHelper;
    }

    public SleepGoalDatabaseHelper getSleepDatabseHelper(){
        return sleepGoalDatabaeHelper;
    }
}