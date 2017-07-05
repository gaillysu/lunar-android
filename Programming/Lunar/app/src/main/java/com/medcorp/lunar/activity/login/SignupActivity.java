package com.medcorp.lunar.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.UserInfoActivity;
import com.medcorp.lunar.activity.tutorial.WelcomeActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedNetworkOperation;
import com.medcorp.lunar.event.SignUpEvent;
import com.medcorp.lunar.network.listener.RequestResponseListener;
import com.medcorp.lunar.network.model.request.CheckEmailRequest;
import com.medcorp.lunar.network.model.response.CheckEmailResponse;
import com.medcorp.lunar.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.medcorp.lunar.R.id.use_wechat_account_register_ib;
import static com.medcorp.lunar.R.style.AppTheme_Dark_Dialog;

public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_email_ed)
    EditText _emailText;
    @Bind(R.id.register_account_activity_edit_password_ed)
    EditText _passwordText;
    @Bind(R.id.register_account_activity_edit_password_confirm_ed)
    EditText _passwordConfirmText;
    @Bind(R.id.register_bt)
    Button _signupButton;
    @Bind(R.id.register_account_activity_edit_first_name_ed)
    EditText editTextFirstName;
    @Bind(R.id.register_account_activity_edit_last_name_ed)
    EditText editLastName;
    @Bind(R.id.register_layout)
    LinearLayout registerLayout;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.network_wait_text));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(WelcomeActivity.class);
            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.register_bt)
    public void signUpAction() {
        progressDialog.show();
        if (!validate()) {
            onSignupFailed();
            return;
        }
        CheckEmailRequest request = new CheckEmailRequest(email);
        MedNetworkOperation.getInstance(this).checkEmail(this, request,new RequestResponseListener<CheckEmailResponse>() {
            @Override
            public void onFailed() {
                progressDialog.dismiss();
                ToastHelper.showShortToast(SignupActivity.this, getString(R.string.network_error));
            }

            @Override
            public void onSuccess(CheckEmailResponse response) {
                progressDialog.dismiss();
                if (response.getStatus() != 1) {
                    Intent intent = new Intent(SignupActivity.this, UserInfoActivity.class);
                    intent.putExtra(getString(R.string.user_email_account), email);
                    intent.putExtra(getString(R.string.user_register_password), password);
                    intent.putExtra(getString(R.string.user_register_first_name), firstName);
                    intent.putExtra(getString(R.string.user_register_last_name), lastName);
                    startActivity(intent);
                    finish();
                } else {
                    ToastHelper.showShortToast(SignupActivity.this, getString(R.string.check_email_message));
                }
            }
        });
    }

    @Subscribe
    public void onEvent(final SignUpEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switch (event.getSignUpStatus()) {
                    case FAILED:
                        onSignupFailed();
                        break;
                    case SUCCESS:
                        Toast.makeText(getBaseContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                        _signupButton.setEnabled(true);
                        getModel().getUser().setUserEmail(_emailText.getText().toString());
                        getModel().saveUser(getModel().getUser());
                        setResult(RESULT_OK, null);
                        finish();
                        break;
                }
            }
        });

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.register_failed, Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        String passwordConfirm = _passwordConfirmText.getText().toString();

        firstName = editTextFirstName.getText().toString();
        lastName = editLastName.getText().toString();
        if (firstName.isEmpty()) {
            valid = false;
            editTextFirstName.setError(getString(R.string.register_input_first_is_empty));
        } else {
            editTextFirstName.setError(null);
        }
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

        if (!passwordConfirm.equals(password)) {
            _passwordText.setError(getString(R.string.register_password_confirm_error));
            valid = false;
        }
        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.cancel_register_bt)
    public void cancelClick() {
        startActivity(WelcomeActivity.class);
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
    }


    /**
     * create WeChat account
     */
    @OnClick(use_wechat_account_register_ib)
    public void createWeChatAccount() {
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
                } else {
                    onFailed();
                    showSnackbar(getString(R.string.wechat_login_fail));
                }
            }
        });
    }


    public void onLoginSuccess() {
        showSnackbar(R.string.log_in_success);
        getModel().getUser().setUserEmail(_emailText.getText().toString());
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

    @OnClick(R.id.use_facebook_account_register_ib)
    public void createFacebookAccount() {
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

    public void showSnackbar(int id) {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
        snackbar = Snackbar.make(registerLayout, "", Snackbar.LENGTH_SHORT);
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
        snackbar = Snackbar.make(registerLayout, "", Snackbar.LENGTH_SHORT);
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

}
