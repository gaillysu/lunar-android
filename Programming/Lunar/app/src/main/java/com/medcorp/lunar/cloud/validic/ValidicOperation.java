package com.medcorp.lunar.cloud.validic;

import android.content.Context;
import android.util.Log;

import com.medcorp.lunar.event.validic.ValidicAddRoutineRecordEvent;
import com.medcorp.lunar.event.validic.ValidicAddSleepRecordEvent;
import com.medcorp.lunar.event.validic.ValidicCreateUserEvent;
import com.medcorp.lunar.event.validic.ValidicDeleteRoutineRecordEvent;
import com.medcorp.lunar.event.validic.ValidicDeleteSleepRecordModelEvent;
import com.medcorp.lunar.event.validic.ValidicException;
import com.medcorp.lunar.event.validic.ValidicReadMoreRoutineRecordsModelEvent;
import com.medcorp.lunar.event.validic.ValidicReadMoreSleepRecordsModelEvent;
import com.medcorp.lunar.event.validic.ValidicUpdateRoutineRecordsModelEvent;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.listener.ResponseListener;
import com.medcorp.lunar.network.validic.manager.ValidicManager;
import com.medcorp.lunar.network.validic.model.CreateUserRequestObject;
import com.medcorp.lunar.network.validic.model.CreateUserRequestObjectUser;
import com.medcorp.lunar.network.validic.model.NevoHourlySleepData;
import com.medcorp.lunar.network.validic.model.RoutineGoal;
import com.medcorp.lunar.network.validic.model.ValidicDeleteRoutineRecordModel;
import com.medcorp.lunar.network.validic.model.ValidicDeleteSleepRecordModel;
import com.medcorp.lunar.network.validic.model.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.lunar.network.validic.model.ValidicReadMoreSleepRecordsModel;
import com.medcorp.lunar.network.validic.model.ValidicRoutineRecord;
import com.medcorp.lunar.network.validic.model.ValidicRoutineRecordModel;
import com.medcorp.lunar.network.validic.model.ValidicSleepRecord;
import com.medcorp.lunar.network.validic.model.ValidicSleepRecordModel;
import com.medcorp.lunar.network.validic.model.ValidicUser;
import com.medcorp.lunar.network.validic.model.VerifyCredentialModel;
import com.medcorp.lunar.network.validic.request.VerifyCredentialsRetroRequest;
import com.medcorp.lunar.network.validic.request.routine.AddRoutineRecordRequest;
import com.medcorp.lunar.network.validic.request.routine.DeleteRoutineRecordRequest;
import com.medcorp.lunar.network.validic.request.routine.GetMoreRoutineRecordsRequest;
import com.medcorp.lunar.network.validic.request.routine.UpdateRoutineRecordRequest;
import com.medcorp.lunar.network.validic.request.sleep.AddSleepRecordRequest;
import com.medcorp.lunar.network.validic.request.sleep.DeleteSleepRecordRequest;
import com.medcorp.lunar.network.validic.request.sleep.GetMoreSleepRecordsRequest;
import com.medcorp.lunar.network.validic.request.user.CreateUserRetroRequest;
import com.medcorp.lunar.util.Common;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by med on 16/8/22.
 */
public class ValidicOperation {
    static private ValidicOperation validicOperationInstance = null;
    private ValidicManager validicManager;

    private ValidicOperation(Context context) {
        validicManager = new ValidicManager(context);
    }

    public static ValidicOperation getInstance(Context context){
        if(validicOperationInstance==null)
        {
            validicOperationInstance = new ValidicOperation(context);
        }
        return validicOperationInstance;
    }

    public void addValidicRoutineRecord(final User user, final Steps steps, final Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }
        if(steps.getCreatedDate()==0)
        {
            return;
        }
        ValidicRoutineRecord record = new ValidicRoutineRecord();
        record.setSteps(steps.getSteps());

        String utc_offset = new SimpleDateFormat("z").format(date).substring(3);
        Date theDay = Common.removeTimeFromDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp  = sdf.format(theDay);

        record.setTimestamp(timestamp);
        record.setUtc_offset(utc_offset);
        record.setDistance(steps.getDistance());
        record.setCalories_burned(steps.getCalories());
        record.setActivity_id("" + steps.getiD());
        RoutineGoal goal = new RoutineGoal();
        goal.setGoal(steps.getGoal());
        record.setExtras(goal);

        AddRoutineRecordRequest addRecordRequest = new AddRoutineRecordRequest(record,validicManager.getOrganizationID(),validicManager.getOrganizationToken(),user.getValidicUserID());
        validicManager.execute(addRecordRequest, new RequestListener<ValidicRoutineRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                String causeString = spiceException.getCause()==null?"":spiceException.getCause().getLocalizedMessage()+"";
                if(causeString.contains("409") || causeString.contains("422"))
                {
                    //409:Activity is already taken
                    //422:Timestamp is already taken
                    updateValidicRoutineRecord(user, steps, date,listener);
                }
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicRoutineRecordModel validicRecordModel) {
                if (validicRecordModel.getCode().equals("200") || validicRecordModel.getCode().equals("201"))
                {
                    //save validic record ID to local database, for using cloud sync
                    steps.setCloudRecordID(validicRecordModel.getRoutine().get_id());
                    EventBus.getDefault().post(new ValidicAddRoutineRecordEvent(steps));
                }
            }
        });
    }

    private void updateValidicRoutineRecord(final User user, final Steps steps, Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }

        getValidicRoutineRecord(user, date, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
                if (validicReadMoreRoutineRecordsModel.getSummary().getResults() > 0) {
                    String validicRecordID = validicReadMoreRoutineRecordsModel.getRoutine()[0].get_id();
                    //if local data == cloud data, save record_id and return
                    if(steps.getSteps()==validicReadMoreRoutineRecordsModel.getRoutine()[0].getSteps())
                    {
                        steps.setCloudRecordID(validicReadMoreRoutineRecordsModel.getRoutine()[0].get_id());
                        EventBus.getDefault().post(new ValidicUpdateRoutineRecordsModelEvent(steps));
                        return;
                    }
                    UpdateRoutineRecordRequest updateRecordRequest = new UpdateRoutineRecordRequest(validicManager.getOrganizationID(), validicManager.getOrganizationToken(), user.getValidicUserID(), validicRecordID, steps.getSteps());
                    validicManager.execute(updateRecordRequest, new RequestListener<ValidicRoutineRecordModel>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            EventBus.getDefault().post(new ValidicException(spiceException));
                        }

                        @Override
                        public void onRequestSuccess(ValidicRoutineRecordModel validicRecordModel) {
                            //save validic record ID to local database, for using cloud sync

                            steps.setCloudRecordID(validicRecordModel.getRoutine().get_id());
                            EventBus.getDefault().post(new ValidicUpdateRoutineRecordsModelEvent(steps));
                        }
                    });
                }
            }
        });
    }

    private void getValidicRoutineRecord(User user, Date date, final ResponseListener listener)
    {
        getMoreValidicRoutineRecord(user, date, date,1,listener);
    }

    public void getMoreValidicRoutineRecord(User user, Date startDate, Date endDate, int page, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }

        String startTimestamp = Common.getUTCTimestampFromLocalDate(startDate);
        String endTimeStamps = Common.getUTCTimestampFromLocalDate(endDate);

        GetMoreRoutineRecordsRequest getMoreRecordsRequest = new GetMoreRoutineRecordsRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),user.getValidicUserID(),startTimestamp,endTimeStamps,page);

        validicManager.execute(getMoreRecordsRequest, new RequestListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
                if(listener!=null) {
                    listener.onRequestFailure(spiceException);
                }
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadAllRecordsModel) {
                EventBus.getDefault().post(new ValidicReadMoreRoutineRecordsModelEvent(validicReadAllRecordsModel));
                if(listener!=null){
                    listener.onRequestSuccess(validicReadAllRecordsModel);
                }
            }
        });
    }

    public void addValidicSleepRecord(final User user, final Sleep sleep, Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }

        ValidicSleepRecord record = new ValidicSleepRecord();
        record.setActivity_id("" + sleep.getiD());
        //validic sleep object , the value is  in seconds
        record.setAwake(60 * sleep.getTotalWakeTime());
        record.setLight(60 * sleep.getTotalLightTime());
        record.setDeep(60 * sleep.getTotalDeepTime());
        record.setTotal_sleep(60 * sleep.getTotalSleepTime());
        //TODO how to calculate the woken times? by hourly wake time is not zero?
        record.setTimes_woken(0);
        //REM value is set 0, nevo doesn't give this data
        record.setRem(0);
        NevoHourlySleepData nevoHourlySleepData = new NevoHourlySleepData();
        nevoHourlySleepData.setHourlyWake(sleep.getHourlyWake());
        nevoHourlySleepData.setHourlyLight(sleep.getHourlyLight());
        nevoHourlySleepData.setHourlyDeep(sleep.getHourlyDeep());
        record.setExtras(nevoHourlySleepData);

        String utc_offset = new SimpleDateFormat("z").format(date).substring(3);
        Date theDay = Common.removeTimeFromDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp  = sdf.format(theDay);
        record.setTimestamp(timestamp);
        record.setUtc_offset(utc_offset);

        AddSleepRecordRequest addSleepRecordRequest = new AddSleepRecordRequest(record,validicManager.getOrganizationID(), validicManager.getOrganizationToken(),user.getValidicUserID());
        validicManager.execute(addSleepRecordRequest, new RequestListener<ValidicSleepRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                String causeString = spiceException.getCause()==null?"":spiceException.getCause().getLocalizedMessage()+"";
                if (causeString.contains("409")||causeString.contains("422")) {
                    //DO NOTHING, sleep record can't be update
                }
                EventBus.getDefault().post(new ValidicException(spiceException));
            }
            @Override
            public void onRequestSuccess(ValidicSleepRecordModel validicSleepRecordModel) {
                if(validicSleepRecordModel.getCode().equals("200")||validicSleepRecordModel.getCode().equals("201"))
                {
                    sleep.setCloudRecordID(validicSleepRecordModel.getSleep().get_id());
                    EventBus.getDefault().post(new ValidicAddSleepRecordEvent(sleep));
                }
            }
        });
    }

    private void getValidicSleepRecord(User user, Date date, final ResponseListener listener)
    {
        getMoreValidicSleepRecord(user, date, date, 1, listener);
    }

    public void getMoreValidicSleepRecord(User user, Date startDate, Date endDate, int page, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }
        String startTimestamp = Common.getUTCTimestampFromLocalDate(startDate);
        String endTimeStamp = Common.getUTCTimestampFromLocalDate(endDate);

        GetMoreSleepRecordsRequest getMoreRecordsRequest = new GetMoreSleepRecordsRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(), user.getValidicUserID(),startTimestamp,endTimeStamp,page);

        validicManager.execute(getMoreRecordsRequest, new RequestListener<ValidicReadMoreSleepRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
                if(listener!=null) {
                    listener.onRequestFailure(spiceException);
                }
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
                // TODO catch this if needed.
                EventBus.getDefault().post(new ValidicReadMoreSleepRecordsModelEvent(validicReadMoreSleepRecordsModel));
                if(listener!=null) {
                    listener.onRequestSuccess(validicReadMoreSleepRecordsModel);
                }
            }
        });
    }

    public void deleteValidicSleepRecord(final User user, final Sleep sleep, final Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }
        String validicRecordID = sleep.getCloudRecordID();
        if(validicRecordID.equals("0")) {
            return;
        }
      DeleteSleepRecordRequest deleteRecordRequest = new DeleteSleepRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(), user.getValidicUserID(), validicRecordID);

        validicManager.execute(deleteRecordRequest, new RequestListener<ValidicDeleteSleepRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicDeleteSleepRecordModel validicDeleteSleepRecordModel) {
                if(validicDeleteSleepRecordModel.getCode().equals("200") || validicDeleteSleepRecordModel.getCode().equals("201"))
                {
                    EventBus.getDefault().post(new ValidicDeleteSleepRecordModelEvent(user.getId(),date));
                }

            }
        });
    }

    public void deleteValidicRoutineRecord(final User user, Steps steps, final Date date, final ResponseListener listener)
    {
        if(user.isLogin()||!user.isConnectValidic()){
            return;
        }
        String validicRecordID = steps.getCloudRecordID();
        if(validicRecordID.equals("0")) {
            return;
        }

        DeleteRoutineRecordRequest deleteRecordRequest = new DeleteRoutineRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),user.getValidicUserID(), validicRecordID);

        validicManager.execute(deleteRecordRequest, new RequestListener<ValidicDeleteRoutineRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicDeleteRoutineRecordModel validicDeleteRecordModel) {
                if(validicDeleteRecordModel.getCode().equals("200") || validicDeleteRecordModel.getCode().equals("201"))
                {
                    EventBus.getDefault().post(new ValidicDeleteRoutineRecordEvent(user.getId(), date));
                }
            }
        });
    }


    public void verifyValidicCredential()
    {
        VerifyCredentialsRetroRequest request = new VerifyCredentialsRetroRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken());
        validicManager.execute(request, new RequestListener<VerifyCredentialModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
            }

            @Override
            public void onRequestSuccess(VerifyCredentialModel model) {
                Log.w("Karl", "Success, model = " + model.toString());
            }
        });
    }

    public void createValidicUser(final User nevoUser, String pinCode, final ResponseListener listener)
    {
        if(!nevoUser.isLogin()){
            return;
        }
        CreateUserRequestObject object = new CreateUserRequestObject();
        object.setPin(pinCode);
        object.setAccess_token(validicManager.getOrganizationToken());
        CreateUserRequestObjectUser user  = new CreateUserRequestObjectUser();
        user.setUid(nevoUser.getNevoUserToken());
        object.setUser(user);
        CreateUserRetroRequest request = new CreateUserRetroRequest(validicManager.getOrganizationID(), object);

        validicManager.execute(request, new RequestListener<ValidicUser>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicUser validicUser) {
                nevoUser.setValidicUserID(validicUser.getUser().get_id());
                nevoUser.setValidicUserToken(validicUser.getUser().getAccess_token());
                nevoUser.setIsConnectValidic(true);
                EventBus.getDefault().post(new ValidicCreateUserEvent(nevoUser));
            }
        });
    }

}
