package com.medcorp.lunar.ble.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.medcorp.lunar.R;
import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.lunar.ble.model.request.OTAStartRequest;
import com.medcorp.lunar.util.Common;

import net.medcorp.library.ble.controller.ConnectionController;
import net.medcorp.library.ble.controller.OtaController;
import net.medcorp.library.ble.event.BLEConnectionStateChangedEvent;
import net.medcorp.library.ble.listener.OnOtaControllerListener;
import net.medcorp.library.ble.util.Constants.DFUControllerState;
import net.medcorp.library.ble.util.Constants.DfuFirmwareTypes;
import net.medcorp.library.ble.util.Optional;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;


public class OtaControllerImpl implements OtaController  {
    private final static String TAG = "OtaControllerImpl";

    private ApplicationModel mContext;

    private Optional<OnOtaControllerListener> mOnOtaControllerListener = new Optional<OnOtaControllerListener>();
    private ConnectionController connectionController;

    private DfuFirmwareTypes dfuFirmwareType = DfuFirmwareTypes.BLUETOOTH ;


    /** check the OTA is doing or stop */
    private Timer mTimeoutTimer = null;
    public static final int MAX_TIME = 45000;
    private double lastprogress = 0.0;

    private DFUControllerState state = DFUControllerState.INIT;
    private double progress = 0.0;
    private boolean manualmode = false;
    //end added

    /**
     * this class is OTA timer:MAX_TIME seconds, when OTA is in progress that got broken, it will fire this timer
     * and check whether the progress has got changed, if no change, it means OTA got stopped,for MCU OTA, it give
     * a way that continue OTA from the broken point, or popup message to user how to do(retry or reinstall battery)
     */
    private class myOTATimerTask extends  TimerTask
    {
        @Override
        public void run() {
            //add timeout process when use Nordic dfu library,check the state value to judge OTA is doing or not
            if(dfuFirmwareType == DfuFirmwareTypes.DISTRIBUTION_ZIP
                    && (state == DFUControllerState.SEND_FIRMWARE_DATA||state == DFUControllerState.INIT)) {
                return;
            }
            if (lastprogress == progress) //when no change happened, timeout
            {
                Log.e(TAG, "* * * OTA timeout * * *" + "state = " + state + ",connected:" + isConnected() + ",lastprogress = " + lastprogress + ",progress = " + progress);
                ERRORCODE errorcode = ERRORCODE.TIMEOUT;
                if (state == DFUControllerState.SEND_START_COMMAND
                        && dfuFirmwareType == DfuFirmwareTypes.BLUETOOTH
                        && isConnected()) {
                    Log.e(TAG, "* * * BLE OTA timeout by start command not get disconnected from watch* * *");
                }
                //when start Scan DFU service, perhaps get nothing with 20s, here need again scan it?
                else if (state == DFUControllerState.DISCOVERING && dfuFirmwareType == DfuFirmwareTypes.BLUETOOTH) {
                    Log.e(TAG, "* * * BLE OTA timeout by no found DFU service * * *");
                    errorcode = ERRORCODE.NODFUSERVICE;
                }
                Log.e(TAG, "* * * call OTA timeout function * * * OTA type = " + (dfuFirmwareType == DfuFirmwareTypes.BLUETOOTH ?"BLE":"MCU") + ",ErrorCode = " + errorcode);
                if (mOnOtaControllerListener.notEmpty()) {
                     mOnOtaControllerListener.get().onError(errorcode);
                }
            } else {
                lastprogress = progress;
            }
        }
    }

    public OtaControllerImpl(ApplicationModel context)
    {
        mContext = context;
        connectionController = ConnectionController.Singleton.getInstance(context,new GattAttributesDataSourceImpl(context));
        connectionController.connect();
        EventBus.getDefault().register(this);
    }

    public void setManualMode(boolean  manualmode)
    {
        this.manualmode = manualmode;
    }

    //start public function
    /**
     * start OTA
     * @param filename
     * @param firmwareType
     */
    public void performDFUOnFile(String filename , DfuFirmwareTypes firmwareType)
    {
        if(!isConnected()) {
            String errorMessage = mContext.getString(R.string.dfu_connect_error_no_nevo_do_ota);
            Log.e(TAG,errorMessage);
            state = DFUControllerState.INIT;
            Toast.makeText(mContext,errorMessage,Toast.LENGTH_LONG).show();
            if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onError(ERRORCODE.NOCONNECTION);
            return;
        }
        lastprogress = 0.0;
        progress = 0.0;
        mTimeoutTimer = new Timer();
        mTimeoutTimer.schedule(new myOTATimerTask(),MAX_TIME, MAX_TIME);
        dfuFirmwareType = firmwareType;
        if(manualmode && dfuFirmwareType == DfuFirmwareTypes.DISTRIBUTION_ZIP)
        {
            Log.i(TAG,"***********connectionController disconnect without find DFU service and dfu library will take over the OTA*******,manualmode=true");
            String newDeviceAdress = connectionController.getSaveAddress();
            state = DFUControllerState.SEND_FIRMWARE_DATA;
            connectionController.disconnect();
            if (mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onDFUServiceStarted(newDeviceAdress);
        }
        //pair mode for doing OTA
        else
        {
            state = DFUControllerState.IDLE;
            connectionController.setOTAMode(false, true);
        }
        mContext.getSyncController().setHoldRequest(true);
        if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onPrepareOTA(firmwareType);
    }

    @Override
    public void cancelDFU() {

    }
    @Override
    public void setOtaMode(boolean otaMode,boolean disConnect)
    {
        connectionController.setOTAMode(otaMode, disConnect);
    }

    /**
     * set hight level listener, it should be a activity (OTA controller view:Activity or one fragment)
     */
    @Override
    public void setOnOtaControllerListener(OnOtaControllerListener listener)
    {
        mOnOtaControllerListener.set(listener);
    }

    @Override
    public Boolean isConnected() {
        return connectionController.isConnected();
    }

    @Override
    public DFUControllerState getState()
    {
        return state;
    }
    @Override
    public void setState(DFUControllerState state)
    {
        this.state = state;
    }

    /**
     reset to normal mode "NevoProfile"
     parameter: switch2SyncController: true/false
     step1: restore Address
     step2: restore syncController
     step3: restore normal mode
     step4: reconnect
     //from OTA mode to normal mode, must make syncController to handle connectionController
     because MCU/BLE ota, user has done one of them, perhaps do another one,
     so no need make syncController handle connectionController
     */
    @Override
    public void reset(boolean switch2SyncController) {

        if(mTimeoutTimer!=null) {mTimeoutTimer.cancel();mTimeoutTimer=null;}
        //reset it to INIT status !!!IMPORTANT!!!
        state = DFUControllerState.INIT;
        //BLE OTA and lunar OTA with DFU library, both need forgetSavedAddress(), so here restore it for next time connection
        if(dfuFirmwareType == DfuFirmwareTypes.BLUETOOTH || dfuFirmwareType == DfuFirmwareTypes.DISTRIBUTION_ZIP)
        {
            connectionController.restoreSavedAddress();
        }
        if(manualmode)
        {
            manualmode = false;
            connectionController.forgetSavedAddress();
        }

        //disconnect and reconnect for reading new version
        connectionController.setOTAMode(false, true);
        mContext.getSyncController().setHoldRequest(false);
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
    public void forGetDevice()
    {
        //BLE OTA need repair NEVO, so here forget this nevo when OTA done.
        connectionController.unPairDevice(connectionController.getSaveAddress());
    }

    @Subscribe
    public void onEvent(final BLEConnectionStateChangedEvent event){
        if(mOnOtaControllerListener.notEmpty()) {
            mOnOtaControllerListener.get().connectionStateChanged(event.isConnected());
        }
        //use distribution firmware (zip file)
        if(dfuFirmwareType == DfuFirmwareTypes.DISTRIBUTION_ZIP)
        {
            if (event.isConnected())
            {
                if (state == DFUControllerState.SEND_RECONNECT)
                {
                    state = DFUControllerState.SEND_START_COMMAND;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectionController.sendRequest(new OTAStartRequest(mContext));
                        }
                    },1000);
                }
                else if (state == DFUControllerState.DISCOVERING)
                {
                    state = DFUControllerState.SEND_FIRMWARE_DATA;
                    //kill connectionController and med-library BT service and use dfu library service
                    Log.i(TAG,"***********connectionController has found DFU service,disconnect it and dfu library will take over the OTA*******");
                    connectionController.restoreSavedAddress();
                    connectionController.disconnect();
                    if(mOnOtaControllerListener.notEmpty()) mOnOtaControllerListener.get().onDFUServiceStarted(event.getAddress());
                }
            }
            else
            {
                if (state == DFUControllerState.IDLE)
                {
                    state = DFUControllerState.SEND_RECONNECT;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectionController.reconnect();
                        }
                    },1000);
                }

                //by BLE peer disconnect when normal mode to ota mode
                else if (state == DFUControllerState.SEND_START_COMMAND)
                {
                    //!!!IMPORT!!! here set it true, means that otaController need firstly search the DFU service and connect it, then disconnect it and hand it over to DFU library
                    //if set it false,means that let DFU library search the DFU service directly, there is a risk that always got ota failed at the first ota (100% occur),even through redo ota can get success 100%, that still is a bad experience for user.
                    final boolean needSearchDFUservice = true;
                    if(needSearchDFUservice) {
                        state = DFUControllerState.DISCOVERING;
                        connectionController.setOTAMode(true, true);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG,"***********set OTA mode,forget it firstly,and scan DFU service*******");
                                //when switch to DFU mode, the MAC address has changed to another one
                                connectionController.forgetSavedAddress();
                                connectionController.connect();
                            }
                        },1000);
                    }
                    //we let DFU library take over the OTA process directly without verify DFU service,
                    // but here we must calculate the new device address changed by DFU mode
                    else {
                        String newDeviceAdress = Common.getMacAdd(event.getAddress());
                        state = DFUControllerState.SEND_FIRMWARE_DATA;
                        connectionController.disconnect();
                        if (mOnOtaControllerListener.notEmpty())
                            mOnOtaControllerListener.get().onDFUServiceStarted(newDeviceAdress);
                    }
                }
            }
        }

    }

}
