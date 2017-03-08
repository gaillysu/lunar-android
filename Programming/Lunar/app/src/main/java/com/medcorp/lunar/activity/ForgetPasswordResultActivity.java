package com.medcorp.lunar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.medcorp.lunar.R;
import com.medcorp.lunar.activity.login.LoginActivity;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.network.listener.ResponseListener;
import com.medcorp.lunar.network.med.model.LoginUserModel;
import com.medcorp.lunar.network.validic.model.ForgetPasswordModel;
import com.medcorp.lunar.network.validic.model.ForgetPasswordRequest;
import com.medcorp.lunar.view.ToastHelper;
import com.octo.android.robospice.persistence.exception.SpiceException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/5.
 *
 */
public class ForgetPasswordResultActivity extends BaseActivity {

//    @Bind(R.id.forget_password_result_show_email_tx)
//    TextView showUserEmailAccountText;

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
        email = intent.getStringExtra("email");
//        showUserEmailAccountText.setText(email);
        passwordToken = intent.getStringExtra("token");
        id = intent.getIntExtra("id", -1);
        //        new Handler().postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                Intent intent = new Intent(ForgetPasswordResultActivity.this, LoginActivity.class);
        //                intent.putExtra("isTutorialPage", true);
        //                startActivity(intent);
        //                finish();
        //            }
        //        }, 1500);
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

        ForgetPasswordModel requestModel = new ForgetPasswordModel(id, email, passwordToken, newPassword);
        getModel().getNetworkManage().execute(new ForgetPasswordRequest(getModel().getNetworkManage()
                .getAccessToken(), requestModel), new ResponseListener<LoginUserModel>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                progressDialog.dismiss();
               ToastHelper.showShortToast(ForgetPasswordResultActivity.this,
                        getString(R.string.password_change_failure));
            }

            @Override
            public void onRequestSuccess(LoginUserModel loginUserModel) {
                progressDialog.dismiss();
                if (loginUserModel.getStatus() == 1 && loginUserModel.getMessage().equals("OK")) {
                   ToastHelper.showShortToast(ForgetPasswordResultActivity.this,
                            getString(R.string.password_change_success));
                    Intent intent = new Intent(ForgetPasswordResultActivity.this,LoginActivity.class);
                    intent.putExtra("isTutorialPage", false );
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
