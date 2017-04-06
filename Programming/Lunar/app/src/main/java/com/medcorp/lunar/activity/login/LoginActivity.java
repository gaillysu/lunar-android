package com.medcorp.lunar.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.ForgetPasswordActivity;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.tutorial.TutorialPage1Activity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.CheckWeChatEvent;
import com.medcorp.lunar.event.CreateWeChatEvent;
import com.medcorp.lunar.event.LoginEvent;
import com.medcorp.lunar.event.ReturnUserInfoEvent;
import com.medcorp.lunar.event.WeChatEvent;
import com.medcorp.lunar.event.WeChatLoginEvent;
import com.medcorp.lunar.event.WeChatTokenEvent;
import com.medcorp.lunar.network.med.model.LoginUser;
import com.medcorp.lunar.network.med.model.WeChatUserInfoResponse;
import com.medcorp.lunar.util.Preferences;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

import net.medcorp.library.ble.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medcorp.lunar.R.style.AppTheme_Dark_Dialog;


public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
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

    private IWXAPI weChatApi;
    private String APP_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        Log.i("jason", "eventBus register");
        ButterKnife.bind(this);
        if (getModel().getNevoUser().getNevoUserEmail() != null) {
            _emailText.setText(getModel().getNevoUser().getNevoUserEmail());
        }
        progressDialog = new ProgressDialog(LoginActivity.this, AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.log_in_popup_message));
    }

    @OnClick(R.id.link_signup)
    public void signUpAction() {
        startActivity(SignupActivity.class);
        finish();
    }

    @OnClick(R.id.login_skip_bt)
    public void skipLogin() {
        Preferences.saveIsFirstLogin(this, false);
        if (getIntent().getBooleanExtra("isTutorialPage", true) && !getModel().isWatchConnected()) {
            startActivity(TutorialPage1Activity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }

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
        Log.e("jason","send weChat message");
    }

    private void regToWx() {
        APP_ID = getString(R.string.we_chat_app_id);
        weChatApi = getModel().getWXApi();
        weChatApi.registerApp(APP_ID);
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

        LoginUser user = new LoginUser();
        user.setEmail(email);
        user.setPassword(password);
        getModel().getCloudSyncManager().userLogin(user);
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
        getModel().getNevoUser().setNevoUserEmail(_emailText.getText().toString());
        getModel().saveNevoUser(getModel().getNevoUser());
        setResult(RESULT_OK, null);
        Preferences.saveIsFirstLogin(this, false);
        getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG, false).commit();
        if (getModel().isWatchConnected()) {
        }
        if (getIntent().getBooleanExtra("isTutorialPage", true) &&
                getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true)) {
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
                    intent.putExtra("email", email);
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

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError(getString(R.string.register_password_error));
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    @Subscribe
    public void weChatErrorEvent(WeChatEvent event) {
        showSnackbar(getString(R.string.network_error));
    }

    @Subscribe
    public void weChatEvent(WeChatTokenEvent event) {
        if (event != null) {
            progressDialog.show();
            getModel().getWeChatToken(event.getCode());
        }
    }

    @Subscribe
    public void userInfoEvent(ReturnUserInfoEvent event) {
        if (event != null) {
            WeChatUserInfoResponse userInfo = event.getUserInfo();
            getModel().getCloudSyncManager().checkWeChatAccount(userInfo);
        } else {
            progressDialog.dismiss();
            showSnackbar(getString(R.string.wechat_login_fail));
        }
    }

    @Subscribe
    public void checkWeChatEvent(CheckWeChatEvent event) {
        if (event.getStatus() == -1) {
            showSnackbar(event.getErrorMsg());
            progressDialog.dismiss();
        }
    }

    @Subscribe
    public void createWeChatEvent(CreateWeChatEvent event) {
        if (event.getStatus() == -1 | event.getStatus() == 0) {
            showSnackbar(event.getStatus());
            progressDialog.dismiss();
        }
    }

    @Subscribe
    public void weChatLoginEvent(WeChatLoginEvent event) {
        progressDialog.dismiss();
        if (event.getStatus() == -1 | event.getStatus() == 0) {
            showSnackbar(event.getErrorMsg());
        } else {
            onLoginSuccess();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("jason", "eventBus unregister");
        EventBus.getDefault().unregister(this);
    }
}
