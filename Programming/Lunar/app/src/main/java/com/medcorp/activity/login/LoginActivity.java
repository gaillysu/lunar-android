package com.medcorp.activity.login;

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
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.R;
import com.medcorp.activity.ForgetPasswordActivity;
import com.medcorp.activity.MainActivity;
import com.medcorp.activity.tutorial.TutorialPage1Activity;
import com.medcorp.base.BaseActivity;
import com.medcorp.event.LoginEvent;
import com.medcorp.network.med.model.LoginUser;
import com.medcorp.util.Preferences;

import net.medcorp.library.ble.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medcorp.R.style.AppTheme_Dark_Dialog;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        if (getModel().getNevoUser().getNevoUserEmail() != null) {
            _emailText.setText(getModel().getNevoUser().getNevoUserEmail());
        }
    }

    @OnClick(R.id.link_signup)
    public void signUpAction() {
        startActivity(SignupActivity.class);
        finish();
    }

    @OnClick(R.id.login_skip_bt)
    public void skipLogin() {
        Preferences.saveIsFirstLogin(this,false);
        if (getIntent().getBooleanExtra("isTutorialPage", true) && !getModel().isWatchConnected()) {
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
        progressDialog = new ProgressDialog(LoginActivity.this, AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.log_in_popup_message));
        progressDialog.show();

        email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        LoginUser user = new LoginUser();
        user.setEmail(email);
        user.setPassword(password);
        getModel().getCloudSyncManager().userLogin(user);
    }

    @Subscribe
    public void onEvent(LoginEvent event) {
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

    public void showSnackbar(int id){
        if(snackbar != null){
            if(snackbar.isShown()){
                snackbar.dismiss();
            }
        }
        snackbar = Snackbar.make(loginLayout,"",Snackbar.LENGTH_SHORT);
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
}
