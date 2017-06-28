package com.medcorp.lunar.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.widget.Button;
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
import com.medcorp.lunar.event.LoginEvent;
import com.medcorp.lunar.util.Preferences;

import net.medcorp.library.ble.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {

    private int errorSum = 0;
    private ProgressDialog progressDialog;
    private String email;
    private Snackbar snackbar;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.login_activity_layout)
    CoordinatorLayout loginLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        if (getModel().getUser().getUserEmail() != null) {
            _emailText.setText(getModel().getUser().getUserEmail());
        }
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.log_in_popup_message));
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
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void onLoginSuccess() {
        showSnackbar(R.string.log_in_success);
        _loginButton.setEnabled(true);
        getModel().getUser().setUserEmail(_emailText.getText().toString());
        getModel().saveUser(getModel().getUser());
        setResult(RESULT_OK, null);
        Preferences.saveIsFirstLogin(this, false);
        if ((getIntent().getBooleanExtra(getString(R.string.open_activity_is_tutorial), true) &&
                getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, false))
                | !getModel().isWatchConnected()) {
            startActivity(TutorialPage1Activity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }

    @OnClick(R.id.forget_password_send_bt)
    public void forgetPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        intent.putExtra(getString(R.string.user_email_account), email);
        startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
