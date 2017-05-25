package com.medcorp.lunar.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.ble.dfu.DfuService;
import com.medcorp.lunar.util.Common;
import com.medcorp.lunar.view.RoundProgressBar;
import com.medcorp.lunar.view.customfontview.RobotoButton;

import net.medcorp.library.ble.controller.OtaController;
import net.medcorp.library.ble.listener.OnOtaControllerListener;
import net.medcorp.library.ble.model.response.BLEResponseData;
import net.medcorp.library.ble.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * Created by gaillysu on 15/12/28.
 */
public class DfuActivity extends BaseActivity implements OnOtaControllerListener {

    private static final String TAG = "DfuActivity";

    @Bind(R.id.clock_imageView)
    ImageView clockImage;

    @Bind(R.id.lunar_main_clock_home_hour)
    ImageView hourImage;

    @Bind(R.id.lunar_main_clock_home_minute)
    ImageView minImage;

    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @Bind(R.id.activity_dfu_percent_textview)
    TextView percentTextView;

    @Bind(R.id.activity_dfu_infomation_textview)
    TextView infomationTextView;

    @Bind(R.id.activity_dfu_back2settings_textview)
    TextView back2settings;

    private OtaController mNevoOtaController;
    private Constants.DfuFirmwareTypes enumFirmwareType = Constants.DfuFirmwareTypes.BLUETOOTH;
    private List<String> firmwareURLs;
    private int currentIndex;
    private String errorMsg = "";
    private Context mContext;
    private boolean mUpdateSuccess = false;
    private boolean manualMode = false;
    private boolean backToSetting = false;
    private boolean isShowingAlertDialog = false;

    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            Log.i(TAG, "***********onDeviceConnecting*******" + deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            Log.i(TAG, "***********onDfuProcessStarting*******" + deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            Log.i(TAG, "***********onEnablingDfuMode*******" + deviceAddress);
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            Log.i(TAG, "***********onFirmwareValidating*******" + deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            Log.i(TAG, "***********onDeviceDisconnecting*******" + deviceAddress);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            Log.i(TAG, "***********onDfuCompleted*******" + deviceAddress);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    DfuActivity.this.onSuccessfulFileTranfered();
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            Log.i(TAG, "***********onDfuAborted*******" + deviceAddress);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    DfuActivity.this.onDFUCancelled();
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            Log.i(TAG, "***********onProgressChanged*******" + deviceAddress + ",percent = " + percent);
            // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    DfuActivity.this.onTransferPercentage(percent);
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            Log.i(TAG, "***********onError*******" + deviceAddress + ",message:" + message);
            // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    DfuActivity.this.onError(OtaController.ERRORCODE.EXCEPTION);
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu);
        ButterKnife.bind(this);
        mContext = this;
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initManualmodeAndtFirmwareList();
        initNevoLogo();
        back2settings.setVisibility(View.INVISIBLE);
        back2settings.setText(R.string.dfu_re_upgrade);
        back2settings.setTag(new ButtonTag(getString(R.string.dfu_retry)));

        mNevoOtaController = getModel().getOtaController();
        mNevoOtaController.setOnOtaControllerListener(this);
        if (manualMode) {
            mNevoOtaController.setManualMode(true);
            mNevoOtaController.setOtaMode(true, true);
        } else {
            showAlertDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }

    private void showAlertDialog() {
        if (isShowingAlertDialog) {
            return;
        }
        isShowingAlertDialog = true;

        new MaterialDialog.Builder(this)
                .title(R.string.dfu_update_title)
                .content(R.string.dfu_update_content)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        isShowingAlertDialog = false;
                        uploadPressed();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        isShowingAlertDialog = false;
                        finish();
                    }
                })
                .positiveText(R.string.dfu_update_positive)
                .negativeText(R.string.dfu_update_negative)
                .cancelable(false)
                .show();

    }

    private void initNevoLogo() {
        hourImage.setVisibility(View.VISIBLE);
        minImage.setVisibility(View.VISIBLE);
        clockImage.setVisibility(View.VISIBLE);
        clockImage.setImageResource(R.drawable.watch_dashboard_icon);

        int hour = 11;
        int minute = 5;
        final float degreeHour = (float) ((hour + minute / 60.0) * 30);
        final float degreeMin = minute * 6;
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hourImage.setRotation(degreeHour);
                minImage.setRotation(degreeMin);
            }
        });
    }

    private void initManualmodeAndtFirmwareList() {
        firmwareURLs = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        manualMode = bundle.getBoolean(getString(R.string.key_manual_mode), false);
        if (manualMode) {
            firmwareURLs = Common.getAllBuildInZipFirmwareURLs(this, getModel().getSyncController().getWatchInfomation().getWatchID());
        } else {
            firmwareURLs = bundle.getStringArrayList(getString(R.string.key_firmwares));
        }
        currentIndex = 0;
        backToSetting = bundle.getBoolean(getString(R.string.key_back_to_settings), true);
    }

    private void uploadPressed() {
        mUpdateSuccess = false;
        errorMsg = "";
        if (!mNevoOtaController.isConnected()) {
            Log.e(TAG, mContext.getString(R.string.dfu_connect_error_no_nevo_do_ota));
            onError(OtaController.ERRORCODE.NOCONNECTION);
            return;
        }
        if (currentIndex >= firmwareURLs.size() || firmwareURLs.size() == 0) {
            //check firmwareURLs is null, should hide the button
            Log.e(TAG, mContext.getString(R.string.dfu_checking_firmware));
            onError(OtaController.ERRORCODE.NOFINISHREADVERSION);
            return;
        }

        String selectedFileURL = firmwareURLs.get(currentIndex);
        if (selectedFileURL.contains(".bin")) {
            enumFirmwareType = Constants.DfuFirmwareTypes.MCU;
        }
        if (selectedFileURL.contains(".hex")) {
            enumFirmwareType = Constants.DfuFirmwareTypes.BLUETOOTH;
        }
        if (selectedFileURL.contains(".zip")) {
            enumFirmwareType = Constants.DfuFirmwareTypes.DISTRIBUTION_ZIP;
        }
        initNevoLogo();
        roundProgressBar.setProgress(0);
        roundProgressBar.setVisibility(View.VISIBLE);
        percentTextView.setText("");
        infomationTextView.setText("");
        //The process of OTA hide this control
        back2settings.setVisibility(View.INVISIBLE);
        mNevoOtaController.performDFUOnFile(selectedFileURL, enumFirmwareType);
    }

    @Override
    public void onPrepareOTA(Constants.DfuFirmwareTypes which) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infomationTextView.setText(R.string.dfu_update_prepare);
            }
        });
    }

    @Override
    public void packetReceived(BLEResponseData packet) {

    }

    @Override
    public void connectionStateChanged(final boolean isConnected) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isConnected) {
                    //when get disconnected between firmwares, hidden the retry/continue button until got connected and show it
                    if (currentIndex != firmwareURLs.size()) {
                        back2settings.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mNevoOtaController.getState() == Constants.DFUControllerState.INIT) {
                        if (errorMsg != "") {
                            back2settings.setText(R.string.dfu_re_upgrade);
                            back2settings.setTag(new ButtonTag(getString(R.string.dfu_retry)));
                            back2settings.setVisibility(View.VISIBLE);
                        } else {
                            if (!manualMode || mUpdateSuccess) {
                                back2settings.setVisibility(View.VISIBLE);
                            }
                        }
                        //popup alert dialog for manual OTA mode when find out the OTA service
                        if (manualMode && currentIndex == 0) {
                            showAlertDialog();
                        }
                    } else if ((mNevoOtaController.getState() == Constants.DFUControllerState.SEND_RESET)) {
                        back2settings.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onDFUStarted() {
        Log.i(TAG, "onDFUStarted");
    }

    @Override
    public void onDFUCancelled() {
        Log.i(TAG, "onDFUCancelled");
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNevoOtaController.reset(false);
            }
        });
    }

    @Override
    public void onTransferPercentage(final int percent) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roundProgressBar.setProgress(percent);
                percentTextView.setText(percent + "%");
                infomationTextView.setText(getString(R.string.dfu_update_message) + ((enumFirmwareType == Constants.DfuFirmwareTypes.MCU) ? "MCU" : ((enumFirmwareType == Constants.DfuFirmwareTypes.BLUETOOTH) ? "BLE" : "")) + " (" + (currentIndex + 1) + "/" + firmwareURLs.size() + ")");
            }
        });
    }

    @Override
    public void onSuccessfulFileTranfered() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //BLE OTA done, forget the "nevo_dfu" (this is a new MAC address when doing OTA)
                //for lunar OTA, DFU library has forget the new MAC address, but we must also forget the saved address before next time connection(perhaps lunar ota has destoried the BT pair infomation, if we don't do so, after lunar OTA successfully,lunar will not be connected with app)
                if (enumFirmwareType == Constants.DfuFirmwareTypes.BLUETOOTH || enumFirmwareType == Constants.DfuFirmwareTypes.DISTRIBUTION_ZIP) {
                    mNevoOtaController.forGetDevice();
                }
                currentIndex = currentIndex + 1;
                if (currentIndex == firmwareURLs.size()) {
                    mUpdateSuccess = true;
                    //reset it to avoid again show the alertdialog when next connected in this screen
                    manualMode = false;

                    roundProgressBar.setVisibility(View.INVISIBLE);
                    clockImage.setVisibility(View.INVISIBLE);
                    percentTextView.setText("");
                    infomationTextView.setText(R.string.dfu_firmware_updated);
                    back2settings.setText(backToSetting ? R.string.dfu_back_to_settings : R.string.dfu_back);
                    back2settings.setTag(new ButtonTag(getString(R.string.dfu_back)));
                    back2settings.setVisibility(View.VISIBLE);
                    //show success text or image
                    mNevoOtaController.reset(false);
                    //save date
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = format.format(Calendar.getInstance().getTimeInMillis());
                    getSharedPreferences(OtaController.PREF_NAME, Context.MODE_PRIVATE).edit().putString(OtaController.SYNCDATE, strDate).commit();

                } else {
                    mUpdateSuccess = true;
                    mNevoOtaController.reset(false);
                    mNevoOtaController.setState(Constants.DFUControllerState.SEND_RESET);

                    back2settings.setText(getString(R.string.dfu_continue_button));
                    back2settings.setTag(new ButtonTag(getString(R.string.dfu_continue_button)));
                    back2settings.setVisibility(View.INVISIBLE);

                    roundProgressBar.setVisibility(View.INVISIBLE);
                    hourImage.setVisibility(View.INVISIBLE);
                    minImage.setVisibility(View.INVISIBLE);
                    clockImage.setImageDrawable(ContextCompat.getDrawable(DfuActivity.this, R.drawable.tutorial_nevo_link_success));
                    percentTextView.setText(R.string.dfu_press_third_button);
                    infomationTextView.setText(R.string.dfu_press_third_button_description);
                }
            }
        });
    }

    @Override
    public void onError(final OtaController.ERRORCODE errorCode) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNevoOtaController.reset(false);
                if (mUpdateSuccess) {
                    return; //fix a bug when BLE OTA done,connect it before BT off, it can't find any characteristics and throw exception
                }
                if (errorCode == OtaController.ERRORCODE.TIMEOUT)
                    //errorMsg = mContext.getString(R.string.update_error_timeout);
                    errorMsg = getString(R.string.dfu_failed_preparing);
                else if (errorCode == OtaController.ERRORCODE.NOCONNECTION)
                    errorMsg = mContext.getString(R.string.dfu_error_noconnect);
                else if (errorCode == OtaController.ERRORCODE.CHECKSUMERROR)
                    errorMsg = mContext.getString(R.string.dfu_error_checksum);
                else if (errorCode == OtaController.ERRORCODE.OPENFILEERROR)
                    errorMsg = mContext.getString(R.string.dfu_error_openfile);
                else if (errorCode == OtaController.ERRORCODE.NODFUSERVICE)
                    errorMsg = mContext.getString(R.string.dfu_error_nofounDFUservice);
                else if (errorCode == OtaController.ERRORCODE.NOFINISHREADVERSION)
                    errorMsg = mContext.getString(R.string.dfu_checking_firmware);
                else
                    errorMsg = mContext.getString(R.string.dfu_error_other);

                Log.e(TAG, errorMsg);
                infomationTextView.setText(errorMsg);
                Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDFUServiceStarted(final String dfuAddress) {
        Log.i(TAG, "onDFUServiceStarted");
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                final DfuServiceInitiator starter = new DfuServiceInitiator(dfuAddress)
                        .setKeepBond(false)
                        .setForceDfu(false)
                        .setPacketsReceiptNotificationsEnabled(true)
                        .setPacketsReceiptNotificationsValue(DfuServiceInitiator.DEFAULT_PRN_VALUE);
                starter.setZip(Common.getBuildInZipFirmwareRawResID(mContext, getModel().getSyncController().getWatchInfomation().getWatchID()));
                Log.i(TAG, "***********dfu library starts DfuService*******" + "address = " + dfuAddress);
                starter.start(mContext, DfuService.class);
            }
        }, 2000);

    }

    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }

    @Override
    public void onBackPressed() {
        if (mNevoOtaController.getState() != Constants.DFUControllerState.INIT) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNevoOtaController.getState() == Constants.DFUControllerState.INIT) {
            mNevoOtaController.reset(true);
        }
    }

    @OnClick(R.id.activity_dfu_back2settings_textview)
    public void backToSettingsClicked(RobotoButton button) {
        if (button.getId() == R.id.activity_dfu_back2settings_textview) {
            //TODO Change to multiple buttons please.
            if (button.getTag().toString().equals(getString(R.string.dfu_back))) {
                finish();
            } else if (button.getTag().toString().equals(getString(R.string.dfu_retry)) || button.getTag().toString().equals(getString(R.string.dfu_continue))) {
                uploadPressed();
            }
        }
    }

    class ButtonTag {
        private String name;

        public ButtonTag(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
