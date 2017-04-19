package com.medcorp.lunar.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.medcorp.lunar.activity.ForgetPasswordActivity;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.tutorial.TutorialPage1Activity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedNetworkOperation;
import com.medcorp.lunar.event.LoginEvent;
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

import static com.medcorp.lunar.R.style.AppTheme_Dark_Dialog;


public class LoginActivity extends BaseActivity {

    private int errorSum = 0;
    private static final int REQUEST_SIGN_UP = 0;
    private ProgressDialog progressDialog;
    private String email;
    private Snackbar snackbar;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    @Bind(R.id.login_activity_layout)
    CoordinatorLayout loginLayout;
    @Bind(R.id.facebook_login_button)
    ImageButton facebookLoginBt;

    private IWXAPI weChatApi;
    private String APP_ID;
    private CallbackManager mCallbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        if (getModel().getUser().getNevoUserEmail() != null) {
            _emailText.setText(getModel().getUser().getNevoUserEmail());
        }
        progressDialog = new ProgressDialog(LoginActivity.this, AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.log_in_popup_message));
        registerFacebookCallBack();
    }


    @OnClick(R.id.link_signup)
    public void signUpAction() {
        startActivity(SignupActivity.class);
        finish();
    }

    @OnClick(R.id.login_skip_bt)
    public void skipLogin() {
        Preferences.saveIsFirstLogin(this, false);
        if (getIntent().getBooleanExtra(getString(R.string.open_activity_is_tutorial), true) && !getModel().isWatchConnected()) {
            startActivity(TutorialPage1Activity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }

    @OnClick(R.id.btn_login)
    public void loginAction() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        progressDialog.show();
        email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        getModel().getCloudSyncManager().userLogin(email, password);
    }

    @Subscribe
    public void onEvent(final LoginEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                switch (event.getLoginStatus()) {
                    case FAILED:
                        onLoginFailed();
                        break;
                    case SUCCESS:
                        onLoginSuccess();
                        break;
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void onLoginSuccess() {
        showSnackbar(R.string.log_in_success);
        _loginButton.setEnabled(true);
        getModel().getUser().setNevoUserEmail(_emailText.getText().toString());
        getModel().saveUser(getModel().getUser());
        setResult(RESULT_OK, null);
        Preferences.saveIsFirstLogin(this, false);
        if ((getIntent().getBooleanExtra(getString(R.string.open_activity_is_tutorial), true) &&
                getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, false)) | !getModel().isWatchConnected()) {
            startActivity(TutorialPage1Activity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }

    public void onLoginFailed() {
        errorSum++;
        if (errorSum % 3 == 0) {
            new MaterialDialog.Builder(this).backgroundColor(getResources().getColor(R.color.window_background_color))
                    .contentColor(getResources().getColor(R.color.text_color)).titleColor(getResources().getColor(R.color.text_color))
                    .title(getString(R.string.open_forget_password_dialog_title))
                    .content(getString(R.string.prompt_is_not_forget_password)).negativeText(R.string.tutorial_failed_try_again)
                    .positiveText(android.R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    intent.putExtra(getString(R.string.user_email_account), email);
                    startActivity(intent);
                    finish();
                }
            }).show();
        }
        showSnackbar(R.string.log_in_failed);
        _loginButton.setEnabled(true);
    }

    public void showSnackbar(int id) {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
        snackbar = Snackbar.make(loginLayout, "", Snackbar.LENGTH_SHORT);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setText(getString(id));
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

    public void showSnackbar(String msg) {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
        snackbar = Snackbar.make(loginLayout, "", Snackbar.LENGTH_SHORT);
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

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.register_email_error));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            _passwordText.setError(getString(R.string.register_password_error));
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    /**
     * Facebook login start
     */

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

    @OnClick(R.id.facebook_login_button)
    public void facebookLoginButtonClick() {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList(getString(R.string.facebook_public_profile)
                        , getString(R.string.facebook_user_email), getString(R.string.facebook_user_birthday)));
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
                            showSnackbar(R.string.obtain_facebook_info_failed);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString(getString(R.string.facebook_user_file), getString(R.string.facebook_user_files));
        request.setParameters(parameters);
        request.executeAsync();
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
                , userInfoResponse.getEmail(), currentProfile.getId(),birthday[2] + "-" +
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
                                    FacebookLoginResponse.UserBean user = response.getUser();
                                    final User lunarUser = getModel().getUser();
                                    lunarUser.setFirstName(user.getFirst_name());
                                    lunarUser.setNevoUserID("" + user.getId());
                                    lunarUser.setNevoUserEmail(user.getEmail());
                                    lunarUser.setIsLogin(true);
                                    lunarUser.setCreatedDate(new Date().getTime());
                                    //save it and sync with watch and cloud server
                                    getModel().saveUser(lunarUser);
                                    getModel().getSyncController().getDailyTrackerInfo(true);
                                    getModel().getNeedSyncSteps(lunarUser.getNevoUserID())
                                            .subscribe(new Consumer<List<Steps>>() {
                                                @Override
                                                public void accept(final List<Steps> stepses) throws Exception {
                                                    getModel().getNeedSyncSleep(lunarUser.getNevoUserID())
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
                                } else {
                                    showSnackbar(getString(R.string.log_in_failed));
                                }
                            }
                        });
                    }
                });
    }
    /**
     * facebook end
     */

    /**
     * wechat login start
     */
    @OnClick(R.id.wechat_login_button)
    public void weChatLogin() {
        regToWx();
        if (!weChatApi.isWXAppInstalled()) {
            showSnackbar(R.string.wechat_uninstall);
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
            public void onSuccess(WeChatLoginResponse response) {
                if (response.getStatus() == 1) {
                    WeChatLoginResponse.UserBean user = response.getUser();
                    final User lunarUser = getModel().getUser();
                    lunarUser.setFirstName(user.getFirst_name());
                    lunarUser.setNevoUserID("" + user.getId());
                    lunarUser.setWechat(user.getWechat());
                    lunarUser.setIsLogin(true);
                    lunarUser.setCreatedDate(new Date().getTime());
                    //save it and sync with watch and cloud server
                    getModel().saveUser(lunarUser);
                    getModel().getSyncController().getDailyTrackerInfo(true);
                    getModel().getNeedSyncSteps(lunarUser.getNevoUserID()).subscribe(new Consumer<List<Steps>>() {
                        @Override
                        public void accept(final List<Steps> stepses) throws Exception {
                            getModel().getNeedSyncSleep(lunarUser.getNevoUserID()).subscribe(new Consumer<List<Sleep>>() {
                                @Override
                                public void accept(List<Sleep> sleeps) throws Exception {
                                    getModel().getCloudSyncManager().launchSyncAll(lunarUser, stepses, sleeps);
                                }
                            });
                        }
                    });
                    onLoginSuccess();
                } else {
                    onFailed();
                    showSnackbar(getString(R.string.wechat_login_fail));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
