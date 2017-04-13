package com.medcorp.lunar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedOperation;
import com.medcorp.lunar.network.listener.RequestResponseListener;
import com.medcorp.lunar.network.model.request.ChangePasswordRequest;
import com.medcorp.lunar.network.model.response.ChangePasswordResponse;
import com.medcorp.lunar.view.ToastHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ForgetPasswordResultActivity extends BaseActivity {

    @Bind(R.id.input_new_password_ed)
    EditText inputPasswordOne;
    @Bind(R.id.input_new_password_ed_two)
    EditText inputPasswordTwo;
    private String passwordToken;
    private int id;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_result_page_layout);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        email = intent.getStringExtra(getString(R.string.user_email_account));
        passwordToken = intent.getStringExtra(getString(R.string.forget_password_token));
        id = intent.getIntExtra(getString(R.string.user_id), -1);
    }

    @OnClick(R.id.send_new_password)
    public void startChangePassword() {
        String newPassword = inputPasswordOne.getText().toString();
        String twoPassWord = inputPasswordTwo.getText().toString();
        if (TextUtils.isEmpty(newPassword)) {
            inputPasswordOne.setError(getString(R.string.password_is_not_empty));
            return;
        } else if (TextUtils.isEmpty(twoPassWord)) {
            inputPasswordTwo.setError(getString(R.string.password_is_not_empty));
            return;
        }

        if (!newPassword.equals(twoPassWord)) {
            ToastHelper.showShortToast(this, getString(R.string.password_is_not_repeat));
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.network_wait_text));
        progressDialog.show();

        ChangePasswordRequest request = new ChangePasswordRequest(passwordToken, email, id + "", newPassword);
        MedOperation.getInstance(this).changePassword(this,request, new RequestResponseListener<ChangePasswordResponse>() {
            @Override
            public void onFailed() {
                progressDialog.dismiss();
                ToastHelper.showShortToast(ForgetPasswordResultActivity.this,
                        getString(R.string.password_change_failure));
            }

            @Override
            public void onSuccess(ChangePasswordResponse response) {
                progressDialog.dismiss();
                if (response.getStatus() == 1 && response.getMessage().equals("OK")) {
                    ToastHelper.showShortToast(ForgetPasswordResultActivity.this,
                            getString(R.string.password_change_success));
                    Intent intent = new Intent(ForgetPasswordResultActivity.this, LoginActivity.class);
                    intent.putExtra(getString(R.string.open_activity_is_tutorial), false);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @OnClick(R.id.back_page_image_button)
    public void closePageClick() {
        finish();
    }
}
