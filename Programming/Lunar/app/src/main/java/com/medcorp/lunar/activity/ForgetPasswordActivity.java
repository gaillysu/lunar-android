package com.medcorp.lunar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.network.listener.ResponseListener;
import com.medcorp.lunar.network.validic.model.CheckUserInputEmailIsTrueRequest;
import com.medcorp.lunar.network.validic.model.RequestTokenResponse;
import com.medcorp.lunar.util.EmailUtils;
import com.medcorp.lunar.view.ToastHelper;
import com.octo.android.robospice.persistence.exception.SpiceException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/5.
 *
 */
public class ForgetPasswordActivity extends BaseActivity {

    @Bind(R.id.forget_password_input_email_edit)
    EditText editEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_passwor_activity_layout);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        editEmail.setText(intent.getStringExtra("email"));
    }

    @OnClick(R.id.back_page_image_button)
    public void backPageClick() {
        finish();
    }

    @OnClick(R.id.forget_password_send_bt)
    public void forgetPasswordClick(){
        final String email = editEmail.getText().toString();
        if(!TextUtils.isEmpty(email)){
            if(EmailUtils.checkEmail(email)){
                final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.network_wait_text));
                progressDialog.show();

                getModel().getNetworkManage().execute(new CheckUserInputEmailIsTrueRequest(getModel()
                        .getNetworkManage().getAccessToken(), email), new ResponseListener<RequestTokenResponse>(){
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        ToastHelper.showShortToast(ForgetPasswordActivity.this,getString(R.string.user_email_is_error));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onRequestSuccess(RequestTokenResponse requestTokenResponse) {
                        progressDialog.dismiss();
                        if(requestTokenResponse.getStatus() == 1) {
                        }else{
                            ToastHelper.showShortToast(ForgetPasswordActivity.this,requestTokenResponse.getMessage());
                        }
                            Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordResultActivity.class);
                            intent.putExtra("token", requestTokenResponse.getUser().getPassword_token());
                            intent.putExtra("email", email);
                            intent.putExtra("id", requestTokenResponse.getUser().getId());
                            startActivity(intent);
                            finish();
                    }
                });


            }else {
             editEmail.setError(getString(R.string.email_format_error));
            }
        }else{
            editEmail.setError(getString(R.string.tips_user_account_password));
        }

    }
}
