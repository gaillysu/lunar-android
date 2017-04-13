package com.medcorp.lunar.cloud;

import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.cloud.med.MedOperation;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.listener.RequestResponseListener;
import com.medcorp.lunar.network.model.request.RegisterNewAccountRequest;
import com.medcorp.lunar.network.model.response.ObtainMoreSleepResponse;
import com.medcorp.lunar.network.model.response.ObtainMoreStepsResponse;
import com.medcorp.lunar.network.model.response.RegisterNewAccountResponse;
import com.medcorp.lunar.network.model.response.UserLoginResponse;
import com.medcorp.lunar.util.Common;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;


/**
 * Created by med on 16/3/23.
 * this class do cloud sync between validic server and local database
 * when do cloud sync operation?
 * case1: syncController big/little sync done
 * case2: user login
 * case3: ???
 * IMPORTANT, IF TOO MANY RECORDS NEED SYNC, PERHAPS SPEND TOO LONG TIME,
 * START AN BACKGROUND TASK TO DO IT IS A GOOD IDEA.
 * how to have a cloud sync?
 * step1: sync local steps & sleep records if their Valid_record_ID is "0","0" is the default value in the steps/sleep local table
 * step2: read validic records between start_date = Date.now() - 365 and end_date =  Date.now(),every time get limit 100 records.
 * step3: check step2 return list, get those records that not found in local database, save them to local database.
 * step4: goto step2, change the page number,read next page until step2 return summary.next is null
 */
public class CloudSyncManager {
    private final String TAG = "CloudSyncManager";
    final long INTERVAL_DATE = 365 * 24 * 60 * 60 * 1000l;//user can get all data in a year
    //here select which one cloud server

    private ApplicationModel context;

    public CloudSyncManager(ApplicationModel context) {
        this.context = context;
    }

    private ApplicationModel getModel() {
        return context;
    }

    public void createUser(RegisterNewAccountRequest createUser) {
        //TODO if enable validic, here open it
        //ValidicOperation.getInstance(context).createValidicUser(...);
        MedOperation.getInstance(context).createMedUser(createUser, new RequestListener<RegisterNewAccountResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                //DO NOTHING
            }

            @Override
            public void onRequestSuccess(RegisterNewAccountResponse createUserModel) {
                if (createUserModel.getStatus() == 1 && createUserModel.getUser() != null) {
                    //save user ID and other profile infomation to local database
                    User nevoUser = getModel().getNevoUser();
                    RegisterNewAccountResponse.UserBean user = createUserModel.getUser();
                    try {
                        nevoUser.setBirthday(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(user.getBirthday().getDate()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    nevoUser.setFirstName(user.getFirst_name());
                    nevoUser.setHeight(user.getLength());
                    nevoUser.setLastName(user.getLast_name());
                    nevoUser.setWeight(user.getWeight());
                    nevoUser.setNevoUserID("" + user.getId());
                    nevoUser.setNevoUserEmail(user.getEmail());
                    getModel().saveNevoUser(nevoUser);
                }
            }
        });
    }

    public void userLogin(String email, String password) {
        //TODO if enable validic, here call ValidicOperation function
        MedOperation.getInstance(context).userMedLogin(email, password, new RequestListener<UserLoginResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(UserLoginResponse loginUserModel) {
                if (loginUserModel.getStatus() == 1) {
                    UserLoginResponse.UserBean user = loginUserModel.getUser();
                    final User nevoUser = getModel().getNevoUser();
                    try {
                        nevoUser.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(user.getBirthday().getDate()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    nevoUser.setFirstName(user.getFirst_name());
                    nevoUser.setHeight(user.getLength());
                    nevoUser.setLastName(user.getLast_name());
                    nevoUser.setWeight(user.getWeight());
                    nevoUser.setNevoUserID("" + user.getId());
                    nevoUser.setNevoUserEmail(user.getEmail());
                    nevoUser.setIsLogin(true);
                    nevoUser.setCreatedDate(new Date().getTime());
                    //save it and sync with watch and cloud server
                    getModel().saveNevoUser(nevoUser);
                    getModel().getSyncController().getDailyTrackerInfo(true);
                    getModel().getNeedSyncSteps(nevoUser.getNevoUserID()).subscribe(new Consumer<List<Steps>>() {
                        @Override
                        public void accept(final List<Steps> stepses) throws Exception {
                            getModel().getNeedSyncSleep(nevoUser.getNevoUserID()).subscribe(new Consumer<List<Sleep>>() {
                                @Override
                                public void accept(List<Sleep> sleeps) throws Exception {
                                    launchSyncAll(nevoUser, stepses, sleeps);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    /**
     * when user login, invoke it
     */
    public void launchSyncAll(User user, List<Steps> stepsList, List<Sleep> sleepList) {
        for (Steps steps : stepsList) {
            MedOperation.getInstance(context).addMedRoutineRecord(user, steps, new Date(steps.getDate()), null);
        }
        //calculate today 's last time: 23:59:59
        Date endDate = new Date(Common.removeTimeFromDate(new Date()).getTime() + 24 * 60 * 60 * 1000l - 1);
        Date startDate = new Date(endDate.getTime() - INTERVAL_DATE);
        downloadSteps(user, startDate, endDate, 1);
        for (Sleep sleep : sleepList) {
            MedOperation.getInstance(context).addMedSleepRecord(user, sleep, new Date(sleep.getDate()), null);
        }
        downloadSleep(user, startDate, endDate, 1);
    }

    private void downloadSteps(final User user, final Date startDate, final Date endDate, final int page) {
        MedOperation.getInstance(context).getMoreMedRoutineRecord(user, startDate, endDate, new RequestResponseListener<ObtainMoreStepsResponse>() {
            @Override
            public void onFailed() {

            }

            @Override
            public void onSuccess(ObtainMoreStepsResponse response) {
                if (response.getStatus() == 1 && response.getSteps() != null && response.getSteps().size() > 0) {
                    Date endDate = new Date(startDate.getTime() - 24 * 60 * 60 * 1000l);
                    Date startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000l);
                    //no page split
                    downloadSteps(user, startDate, endDate, 0);
                }
            }
        });
    }


    private void downloadSleep(final User user, final Date startDate, final Date endDate, final int page) {
        MedOperation.getInstance(context).getMoreMedSleepRecord(user, startDate, endDate, new RequestResponseListener<ObtainMoreSleepResponse>() {
            @Override
            public void onFailed() {

            }

            @Override
            public void onSuccess(ObtainMoreSleepResponse response) {
                if (response.getStatus() == 1 && response.getSleep() != null && response.getSleep().size() > 0) {
                    Date endDate = new Date(startDate.getTime() - 24 * 60 * 60 * 1000l);
                    Date startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000l);
                    //no page split
                    downloadSleep(user, startDate, endDate, 0);
                }
            }
        });
    }

    /**
     * when today's steps got change, invoke it
     */
    public void launchSyncDaily(User user, Steps steps) {
            MedOperation.getInstance(context).addMedRoutineRecord(user, steps, new Date(), null);
    }

    /**
     * when syncController big sync is done, invoke it
     */
    public void launchSyncWeekly(User user, List<Steps> stepsList, List<Sleep> sleepList) {
        for (Steps steps : stepsList) {
                MedOperation.getInstance(context).addMedRoutineRecord(user, steps, new Date(steps.getDate()), null);
        }

        for (Sleep sleep : sleepList) {
                MedOperation.getInstance(context).addMedSleepRecord(user, sleep, new Date(sleep.getDate()), null);
        }
    }
}
