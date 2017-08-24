package com.medcorp.lunar.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.ForgetPasswordActivity;
import com.medcorp.lunar.activity.MainActivity;
import com.medcorp.lunar.activity.tutorial.TutorialPage1Activity;
import com.medcorp.lunar.activity.tutorial.WelcomeActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedNetworkOperation;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.model.response.UserLoginResponse;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.view.ToastHelper;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.medcorp.library.ble.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;


public class LoginActivity extends BaseActivity {

    private int errorSum = 0;
    private ProgressDialog progressDialog;
    private String email;
    private Snackbar snackbar;

    @Bind(R.id.input_email_ed)
    EditText _emailText;
    @Bind(R.id.input_password_ed)
    EditText _passwordText;
    @Bind(R.id.login_activity_login_bt)
    AppCompatButton _loginButton;
    @Bind(R.id.login_activity_layout)
    CoordinatorLayout loginLayout;
    private User LoginUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                LoginUser = user;
                if (user.getUserEmail() != null) {
                    _emailText.setText(user.getUserEmail());
                }
            }
        });
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.log_in_popup_message));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startAndFinishActivity(WelcomeActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.login_activity_login_bt)
    public void loginAction() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        progressDialog.show();
        email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        MedNetworkOperation.getInstance(this).userMedLogin(email, password, new RequestListener<UserLoginResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                progressDialog.dismiss();
                onLoginFailed();
            }

            @Override
            public void onRequestSuccess(UserLoginResponse userLoginResponse) {
                progressDialog.dismiss();
                if (userLoginResponse.getStatus() == 1) {
                    UserLoginResponse.UserBean user = userLoginResponse.getUser();
                    try {
                        LoginUser.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(user.getBirthday().getDate()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    LoginUser.setFirstName(user.getFirst_name());
                    LoginUser.setHeight(user.getLength());
                    LoginUser.setLastName(user.getLast_name());
                    LoginUser.setWeight(user.getWeight());
                    LoginUser.setUserID("" + user.getId());
                    LoginUser.setUserEmail(user.getEmail());
                    LoginUser.setIsLogin(true);
                    LoginUser.setCreatedDate(new Date().getTime());
                    //save it and sync with watch and cloud server
                    getModel().saveUser(LoginUser);
                    getModel().getSyncController().getDailyTrackerInfo(true);
                    getModel().getNeedSyncSteps(LoginUser.getUserID()).subscribe(new Consumer<List<Steps>>() {
                        @Override
                        public void accept(final List<Steps> stepses) throws Exception {
                            getModel().getNeedSyncSleep(LoginUser.getUserID()).subscribe(new Consumer<List<Sleep>>() {
                                @Override
                                public void accept(List<Sleep> sleeps) throws Exception {
                                    getModel().getCloudSyncManager().launchSyncAll(LoginUser, stepses, sleeps);
                                }
                            });
                        }
                    });
                    onLoginSuccess();
                } else {
                    onLoginFailed();
                    ToastHelper.showShortToast(LoginActivity.this, userLoginResponse.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void onLoginSuccess() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showSnackbar(R.string.log_in_success);
                _loginButton.setEnabled(true);
                setResult(RESULT_OK, null);
                Preferences.saveIsFirstLogin(LoginActivity.this, false);
                if (getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, false)) {
                    startAndFinishActivity(TutorialPage1Activity.class);
                } else {
                    startAndFinishActivity(MainActivity.class);
                }
            }
        });
    }

    @OnClick(R.id.forget_password_send_bt)
    public void forgetPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        intent.putExtra(getString(R.string.user_email_account), email);
        startAndFinishActivity(intent);
    }

    public void onLoginFailed() {
        errorSum++;
        if (errorSum % 3 == 0) {
            new MaterialDialog.Builder(this).backgroundColor(getResources().getColor(R.color.window_background_color))
                    .contentColor(getResources().getColor(R.color.text_color)).titleColor(getResources().getColor(R.color.text_color))
                    .title(getString(R.string.open_forget_password_dialog_title))
                    .content(getString(R.string.prompt_is_not_forget_password))
                    .negativeText(R.string.tutorial_failed_try_again)
                    .positiveText(android.R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    intent.putExtra(getString(R.string.user_email_account), email);
                    startAndFinishActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
