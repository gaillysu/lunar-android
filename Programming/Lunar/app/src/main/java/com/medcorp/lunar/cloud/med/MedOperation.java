package com.medcorp.lunar.cloud.med;

import android.app.Activity;
import android.content.Context;

import com.medcorp.lunar.R;
import com.medcorp.lunar.event.LoginEvent;
import com.medcorp.lunar.event.SignUpEvent;
import com.medcorp.lunar.event.med.MedAddRoutineRecordEvent;
import com.medcorp.lunar.event.med.MedAddSleepRecordEvent;
import com.medcorp.lunar.event.med.MedException;
import com.medcorp.lunar.event.med.MedReadMoreRoutineRecordsModelEvent;
import com.medcorp.lunar.event.med.MedReadMoreSleepRecordsModelEvent;
import com.medcorp.lunar.event.med.UpdateUserInfoEvent;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.listener.ResponseListener;
import com.medcorp.lunar.network.med.manager.MedManager;
import com.medcorp.lunar.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.lunar.network.med.model.MedReadMoreSleepRecordsModel;
import com.medcorp.lunar.network.med.model.MedRoutineRecord;
import com.medcorp.lunar.network.med.model.MedRoutineRecordModel;
import com.medcorp.lunar.network.med.model.MedSleepRecord;
import com.medcorp.lunar.network.med.model.MedSleepRecordModel;
import com.medcorp.lunar.network.med.request.routine.AddRoutineRecordRequest;
import com.medcorp.lunar.network.med.request.routine.GetMoreRoutineRecordsRequest;
import com.medcorp.lunar.network.med.request.sleep.AddSleepRecordRequest;
import com.medcorp.lunar.network.med.request.sleep.GetMoreSleepRecordsRequest;
import com.medcorp.lunar.network_new.httpmanage.HttpManager;
import com.medcorp.lunar.network_new.httpmanage.RequestResponse;
import com.medcorp.lunar.network_new.httpmanage.SubscriberExtends;
import com.medcorp.lunar.network_new.listener.RequestResponseListener;
import com.medcorp.lunar.network_new.modle.request.ChangePasswordRequest;
import com.medcorp.lunar.network_new.modle.request.CheckEmailRequest;
import com.medcorp.lunar.network_new.modle.request.RegisterNewAccountRequest;
import com.medcorp.lunar.network_new.modle.request.RequestForgotPasswordTokenRequest;
import com.medcorp.lunar.network_new.modle.request.UpdateAccountInformationRequest;
import com.medcorp.lunar.network_new.modle.request.UserLoginRequest;
import com.medcorp.lunar.network_new.modle.request.WeChatAccountCheckRequest;
import com.medcorp.lunar.network_new.modle.request.WeChatAccountRegisterRequest;
import com.medcorp.lunar.network_new.modle.request.WeChatLoginRequest;
import com.medcorp.lunar.network_new.modle.response.ChangePasswordResponse;
import com.medcorp.lunar.network_new.modle.response.CheckEmailResponse;
import com.medcorp.lunar.network_new.modle.response.CheckWeChatAccountResponse;
import com.medcorp.lunar.network_new.modle.response.CreateWeChatAccountResponse;
import com.medcorp.lunar.network_new.modle.response.RegisterNewAccountResponse;
import com.medcorp.lunar.network_new.modle.response.RequestForgotPasswordResponse;
import com.medcorp.lunar.network_new.modle.response.UpdateAccountInformationResponse;
import com.medcorp.lunar.network_new.modle.response.UserLoginResponse;
import com.medcorp.lunar.network_new.modle.response.WeChatLoginResponse;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;

/**
 * Created by med on 16/8/22.
 */
@SuppressWarnings("unchecked")
public class MedOperation {
    private static MedOperation medOperationInstance = null;
    private MedManager medManager;
    private HttpManager httpManager;
    private Context mContext;

    private MedOperation(Context context) {
        httpManager = HttpManager.getInstance(context);
        medManager = new MedManager(context);
        mContext = context;
    }

    public static MedOperation getInstance(Context context) {
        if (null == medOperationInstance) {
            medOperationInstance = new MedOperation(context);
        }
        return medOperationInstance;
    }

    public void createMedUser(RegisterNewAccountRequest createUser, final RequestListener<RegisterNewAccountResponse> listener) {

        Observable<RegisterNewAccountResponse> registerNewAccountRequestObservable = httpManager.createApiService()
                .registerNewAccount(HttpManager.createRequestBody(mContext.getString(R.string.network_token), createUser));

        httpManager.toSubscribe(mContext, registerNewAccountRequestObservable
                , SubscriberExtends.getInstance().getSubscriber(new RequestResponse<RegisterNewAccountResponse>() {
                    @Override
                    public void onFailure(Throwable e) {
                        EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.FAILED, null));
                    }

                    @Override
                    public void onSuccess(RegisterNewAccountResponse createUserModel) {
                        if (listener != null) {
                            listener.onRequestSuccess(createUserModel);
                        }
                        if (createUserModel.getStatus() == 1 && createUserModel.getUser() != null) {
                            EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.SUCCESS, createUserModel));
                        } else {
                            EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.FAILED, null));
                        }
                    }
                }));
    }

    public void userMedLogin(String userEmail, String password, final RequestListener<UserLoginResponse> listener) {
        UserLoginRequest userLogin = new UserLoginRequest(userEmail, password);
        Observable<UserLoginResponse> userLoginResponseObservable = httpManager.createApiService()
                .userLogin(HttpManager.createRequestBody(mContext.getString(R.string.network_token), userLogin));
        httpManager.toSubscribe(mContext, userLoginResponseObservable,
                SubscriberExtends.getInstance().getSubscriber(
                        new RequestResponse<UserLoginResponse>() {
                            @Override
                            public void onFailure(Throwable e) {
                                EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED, null));
                            }

                            @Override
                            public void onSuccess(UserLoginResponse response) {
                                if (listener != null) {
                                    listener.onRequestSuccess(response);
                                }
                                if (response.getStatus() == 1) {
                                    EventBus.getDefault().post(new LoginEvent(LoginEvent.status.SUCCESS, response));
                                } else {
                                    EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED, null));
                                }
                            }
                        }));
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

    public void checkWeChat(final Activity context, WeChatAccountCheckRequest request, final RequestResponseListener<CheckWeChatAccountResponse> listener) {
        Observable<CheckWeChatAccountResponse> response = httpManager.createApiService().checkWeChatAccount
                (HttpManager.createRequestBody(mContext.getString(R.string.network_token), request));
        httpManager.toSubscribe(mContext, response, SubscriberExtends.getInstance().getSubscriber(new RequestResponse<CheckWeChatAccountResponse>() {
            @Override
            public void onFailure(Throwable e) {
                if (context != null && listener != null) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed();
                        }
                    });
                }
            }

            @Override
            public void onSuccess(final CheckWeChatAccountResponse response) {
                if (context != null && listener != null) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(response);
                        }
                    });
                }
            }
        }));
    }

    public void createWeChatAccount(final Activity context, WeChatAccountRegisterRequest registerRequest,
                                    final RequestResponseListener<CreateWeChatAccountResponse> listener) {
        Observable<CreateWeChatAccountResponse> weChatAccount = httpManager.createApiService().createWeChatAccount
                (HttpManager.createRequestBody(mContext.getString(R.string.network_token), registerRequest));
        httpManager.toSubscribe(mContext, weChatAccount, SubscriberExtends.getInstance().getSubscriber(
                new RequestResponse<CreateWeChatAccountResponse>() {
                    @Override
                    public void onFailure(Throwable e) {
                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onFailed();
                                }
                            });
                        }
                    }

                    @Override
                    public void onSuccess(final CreateWeChatAccountResponse o) {
                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(o);
                                }
                            });
                        }
                    }
                }));
    }

    public void weChatLogin(final Activity activity, WeChatLoginRequest request,
                            final RequestResponseListener<WeChatLoginResponse> listener) {
        Observable<WeChatLoginResponse> response = httpManager.createApiService().weChatLogin(HttpManager.createRequestBody(
                mContext.getString(R.string.network_token), request));
        httpManager.toSubscribe(mContext,response,SubscriberExtends.getInstance().getSubscriber(new RequestResponse<WeChatLoginResponse>() {
            @Override
            public void onFailure(Throwable e) {
                if(activity!=null && listener!=null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed();
                        }
                    });
                }
            }

            @Override
            public void onSuccess(final WeChatLoginResponse o) {
                if(activity!=null && listener!=null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(o);
                        }
                    });
                }
            }
        }));
    }

    public void updateUserInformation(UpdateAccountInformationRequest request) {
        Observable<UpdateAccountInformationResponse> updateAccountInformationResponseObservable = httpManager.createApiService().updateInformation(HttpManager.createRequestBody(
                mContext.getString(R.string.network_token), request));
        httpManager.toSubscribe(mContext, updateAccountInformationResponseObservable, SubscriberExtends.getInstance()
                .getSubscriber(new RequestResponse<UpdateAccountInformationResponse>() {
                    @Override
                    public void onFailure(Throwable e) {
                        EventBus.getDefault().post(new UpdateUserInfoEvent(false));
                    }

                    @Override
                    public void onSuccess(UpdateAccountInformationResponse o) {
                        if (o.getStatus() == 1) {
                            EventBus.getDefault().post(new UpdateUserInfoEvent(true));
                        } else {
                            EventBus.getDefault().post(new UpdateUserInfoEvent(false));
                        }
                    }
                }));

    }

    public void obtainPasswordToken(final Activity context, RequestForgotPasswordTokenRequest request,
                                    final RequestResponseListener listener) {
        Observable<RequestForgotPasswordResponse> response = httpManager.createApiService().obtainPasswordToken(
                HttpManager.createRequestBody(mContext.getString(R.string.network_token), request));
        httpManager.toSubscribe(mContext, response, SubscriberExtends.getInstance().getSubscriber(
                new RequestResponse<RequestForgotPasswordResponse>() {
                    @Override
                    public void onFailure(Throwable e) {
                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onFailed();
                                }
                            });
                        }
                    }

                    @Override
                    public void onSuccess(final RequestForgotPasswordResponse response) {
                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(response);
                                }
                            });
                        }
                    }
                }));
    }

    public void changePassword(final Activity context, ChangePasswordRequest request, final RequestResponseListener listener) {
        Observable<ChangePasswordResponse> changePasswordResponseObservable = httpManager.createApiService()
                .changePassword(HttpManager.createRequestBody(mContext.getString(R.string.network_token), request));
        httpManager.toSubscribe(mContext, changePasswordResponseObservable, SubscriberExtends.getInstance()
                .getSubscriber(new RequestResponse<ChangePasswordResponse>() {
                    @Override
                    public void onFailure(Throwable e) {
                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onFailed();
                                }
                            });
                        }
                    }

                    @Override
                    public void onSuccess(final ChangePasswordResponse o) {

                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(o);
                                }
                            });
                        }
                    }
                }));
    }

    public void checkEmail(final Activity context, CheckEmailRequest request, final RequestResponseListener<CheckEmailResponse> listener) {
        Observable<CheckEmailResponse> checkEmailResponse = httpManager.createApiService().checkAccount(
                HttpManager.createRequestBody(mContext.getString(R.string.network_token), request));
        httpManager.toSubscribe(mContext, checkEmailResponse, SubscriberExtends.getInstance().getSubscriber(
                new RequestResponse<CheckEmailResponse>() {
                    @Override
                    public void onFailure(Throwable e) {
                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onFailed();
                                }
                            });
                        }
                    }

                    @Override
                    public void onSuccess(final CheckEmailResponse o) {
                        if (context != null && listener != null) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(o);
                                }
                            });
                        }
                    }
                }));

    }
}
