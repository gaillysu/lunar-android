package com.medcorp.lunar.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.UserInfoActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.event.SignUpEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @Bind(R.id.register_title)
    RelativeLayout titleRegister;

    private EditText editTextFirstName;
    private EditText editLastName;


    private String firstName;
    private String lastName;
//    private CheckBox checkIsAgreeBt;
    private String email;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
//        checkIsAgreeBt = (CheckBox) findViewById(R.id.sign_up_check_user_is_agree_terms_radio_bt);
        editTextFirstName = (EditText) findViewById(R.id.register_account_activity_edit_first_name);
        editLastName = (EditText) findViewById(R.id.register_account_activity_edit_last_name);
//        checkIsAgreeBt.setChecked(false);
    }

    @OnClick(R.id.register_title_back_image_button)
    public void backClick() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.putExtra("isTutorialPage", true);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            backClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.link_login)
    public void loginLink() {
        finish();
    }

    @OnClick(R.id.btn_signup)
    public void signUpAction() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onEvent(SignUpEvent event) {
        switch (event.getSignUpStatus()) {
            case FAILED:
                onSignupFailed();
                break;
            case SUCCESS:
                Toast.makeText(getBaseContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                _signupButton.setEnabled(true);
                getModel().getNevoUser().setNevoUserEmail(_emailText.getText().toString());
                getModel().saveNevoUser(getModel().getNevoUser());
                setResult(RESULT_OK, null);
                finish();
                break;
        }
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
//        if (!checkIsAgreeBt.isChecked()) {
//            valid = false;
//        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.register_email_error));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length()<8) {
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
}