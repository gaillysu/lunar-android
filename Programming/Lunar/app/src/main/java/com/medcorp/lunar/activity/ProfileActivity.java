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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    @Bind(R.id.profile_user_gender_radio_group)
    RadioGroup profileGender;
    @Bind(R.id.profile_user_gender_female)
    RadioButton female;
    @Bind(R.id.profile_user_gender_male)
    RadioButton male;
    @Bind(R.id.profile_logout_bt)
    AppCompatButton logoutButton;
    @Bind(R.id.profile_delete_bt)
    AppCompatButton deleteProfile;
    @Bind(R.id.profile_fragment_user_user_email_tv)
    EditText userEmailTv;

    private User lunarUser;
    private int viewType;
    private String userEmail;
    private static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private ArrayList<String> mSelectPath;
    private ProgressDialog progressDialog;

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
        if (lunarUser.getSex() == 1) {
            male.setChecked(true);
        } else {
            female.setChecked(true);
        }
        if (lunarUser != null) {
            firstName.setText(TextUtils.isEmpty(lunarUser.getFirstName()) ? getString(R.string.edit_user_first_name) : lunarUser.getFirstName());
            lastName.setText(TextUtils.isEmpty(lunarUser.getLastName()) ? getString(R.string.edit_user_last_name) : lunarUser.getLastName());
            //please strictly refer to our UI design Docs, the date format is dd,MMM,yyyy
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            userBirthday.setText(dateFormat.format(new Date(lunarUser.getBirthday())));
            userHeight.setText(lunarUser.getHeight() + " cm");
            userWeight.setText(lunarUser.getWeight() + " kg");
        }

        profileGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.profile_user_gender_male) {
                    lunarUser.setSex(1);
                } else {
                    lunarUser.setSex(0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void editUserName(final TextView nameText) {

        String content = null;
        String hintName = null;
        if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
            content = getString(R.string.profile_input_user_first_name_dialog_title);
            hintName = lunarUser.getFirstName();
        } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
            content = getString(R.string.profile_fragment_input_user_surname_dialog_title);
            hintName = lunarUser.getLastName();
        }

        new MaterialDialog.Builder(this).title(getString(R.string.edit_profile)).content(content)
                .inputType(InputType.TYPE_CLASS_TEXT).input(getResources().getString(R.string.profile_fragment_edit_first_name_edit_hint),
                hintName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().length() > 0) {
                            nameText.setText(input.toString());
                            if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
                                lunarUser.setFirstName(input.toString());
                            } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
                                lunarUser.setLastName(input.toString());
                            }
                        }
                    }
                })
                .negativeText(R.string.notification_cancel).positiveText(getString(R.string.notification_ok)).show();

    }

    @OnClick(R.id.profile_activity_edit_first_name)
    public void editFirstName() {
        editUserName(firstName);
    }

    @OnClick(R.id.profile_activity_edit_last_name)
    public void editLastName() {
        editUserName(lastName);
    }

    @OnClick(R.id.profile_activity_edit_user_email)
    public void writeEmail() {
        String email = lunarUser.getUserEmail();
        new MaterialDialog.Builder(this).title(getString(R.string.edit_profile))
                .content(R.string.profile_input_user_email_dialog_title)
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .input(getResources().getString(R.string.profile_input_user_email_dialog_title),
                        email, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.toString().length() > 0) {
                                    if (Patterns.EMAIL_ADDRESS.matcher(input.toString()).matches()) {
                                        userEmailTv.setText(input.toString());
                                        lunarUser.setUserEmail(input.toString());
                                    } else {
                                        ToastHelper.showShortToast(ProfileActivity.this, getString(R.string.register_email_error));
                                    }
                                }
                            }
                        })
                .negativeText(R.string.notification_cancel).positiveText(getString(R.string.notification_ok)).show();
    }

    @OnClick(R.id.edit_user_weight_pop)
    public void editUserWeight(final TextView userWeight) {
        viewType = 3;
        final DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userWeight.setText(dateDesc + " kg");
                        lunarUser.setWeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(lunarUser.getWeight() + "")
                .build();

        pickerPopWin3.showPopWin(this);
    }

    @OnClick(R.id.edit_user_height_pop)
    public void editUserHeight(final TextView userHeight) {
        viewType = 2;
        final DatePickerPopWin pickerPopWin2 = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userHeight.setText(dateDesc + " cm");
                        lunarUser.setHeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(lunarUser.getHeight() + "")
                .build();

        pickerPopWin2.showPopWin(this);
    }

    @OnClick(R.id.edit_user_birthday_pop)
    public void editUserBirthday(final TextView birthdayText) {
        viewType = 1;
        final DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = dateFormat.parse(dateDesc);
                            birthdayText.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(date));
                            lunarUser.setBirthday(date.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).viewStyle(viewType)
                .viewTextSize(25) // pick view text size
                .minYear(Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date())) - 100) //min year in loop
                .maxYear(Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date())) + 1)
                .dateChose(new SimpleDateFormat("yyyy-MM-dd").format(new Date(lunarUser.getBirthday()))) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(this);
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