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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medcorp.lunar.R.style.AppTheme_Dark_Dialog;

public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_password_confirm)
    EditText _passwordConfirmText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.register_account_activity_edit_first_name)
    EditText editTextFirstName;
    @Bind(R.id.register_account_activity_edit_last_name)
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

        progressDialog = new ProgressDialog(this, AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.load_in_popup_message));
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

    @OnClick(R.id.btn_signup)
    public void signUpAction() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.network_wait_text));
        CheckEmailRequest request = new CheckEmailRequest(email);
        MedNetworkOperation.getInstance(this).checkEmail(this, request, new RequestResponseListener<CheckEmailResponse>() {
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
}