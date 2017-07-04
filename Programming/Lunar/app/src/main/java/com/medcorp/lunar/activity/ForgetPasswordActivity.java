package com.medcorp.lunar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedNetworkOperation;
import com.medcorp.lunar.network.listener.RequestResponseListener;
import com.medcorp.lunar.network.model.request.CheckEmailRequest;
import com.medcorp.lunar.network.model.request.RequestForgotPasswordTokenRequest;
import com.medcorp.lunar.network.model.response.CheckEmailResponse;
import com.medcorp.lunar.network.model.response.RequestForgotPasswordResponse;
import com.medcorp.lunar.util.EmailUtils;
import com.medcorp.lunar.view.ToastHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ForgetPasswordActivity extends BaseActivity {

    @Bind(R.id.forget_password_input_email_edit)
    EditText editEmail;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_passwor_activity_layout);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        editEmail.setText(intent.getStringExtra(getString(R.string.user_email_account)));
    }

    @OnClick(R.id.forget_password_send_bt)
    public void forgetPasswordClick() {
        final String email = editEmail.getText().toString();
        if (!TextUtils.isEmpty(email)) {
            if (EmailUtils.checkEmail(email)) {
                progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.network_wait_text));
                progressDialog.show();
                final CheckEmailRequest request = new CheckEmailRequest(email);
                MedNetworkOperation.getInstance(this).checkEmail(this, request, new RequestResponseListener<CheckEmailResponse>() {
                    @Override
                    public void onFailed() {
                        ToastHelper.showShortToast(ForgetPasswordActivity.this, getString(R.string.network_error));
                    }

                    @Override
                    public void onSuccess(CheckEmailResponse response) {
                        if (response.getStatus() == 1) {
                            obtainPasswordToken(response.getUser().getEmail());
                        } else {
                            ToastHelper.showShortToast(ForgetPasswordActivity.this, response.getMessage());
                        }
                    }
                });
            } else {
                editEmail.setError(getString(R.string.email_format_error));
            }
        } else {
            editEmail.setError(getString(R.string.tips_user_account_password));
        }
    }

    public void obtainPasswordToken(final String email) {
        RequestForgotPasswordTokenRequest request = new RequestForgotPasswordTokenRequest(email);
        MedNetworkOperation.getInstance(this).obtainPasswordToken(this, request, new RequestResponseListener<RequestForgotPasswordResponse>() {
            @Override
            public void onFailed() {
                ToastHelper.showShortToast(ForgetPasswordActivity.this, getString(R.string.user_email_is_error));
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(RequestForgotPasswordResponse requestTokenResponse) {
                progressDialog.dismiss();
                if (requestTokenResponse.getStatus() == 1) {
                    Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordResultActivity.class);
                    intent.putExtra(getString(R.string.forget_password_token), requestTokenResponse.getUser().getPassword_token());
                    intent.putExtra(getString(R.string.user_email_account), email);
                    intent.putExtra(getString(R.string.user_id), requestTokenResponse.getUser().getId());
                    startActivity(intent);
                    finish();
                } else {
                    ToastHelper.showShortToast(ForgetPasswordActivity.this, requestTokenResponse.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(LoginActivity.class);
            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
