package com.medcorp.lunar.cloud.med;

import android.content.Context;

import com.medcorp.lunar.event.LoginEvent;
import com.medcorp.lunar.event.SignUpEvent;
import com.medcorp.lunar.event.med.MedAddRoutineRecordEvent;
import com.medcorp.lunar.event.med.MedAddSleepRecordEvent;
import com.medcorp.lunar.event.med.MedException;
import com.medcorp.lunar.event.med.MedReadMoreRoutineRecordsModelEvent;
import com.medcorp.lunar.event.med.MedReadMoreSleepRecordsModelEvent;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.listener.ResponseListener;
import com.medcorp.lunar.network.med.manager.MedManager;
import com.medcorp.lunar.network.med.model.CheckWeChatModel;
import com.medcorp.lunar.network.med.model.CreateUser;
import com.medcorp.lunar.network.med.model.CreateUserModel;
import com.medcorp.lunar.network.med.model.CreateWeChatUserModel;
import com.medcorp.lunar.network.med.model.LoginUser;
import com.medcorp.lunar.network.med.model.LoginUserModel;
import com.medcorp.lunar.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.lunar.network.med.model.MedReadMoreSleepRecordsModel;
import com.medcorp.lunar.network.med.model.MedRoutineRecord;
import com.medcorp.lunar.network.med.model.MedRoutineRecordModel;
import com.medcorp.lunar.network.med.model.MedSleepRecord;
import com.medcorp.lunar.network.med.model.MedSleepRecordModel;
import com.medcorp.lunar.network.med.model.UserWeChatInfo;
import com.medcorp.lunar.network.med.model.WeChatLogin;
import com.medcorp.lunar.network.med.model.WeChatLoginModel;
import com.medcorp.lunar.network.med.model.WeChatUserInfoResponse;
import com.medcorp.lunar.network.med.request.routine.AddRoutineRecordRequest;
import com.medcorp.lunar.network.med.request.routine.GetMoreRoutineRecordsRequest;
import com.medcorp.lunar.network.med.request.sleep.AddSleepRecordRequest;
import com.medcorp.lunar.network.med.request.sleep.GetMoreSleepRecordsRequest;
import com.medcorp.lunar.network.med.request.user.CheckWeChatRequest;
import com.medcorp.lunar.network.med.request.user.CreateUserRequest;
import com.medcorp.lunar.network.med.request.user.CreateWeChatRequest;
import com.medcorp.lunar.network.med.request.user.LoginUserRequest;
import com.medcorp.lunar.network.med.request.user.WeChatLoginRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by med on 16/8/22.
 */
public class MedOperation {
    private static MedOperation medOperationInstance = null;
    private MedManager medManager;

    private MedOperation(Context context) {
        medManager = new MedManager(context);
    }

    public static MedOperation getInstance(Context context) {
        if (null == medOperationInstance) {
            medOperationInstance = new MedOperation(context);
        }
        return medOperationInstance;
    }

    public void createMedUser(CreateUser createUser, final RequestListener<CreateUserModel> listener) {
        medManager.execute(new CreateUserRequest(createUser, medManager.getAccessToken()), new RequestListener<CreateUserModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
                EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.FAILED, null));
            }

            @Override
            public void onRequestSuccess(CreateUserModel createUserModel) {
                if (listener != null) {
                    listener.onRequestSuccess(createUserModel);
                }
                if (createUserModel.getStatus() == 1 && createUserModel.getUser() != null) {
                    EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.SUCCESS, createUserModel));
                } else {
                    EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.FAILED, null));
                }
            }
        });
    }

    public void userMedLogin(LoginUser loginUser, final RequestListener<LoginUserModel> listener) {
        final LoginUserRequest loginUserRequest = new LoginUserRequest(loginUser, medManager.getAccessToken());

        medManager.execute(loginUserRequest, new RequestListener<LoginUserModel>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
                EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED, null));
            }

            @Override
            public void onRequestSuccess(LoginUserModel nevoUserModel) {
                if (listener != null) {
                    listener.onRequestSuccess(nevoUserModel);
                }
                if (nevoUserModel.getStatus() == 1) {
                    EventBus.getDefault().post(new LoginEvent(LoginEvent.status.SUCCESS, nevoUserModel));
                } else {
                    EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED, null));
                }
            }
        });
    }

    public void addMedRoutineRecord(final User user, final Steps steps, final Date date, final ResponseListener listener) {
        if (!user.isLogin()) {
            return;
        }
        if (steps.getCreatedDate() == 0) {
            return;
        }
        MedRoutineRecord record = new MedRoutineRecord();
        record.setUid(Integer.parseInt(steps.getNevoUserID()));
        record.setSteps(steps.getHourlySteps());
        record.setCalories(steps.getCalories());
        record.setDistance(steps.getDistance());
        record.setDate(new SimpleDateFormat("yyyy-MM-dd").format(date));
        record.setActive_time(steps.getRunDuration() + steps.getWalkDuration());

        AddRoutineRecordRequest addRecordRequest = new AddRoutineRecordRequest(record, medManager.getAccessToken());
        medManager.execute(addRecordRequest, new RequestListener<MedRoutineRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
                EventBus.getDefault().post(new MedException(spiceException));
            }

            @Override
            public void onRequestSuccess(MedRoutineRecordModel medRoutineRecordModel) {
                if (listener != null) {
                    listener.onRequestSuccess(medRoutineRecordModel);
                }
                if (medRoutineRecordModel.getStatus() == 1) {
                    //save cloud record ID to local database for next cloud sync
                    steps.setCloudRecordID(medRoutineRecordModel.getSteps().getId() + "");
                    EventBus.getDefault().post(new MedAddRoutineRecordEvent(steps));
                }
            }
        });
    }

    public void getMoreMedRoutineRecord(User user, Date startDate, Date endDate, final ResponseListener listener) {
        if (!user.isLogin()) {
            return;
        }
        //use unit in "second"
        long startTimestamp = startDate.getTime() / 1000;
        long endTimeStamps = endDate.getTime() / 1000;

        GetMoreRoutineRecordsRequest getMoreRecordsRequest = new GetMoreRoutineRecordsRequest(medManager.getAccessToken(), user.getNevoUserID(), startTimestamp, endTimeStamps);

        medManager.execute(getMoreRecordsRequest, new RequestListener<MedReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
                spiceException.printStackTrace();
                EventBus.getDefault().post(new MedException(spiceException));
            }

            @Override
            public void onRequestSuccess(MedReadMoreRoutineRecordsModel medReadMoreRoutineRecordsModel) {
                if (listener != null) {
                    listener.onRequestSuccess(medReadMoreRoutineRecordsModel);
                }
                EventBus.getDefault().post(new MedReadMoreRoutineRecordsModelEvent(medReadMoreRoutineRecordsModel));
            }
        });
    }

    //BELOW ARE SLEEP FUNCTIONS
    public void addMedSleepRecord(final User user, final Sleep sleep, Date date, final ResponseListener listener) {
        if (!user.isLogin()) {
            return;
        }
        MedSleepRecord record = new MedSleepRecord();
        record.setUid(Integer.parseInt(user.getNevoUserID()));
        record.setDeep_sleep(sleep.getHourlyDeep());
        record.setLight_sleep(sleep.getHourlyLight());
        record.setWake_time(sleep.getHourlyWake());
        record.setDate(new SimpleDateFormat("yyyy-MM-dd").format(date));

        AddSleepRecordRequest addSleepRecordRequest = new AddSleepRecordRequest(record, medManager.getAccessToken());
        medManager.execute(addSleepRecordRequest, new RequestListener<MedSleepRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
                EventBus.getDefault().post(new MedException(spiceException));
            }

            @Override
            public void onRequestSuccess(MedSleepRecordModel medSleepRecordModel) {
                if (listener != null) {
                    listener.onRequestSuccess(medSleepRecordModel);
                }
                if (medSleepRecordModel.getStatus() == 1) {
                    //save cloud record ID to local database for next cloud sync
                    sleep.setCloudRecordID(medSleepRecordModel.getSleep().getId() + "");
                    EventBus.getDefault().post(new MedAddSleepRecordEvent(sleep));
                }
            }
        });


    }

    public void getMoreMedSleepRecord(User user, Date startDate, Date endDate, final ResponseListener listener) {
        if (!user.isLogin()) {
            return;
        }

        //use unit in "second"
        long startTimestamp = startDate.getTime() / 1000;
        long endTimeStamps = endDate.getTime() / 1000;

        GetMoreSleepRecordsRequest getMoreRecordsRequest = new GetMoreSleepRecordsRequest(medManager.getAccessToken(), user.getNevoUserID(), startTimestamp, endTimeStamps);

        medManager.execute(getMoreRecordsRequest, new RequestListener<MedReadMoreSleepRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
                spiceException.printStackTrace();
                EventBus.getDefault().post(new MedException(spiceException));
            }

            @Override
            public void onRequestSuccess(MedReadMoreSleepRecordsModel medReadMoreSleepRecordsModel) {
                if (listener != null) {
                    listener.onRequestSuccess(medReadMoreSleepRecordsModel);
                }
                EventBus.getDefault().post(new MedReadMoreSleepRecordsModelEvent(medReadMoreSleepRecordsModel));
            }
        });

    }

    public void checkWeChat(WeChatUserInfoResponse userInfo, final RequestListener<CheckWeChatModel> listener) {
        UserWeChatInfo info = new UserWeChatInfo(userInfo.getNickname(), userInfo.getUnionid());
        CheckWeChatRequest loginUserRequest = new CheckWeChatRequest(info, medManager.getAccessToken());
        medManager.execute(loginUserRequest, new ResponseListener<CheckWeChatModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
            }

            @Override
            public void onRequestSuccess(CheckWeChatModel response) {
                if (listener != null) {
                    listener.onRequestSuccess(response);
                }
            }
        });
    }

    public void createWeChatAccount(WeChatUserInfoResponse userInfo, final ResponseListener<CreateWeChatUserModel> listener) {
        UserWeChatInfo info = new UserWeChatInfo(userInfo.getNickname(), userInfo.getUnionid());
        CreateWeChatRequest request = new CreateWeChatRequest(info, medManager.getAccessToken());
        medManager.execute(request, new ResponseListener<CreateWeChatUserModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (listener != null) {
                    listener.onRequestFailure(spiceException);
                }
            }

            @Override
            public void onRequestSuccess(CreateWeChatUserModel createWeChatUserModel) {
                if (listener != null) {
                    listener.onRequestSuccess(createWeChatUserModel);
                }
            }
        });
    }

    public void weChatLogin(WeChatUserInfoResponse userInfo, final ResponseListener<WeChatLoginModel> listener) {
        WeChatLogin user = new WeChatLogin(userInfo.getUnionid());
        WeChatLoginRequest request = new WeChatLoginRequest(user,medManager.getAccessToken());
        medManager.execute(request, new ResponseListener<WeChatLoginModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if(listener != null){
                    listener.onRequestFailure(spiceException);
                }
            }

            @Override
            public void onRequestSuccess(WeChatLoginModel weChatLoginModel) {
                if(listener != null){
                    listener.onRequestSuccess(weChatLoginModel);
                }
            }
        });
    }
}
