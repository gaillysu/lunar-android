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
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.lunar.R;
import com.medcorp.lunar.base.BaseActivity;
import com.medcorp.lunar.cloud.med.MedNetworkOperation;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.network.listener.RequestResponseListener;
import com.medcorp.lunar.network.model.request.UpdateAccountInformationRequest;
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
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;

import static com.medcorp.lunar.R.style.AppTheme_Dark_Dialog;

/**
 * Created by med on 16/4/6.
 */
public class ProfileActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.profile_activity_select_picture)
    ImageView mImageButton;
    @Bind(R.id.profile_activity_edit_first_name)
    LinearLayout editFirstNameL;
    @Bind(R.id.profile_activity_edit_last_name)
    LinearLayout editLastName;
    @Bind(R.id.edit_user_birthday_pop)
    LinearLayout editUserBirthday;
    @Bind(R.id.edit_user_height_pop)
    LinearLayout editUserHeightL;
    @Bind(R.id.edit_user_weight_pop)
    LinearLayout editUserWeightL;

    private User user;
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
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.profile_title);
        user = getModel().getUser();
        if (getModel().getUser().isLogin()) {
            userEmail = user.getNevoUserEmail();
        } else {
            userEmail = getString(R.string.watch_med_profile);
        }
        Bitmap bt = BitmapFactory.decodeFile(Preferences.getUserHeardPicturePath(this, userEmail));//从Sd中找头像，转换成Bitmap
        if (bt != null) {
            mImageButton.setImageBitmap(PublicUtils.drawCircleView(bt));
        } else {
            mImageButton.setImageResource(R.drawable.user);
        }
        initView();
    }


    private void initView() {
        final TextView firstName = (TextView) findViewById(R.id.profile_fragment_user_first_name_tv);
        final TextView lastName = (TextView) findViewById(R.id.profile_fragment_user_last_name_tv);
        final TextView userBirthday = (TextView) findViewById(R.id.profile_fragment_user_birthday_tv);
        final TextView userHeight = (TextView) findViewById(R.id.profile_fragment_user_height_tv);
        final TextView userWeight = (TextView) findViewById(R.id.profile_fragment_user_weight_tv);

        firstName.setText(TextUtils.isEmpty(user.getFirstName()) ? getString(R.string.edit_user_first_name) : user.getFirstName());
        lastName.setText(TextUtils.isEmpty(user.getLastName()) ? getString(R.string.edit_user_last_name) : user.getLastName());
        //please strictly refer to our UI design Docs, the date format is dd,MMM,yyyy
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        userBirthday.setText(simpleDateFormat.format(new Date(user.getBirthday())));
        userHeight.setText(user.getHeight() + " cm");
        userWeight.setText(user.getWeight() + " kg");

        editLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName(lastName);
            }
        });

        editFirstNameL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName(firstName);
            }
        });

        editUserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserBirthday(userBirthday);
            }
        });

        editUserHeightL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserHeight(userHeight);
            }
        });

        editUserWeightL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserWeight(userWeight);
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
            hintName = user.getFirstName();
        } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
            content = getString(R.string.profile_fragment_input_user_surname_dialog_title);
            hintName = user.getLastName();
        }

        new MaterialDialog.Builder(this).title(getString(R.string.edit_profile)).content(content)
                .inputType(InputType.TYPE_CLASS_TEXT).input(getResources().getString(R.string.profile_fragment_edit_first_name_edit_hint),
                hintName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().length() > 0) {
                            nameText.setText(input.toString());
                            if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
                                user.setFirstName(input.toString());
                            } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
                                user.setLastName(input.toString());
                            }
                        }
                    }
                })
                .negativeText(R.string.notification_cancel).positiveText(getString(R.string.notification_ok)).show();

    }


    private void editUserWeight(final TextView userWeight) {
        viewType = 3;
        final DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userWeight.setText(dateDesc + " kg");
                        user.setWeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(user.getWeight() + "")
                .build();

        pickerPopWin3.showPopWin(this);
    }

    private void editUserHeight(final TextView userHeight) {

        viewType = 2;
        final DatePickerPopWin pickerPopWin2 = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userHeight.setText(dateDesc + " cm");
                        user.setHeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(user.getHeight() + "")
                .build();

        pickerPopWin2.showPopWin(this);
    }

    private void editUserBirthday(final TextView birthdayText) {
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
                            user.setBirthday(date.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).viewStyle(viewType)
                .viewTextSize(25) // pick view text size
                .minYear(Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date())) - 100) //min year in loop
                .maxYear(Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date())) + 1)
                .dateChose(new SimpleDateFormat("yyyy-MM-dd").format(new Date(user.getBirthday()))) // date chose when init popwindow
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
                startActivity(MainActivity.class);
                finish();
                overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
                break;
            case R.id.done_menu:
                progressDialog = new ProgressDialog(this, AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.network_wait_text));
                progressDialog.show();
                String format = new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthday());
                UpdateAccountInformationRequest request = new UpdateAccountInformationRequest(
                        new Integer(user.getNevoUserID()).intValue()
                        , user.getFirstName(), user.getNevoUserEmail(), user.getLastName(), format
                        , user.getHeight(), user.getWeight(), user.getSex());
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
                                        getModel().saveUser(user);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}