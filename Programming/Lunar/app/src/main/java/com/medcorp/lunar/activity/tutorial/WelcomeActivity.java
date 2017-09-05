package com.medcorp.lunar.activity.tutorial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.activity.login.SignupActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedNetworkOperation;
import com.medcorp.lunar.event.ReturnUserInfoEvent;
import com.medcorp.lunar.event.WeChatEvent;
import com.medcorp.lunar.event.WeChatTokenEvent;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.listener.RequestResponseListener;
import com.medcorp.lunar.network.model.request.CheckEmailRequest;
import com.medcorp.lunar.network.model.request.CreateFacebookAccountRequest;
import com.medcorp.lunar.network.model.request.FaceBookAccountLoginRequest;
import com.medcorp.lunar.network.model.request.WeChatAccountCheckRequest;
import com.medcorp.lunar.network.model.request.WeChatAccountRegisterRequest;
import com.medcorp.lunar.network.model.request.WeChatLoginRequest;
import com.medcorp.lunar.network.model.response.CheckEmailResponse;
import com.medcorp.lunar.network.model.response.CheckWeChatAccountResponse;
import com.medcorp.lunar.network.model.response.CreateFacebookAccountResponse;
import com.medcorp.lunar.network.model.response.CreateWeChatAccountResponse;
import com.medcorp.lunar.network.model.response.FacebookLoginResponse;
import com.medcorp.lunar.network.model.response.FacebookUserInfoResponse;
import com.medcorp.lunar.network.model.response.WeChatLoginResponse;
import com.medcorp.lunar.network.model.response.WeChatUserInfoResponse;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.PublicUtils;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

import net.medcorp.library.ble.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/***
 * Created by Jason on 2017/6/16.
 */

public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.welcome_activity_root_view)
    RelativeLayout relativeLayout;
    @Bind(R.id.login_skip_bt)
    Button skipBt;

    private Snackbar snackbar;
    private ProgressDialog progressDialog;
    private IWXAPI weChatApi;
    private String APP_ID;
    private CallbackManager mCallbackManager;
    private static final int REQUEST_SIGN_UP = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.log_in_popup_message));
        registerFacebookCallBack();
        initView();
    }

    private void initView() {
        if (!PublicUtils.isLocaleChinese()) {
            SpannableString span = new SpannableString(getString(R.string.welcome_activity_skip_login));
            String describe = getString(R.string.welcome_activity_skip_login);
            int index = describe.indexOf(getString(R.string.other_color_gray));
            ForegroundColorSpan textColor = new ForegroundColorSpan(getResources().getColor(R.color.welcome_other_text_color));
            span.setSpan(textColor, index, index + getString(R.string.other_color_gray).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            skipBt.setText(span);
        }
    }

    @OnClick(R.id.create_account_bt)
    public void startRegister() {
        startActivity(SignupActivity.class);
        finish();
    }

    @OnClick(R.id.login_bt)
    public void startLogin() {
        startActivity(LoginActivity.class);
        finish();
    }

    @OnClick(R.id.login_skip_bt)
    public void skipLogin() {
        if (Preferences.getIsFirstLogin(this)) {
            startActivity(FirstThingsActivity.class);
        }else{
            Preferences.saveIsFirstLogin(this, false);
            if (getIntent().getBooleanExtra(getString(R.string.open_activity_is_tutorial), true)
                    && !getModel().isWatchConnected()) {
                startActivity(TutorialPage1Activity.class);
            } else {
                startActivity(MainActivity.class);
            }
        }
        finish();
    }


    @OnClick(R.id.welcome_activity_wechat_login_bt)
    public void weChatLogin() {
        regToWx();
        if (!weChatApi.isWXAppInstalled()) {
            showSnackbar(getString(R.string.wechat_uninstall));
            return;
        }
        SendAuth.Req request = new SendAuth.Req();
        request.scope = getString(R.string.weixin_scope);
        request.state = getString(R.string.weixin_package_name);
        weChatApi.sendReq(request);
    }


    private void regToWx() {
        APP_ID = getString(R.string.we_chat_app_id);
        weChatApi = getModel().getWXApi();
        weChatApi.registerApp(APP_ID);
    }

    @Subscribe
    public void weChatErrorEvent(WeChatEvent event) {
        showSnackbar(getString(R.string.network_error));
    }

    @Subscribe
    public void weChatEvent(final WeChatTokenEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (event != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    progressDialog.show();
                    getModel().getWeChatToken(event.getCode());
                }
            }
        });
    }

    @Subscribe
    public void userInfoEvent(final ReturnUserInfoEvent event) {
        if (event != null) {
            final WeChatUserInfoResponse userInfo = event.getUserInfo();
            WeChatAccountCheckRequest request = new WeChatAccountCheckRequest(userInfo.getNickname(), userInfo.getUnionid());
            MedNetworkOperation.getInstance(this).checkWeChat(this, request, new RequestResponseListener<CheckWeChatAccountResponse>() {
                @Override
                public void onFailed() {
                    showSnackbar(getString(R.string.check_wechat_fail));
                    progressDialog.dismiss();
                }

                @Override
                public void onSuccess(CheckWeChatAccountResponse response) {
                    if (response.getStatus() <= 0) {
                        createWeChatUser(userInfo);
                    } else if (response.getStatus() == 1) {
                        WeChatLoginRequest request = new WeChatLoginRequest(userInfo.getUnionid());
                        weChatStartLogin(request);
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            showSnackbar(getString(R.string.wechat_login_fail));
        }
    }

    private void createWeChatUser(final WeChatUserInfoResponse userInfo) {
        WeChatAccountRegisterRequest request = new WeChatAccountRegisterRequest(userInfo.getNickname(), userInfo.getUnionid());
        MedNetworkOperation.getInstance(this).createWeChatAccount(this, request, new RequestResponseListener<CreateWeChatAccountResponse>() {
            @Override
            public void onFailed() {
                showSnackbar(getString(R.string.wechat_create_account_fail));
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(CreateWeChatAccountResponse response) {
                if (response.getStatus() == 1) {
                    WeChatLoginRequest request = new WeChatLoginRequest(userInfo.getUnionid());
                    weChatStartLogin(request);
                } else {
                    showSnackbar(response.getMessage());
                }
            }
        });
    }

    private void weChatStartLogin(WeChatLoginRequest request) {
        MedNetworkOperation.getInstance(this).weChatLogin(this, request, new RequestResponseListener<WeChatLoginResponse>() {
            @Override
            public void onFailed() {
                showSnackbar(getString(R.string.wechat_login_fail));
            }

            @Override
            public void onSuccess(final WeChatLoginResponse response) {
                if (response.getStatus() == 1) {
                    getModel().getUser().subscribe(new Consumer<User>() {
                        @Override
                        public void accept(final User lunarUser) throws Exception {
                            WeChatLoginResponse.UserBean user = response.getUser();
                            lunarUser.setFirstName(user.getFirst_name());
                            lunarUser.setUserID("" + user.getId());
                            lunarUser.setWechat(user.getWechat());
                            lunarUser.setIsLogin(true);
                            lunarUser.setCreatedDate(new Date().getTime());
                            //save it and sync with watch and cloud server
                            getModel().saveUser(lunarUser);
                            getModel().getSyncController().getDailyTrackerInfo(true);
                            getModel().getNeedSyncSteps(lunarUser.getUserID()).subscribe(new Consumer<List<Steps>>() {
                                @Override
                                public void accept(final List<Steps> stepses) throws Exception {
                                    getModel().getNeedSyncSleep(lunarUser.getUserID()).subscribe(new Consumer<List<Sleep>>() {
                                        @Override
                                        public void accept(List<Sleep> sleeps) throws Exception {
                                            getModel().getCloudSyncManager().launchSyncAll(lunarUser, stepses, sleeps);
                                        }
                                    });
                                }
                            });
                            onLoginSuccess();

                        }
                    });
                } else {
                    onFailed();
                    showSnackbar(getString(R.string.wechat_login_fail));
                }
            }
        });
    }

    @OnClick(R.id.welcome_activity_facebook_login)
    public void facebookLogin() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList(getString(R.string.facebook_public_profile)
                        , getString(R.string.facebook_user_email), getString(R.string.facebook_user_birthday)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_UP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }

    private void obtainFacebookProfile(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            Gson gson = new Gson();
                            FacebookUserInfoResponse rsp = gson.fromJson
                                    (object.toString(), FacebookUserInfoResponse.class);
                            checkFacebookEmail(rsp);
                        } else {
                            showSnackbar(getString(R.string.obtain_facebook_info_failed));
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString(getString(R.string.facebook_user_file), getString(R.string.facebook_user_files));
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void registerFacebookCallBack() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                obtainFacebookProfile(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                showSnackbar(error.getMessage());
            }
        });
    }

    private void checkFacebookEmail(final FacebookUserInfoResponse userInfoResponse) {
        progressDialog.show();
        final CheckEmailRequest request = new CheckEmailRequest(userInfoResponse.getEmail());
        MedNetworkOperation.getInstance(this).checkEmail(this, request, new RequestResponseListener<CheckEmailResponse>() {
            @Override
            public void onFailed() {
            }

            @Override
            public void onSuccess(CheckEmailResponse response) {
                if (response.getStatus() == 1) {
                    FaceBookAccountLoginRequest loginRequest = new FaceBookAccountLoginRequest
                            (userInfoResponse.getEmail(), userInfoResponse.getId());
                    facebookLogin(loginRequest);
                } else {
                    createFacebookAccount(userInfoResponse);
                }
            }
        });
    }

    private void createFacebookAccount(FacebookUserInfoResponse userInfoResponse) {
        Profile currentProfile = Profile.getCurrentProfile();
        int sex = 0;
        if (userInfoResponse.getGender().equals(getString(R.string.user_gender))) {
            sex = 1;
        } else {
            sex = 0;
        }
        String[] birthday = userInfoResponse.getBirthday().split("/");
        final CreateFacebookAccountRequest request = new CreateFacebookAccountRequest(currentProfile.getFirstName()
                , userInfoResponse.getEmail(), currentProfile.getId(), birthday[2] + "-" +
                birthday[0] + "-" + birthday[1], 170, 55, sex);
        MedNetworkOperation.getInstance(this).createFacebookUser(request,
                new RequestResponseListener<CreateFacebookAccountResponse>() {
                    @Override
                    public void onFailed() {
                    }

                    @Override
                    public void onSuccess(final CreateFacebookAccountResponse response) {
                        if (response.getStatus() == 1) {
                            FaceBookAccountLoginRequest request = new FaceBookAccountLoginRequest
                                    (response.getUser().getEmail(), response.getUser().getFacebook_id());
                            facebookLogin(request);
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    showSnackbar(response.getMessage());
                                }
                            });
                        }
                    }
                });
    }

    private void facebookLogin(FaceBookAccountLoginRequest loginRequest) {
        MedNetworkOperation.getInstance(this).facebookLogin(loginRequest,
                new RequestResponseListener<FacebookLoginResponse>() {
                    @Override
                    public void onFailed() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showSnackbar(getString(R.string.log_in_failed));
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final FacebookLoginResponse response) {
                        progressDialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getStatus() == 1) {
                                    getModel().getUser().subscribe(new Consumer<User>() {
                                        @Override
                                        public void accept(final User lunarUser) throws Exception {
                                            FacebookLoginResponse.UserBean user = response.getUser();
                                            lunarUser.setFirstName(user.getFirst_name());
                                            lunarUser.setUserID("" + user.getId());
                                            lunarUser.setUserEmail(user.getEmail());
                                            lunarUser.setIsLogin(true);
                                            lunarUser.setCreatedDate(new Date().getTime());
                                            //save it and sync with watch and cloud server
                                            getModel().saveUser(lunarUser);
                                            getModel().getSyncController().getDailyTrackerInfo(true);
                                            getModel().getNeedSyncSteps(lunarUser.getUserID())
                                                    .subscribe(new Consumer<List<Steps>>() {
                                                        @Override
                                                        public void accept(final List<Steps> stepses) throws Exception {
                                                            getModel().getNeedSyncSleep(lunarUser.getUserID())
                                                                    .subscribe(new Consumer<List<Sleep>>() {
                                                                        @Override
                                                                        public void accept(List<Sleep> sleeps) throws Exception {
                                                                            getModel().getCloudSyncManager().launchSyncAll(lunarUser, stepses
                                                                                    , sleeps);
                                                                        }
                                                                    });
                                                        }
                                                    });
                                            onLoginSuccess();
                                        }
                                    });
                                } else {
                                    showSnackbar(getString(R.string.log_in_failed));
                                }
                            }
                        });
                    }
                });
    }

    public void onLoginSuccess() {
        showSnackbar(getString(R.string.log_in_success));
        Preferences.saveIsFirstLogin(this, false);
        if ((getIntent().getBooleanExtra(getString(R.string.open_activity_is_tutorial), true) && getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, false)) | !getModel().isWatchConnected()) {
            startActivity(TutorialPage1Activity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }

    public void showSnackbar(String msg) {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
        snackbar = Snackbar.make(relativeLayout, "", Snackbar.LENGTH_SHORT);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setText(msg);
        Snackbar.SnackbarLayout ve = (Snackbar.SnackbarLayout) snackbar.getView();
        ve.setBackgroundColor(getResources().getColor(R.color.snackbar_bg_color));
        snackbar.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
