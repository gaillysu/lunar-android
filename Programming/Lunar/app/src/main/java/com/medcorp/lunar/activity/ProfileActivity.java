package com.medcorp.lunar.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedNetworkOperation;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.listener.RequestResponseListener;
import com.medcorp.lunar.network.model.request.DeleteUserAccountRequest;
import com.medcorp.lunar.network.model.request.UpdateAccountInformationRequest;
import com.medcorp.lunar.network.model.response.DeleteUserAccountResponse;
import com.medcorp.lunar.network.model.response.UpdateAccountInformationResponse;
import com.medcorp.lunar.util.Preferences;
import com.medcorp.lunar.util.PublicUtils;
import com.medcorp.lunar.view.ToastHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;

import static com.medcorp.lunar.R.style.AppTheme_Dark_Dialog;

/***
 * Created by med on 16/4/6.
 */
public class ProfileActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.profile_activity_select_picture)
    ImageView mImageButton;
    @Bind(R.id.profile_fragment_user_first_name_tv)
    EditText firstName;
    @Bind(R.id.profile_fragment_user_last_name_tv)
    EditText lastName;
    @Bind(R.id.profile_fragment_user_birthday_tv)
    TextView userBirthday;
    @Bind(R.id.profile_fragment_user_height_tv)
    TextView userHeight;
    @Bind(R.id.profile_fragment_user_weight_tv)
    TextView userWeight;
    @Bind(R.id.profile_logout_bt)
    AppCompatButton logoutButton;
    @Bind(R.id.profile_delete_bt)
    AppCompatButton deleteProfile;
    @Bind(R.id.profile_fragment_user_user_email_tv)
    EditText userEmailTv;
    @Bind(R.id.profile_fragment_user_gender_tv)
    TextView userGender;
    private User lunarUser;
    private String userEmail;
    private static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private ArrayList<String> mSelectPath;
    private ProgressDialog progressDialog;
    private Calendar calendar = Calendar.getInstance();
    private int height = 0;
    private int weight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(R.string.profile_title);
        getModel().getUser().subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                lunarUser = user;
                initView();
                if (user.getUserEmail() != null) {
                    userEmail = user.getUserEmail();
                } else {
                    userEmail = getString(R.string.watch_med_profile);
                }
                Bitmap bt = BitmapFactory.decodeFile(Preferences.getUserHeardPicturePath(ProfileActivity.this, userEmail));
                if (bt != null) {
                    mImageButton.setImageBitmap(PublicUtils.drawCircleView(bt));
                } else {
                    mImageButton.setImageResource(R.drawable.user);
                }
            }
        });
    }


    private void initView() {
        progressDialog = new ProgressDialog(this, AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.network_wait_text));
        if (lunarUser.isLogin()) {
            deleteProfile.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            deleteProfile.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }

        if (lunarUser != null) {
            if (!TextUtils.isEmpty(lunarUser.getFirstName())) {
                firstName.setText(lunarUser.getFirstName());
            }
            if (!TextUtils.isEmpty(lunarUser.getLastName())) {
                lastName.setText(lunarUser.getLastName());
            }
            if (!TextUtils.isEmpty(lunarUser.getUserEmail())) {
                userEmailTv.setText(userEmail);
            }
            //please strictly refer to our UI design Docs, the date format is dd,MMM,yyyy
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            userBirthday.setText(dateFormat.format(new Date(lunarUser.getBirthday())));
            userHeight.setText(lunarUser.getHeight() + " cm");
            userWeight.setText(lunarUser.getWeight() + " kg");
            userGender.setText(getResources().getStringArray(R.array.profile_gender)[lunarUser.getSex()]);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.edit_user_gender_pop)
    public void editGender() {
        new MaterialDialog.Builder(this)
                .title(R.string.setting_user_gender_dialog_title)
                .items(R.array.profile_gender)
                .itemsCallbackSingleChoice(lunarUser.getSex(), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        lunarUser.setSex(which);
                        userGender.setText(getResources().getStringArray(R.array.profile_gender)[which]);
                        return true;
                    }
                }).positiveText(R.string.mis_permission_dialog_ok)
                .negativeText(R.string.mis_permission_dialog_cancel)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .show();
    }

    @OnClick(R.id.edit_user_weight_pop)
    public void editUserWeight() {

        View view = LayoutInflater.from(this).inflate(R.layout.number_picker_layout, null);
        TextView unitText = (TextView) view.findViewById(R.id.dialog_unit);
        unitText.setText("kg");
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker_group);
        numberPicker.setMinValue(30);
        numberPicker.setMaxValue(200);
        numberPicker.setValue(lunarUser.getWeight());
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                userWeight.setText(newVal + " kg");
                weight = newVal;
            }
        });
        new MaterialDialog.Builder(this)
                .title(R.string.setting_user_weight_dialog_title)
                .customView(view, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        lunarUser.setWeight(weight);
                    }
                }).positiveText(R.string.mis_permission_dialog_ok)
                .negativeText(R.string.mis_permission_dialog_cancel)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .show();
    }

    @OnClick(R.id.edit_user_height_pop)
    public void editUserHeight() {
        View view = LayoutInflater.from(this).inflate(R.layout.number_picker_layout, null);
        TextView unitText = (TextView) view.findViewById(R.id.dialog_unit);
        unitText.setText("cm");
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker_group);
        numberPicker.setMinValue(120);
        numberPicker.setMaxValue(320);
        numberPicker.setValue(lunarUser.getHeight());
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                userHeight.setText(newVal + " cm");
                height = newVal;
            }
        });
        new MaterialDialog.Builder(this)
                .title(R.string.setting_user_height_dialog_title)
                .customView(view, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        lunarUser.setHeight(height);
                    }
                }).positiveText(R.string.mis_permission_dialog_ok)
                .negativeText(R.string.mis_permission_dialog_cancel)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .show();
    }

    @OnClick(R.id.edit_user_birthday_pop)
    public void editUserBirthday() {
        if (lunarUser.getBirthday() != 0) {
            calendar.setTimeInMillis(lunarUser.getBirthday());
        }
        View inflate = LayoutInflater.from(this).inflate(R.layout.date_picker_dialog_layout, null);
        DatePicker datepicker = (DatePicker) inflate.findViewById(R.id.setting_user_birthday_dp);
        datepicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.YEAR, year);
                        userBirthday.setText(dayOfMonth + " - " +
                                new SimpleDateFormat("MMM").format(calendar.getTime()) + " - " + year);
                    }
                });
        new MaterialDialog.Builder(this).title(getString(R.string.setting_user_birthday))
                .customView(inflate, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        lunarUser.setBirthday(calendar.getTimeInMillis());
                    }
                }).positiveText(R.string.mis_permission_dialog_ok)
                .negativeText(R.string.mis_permission_dialog_cancel)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startAndFinishActivity(MainActivity.class);
                break;
            case R.id.done_menu:
                String userLastName = lastName.getText().toString();
                String userFirstName = firstName.getText().toString();
                String userEmail = userEmailTv.getText().toString();
                if (userFirstName.isEmpty()) {
                    ToastHelper.showShortToast(this, R.string.register_input_first_is_empty);
                    break;
                } else {
                    lunarUser.setFirstName(userFirstName);
                }
                if (userLastName.isEmpty()) {
                    ToastHelper.showShortToast(this, R.string.register_input_first_is_empty);
                    break;
                } else {
                    lunarUser.setLastName(userLastName);
                }
                if (Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    userEmailTv.setText(userEmail);
                    lunarUser.setUserEmail(userEmail);
                } else {
                    ToastHelper.showShortToast(ProfileActivity.this, getString(R.string.register_email_error));
                }
                progressDialog.show();
                String format = new SimpleDateFormat("yyyy-MM-dd").format(lunarUser.getBirthday());
                UpdateAccountInformationRequest request = new UpdateAccountInformationRequest(
                        new Integer(lunarUser.getUserID()).intValue()
                        , lunarUser.getFirstName(), lunarUser.getUserEmail(), lunarUser.getLastName(), format
                        , lunarUser.getHeight(), lunarUser.getWeight(), lunarUser.getSex());
                MedNetworkOperation.getInstance(this).updateUserInformation(request,
                        new RequestResponseListener<UpdateAccountInformationResponse>() {
                            @Override
                            public void onFailed() {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        ToastHelper.showShortToast(ProfileActivity.this,
                                                getString(R.string.save_update_user_info_failed));
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(UpdateAccountInformationResponse response) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        getModel().saveUser(lunarUser);
                                        startActivity(MainActivity.class);
                                        finish();
                                        overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
                                    }
                                });
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.profile_activity_select_picture)
    public void settingPicture() {
        pickImage();

    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED
                ) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            MultiImageSelector selector = MultiImageSelector.create();
            selector.showCamera(true);
            selector.count(1);
            selector.single();
            selector.start(ProfileActivity.this, REQUEST_IMAGE);
        }
    }

    private void requestPermission(final String permission, final String permissionCamera, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]
                                    {permission, permissionCamera}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (mSelectPath.size() > 0) {
                    File imageFilePath = new File(mSelectPath.get(0));
                    if (imageFilePath != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath.getAbsolutePath());
                        mImageButton.setImageBitmap(PublicUtils.drawCircleView(bitmap));
                        Preferences.saveUserHeardPicture(ProfileActivity.this, userEmail, imageFilePath.getAbsolutePath());
                    }
                }
            } else if (requestCode == MultiImageSelectorFragment.ANDROID_SEVEN_REQUEST_CAMERA) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (mSelectPath.size() > 0) {
                    File imagePath = new File(mSelectPath.get(0));
                    if (imagePath != null) {
                        Bitmap bitImage = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
                        mImageButton.setImageBitmap(PublicUtils.drawCircleView(bitImage));
                        // setPicToView(BitmapFactory.decodeFile(mSelectPath.get(0)));
                        Preferences.saveUserHeardPicture(ProfileActivity.this, userEmail, imagePath.getAbsolutePath());
                    }
                }
            }
        }
    }

    @OnClick(R.id.profile_logout_bt)
    public void logout() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.google_fit_log_out))
                .content(getString(R.string.settings_sure))
                .positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        lunarUser.setIsLogin(false);
                        getModel().saveUser(lunarUser);
                        ProfileActivity.this.finish();
                    }
                })
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    @OnClick(R.id.profile_delete_bt)
    public void deleteProfile() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.profile_delete_dialog_title))
                .content(getString(R.string.profile_delete_dialog_prompt_user))
                .positiveText(R.string.goal_ok)
                .negativeText(R.string.goal_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteCurrentAccount();
                    }
                })
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void deleteCurrentAccount() {
        progressDialog.show();
        DeleteUserAccountRequest request = new DeleteUserAccountRequest(new Integer(lunarUser.getUserID()).intValue());
        MedNetworkOperation.getInstance(this).deleteCurrentAccount(request,
                new RequestResponseListener<DeleteUserAccountResponse>() {
                    @Override
                    public void onFailed() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                ToastHelper.showShortToast(ProfileActivity.this,
                                        getString(R.string.save_update_user_info_failed));
                                String userHeardPicturePath = Preferences.getUserHeardPicturePath(ProfileActivity.this, userEmail);
                                new File(userHeardPicturePath).delete();

                            }
                        });
                    }

                    @Override
                    public void onSuccess(DeleteUserAccountResponse response) {
                        progressDialog.dismiss();
                        if (response.getStatus() >= 0) {
                            getModel().getUserDatabaseHelper().remove(lunarUser.getUserID(), new Date(lunarUser.getCreatedDate()));
                            lunarUser.setIsLogin(false);
                            getModel().saveUser(lunarUser);
                            ProfileActivity.this.finish();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}